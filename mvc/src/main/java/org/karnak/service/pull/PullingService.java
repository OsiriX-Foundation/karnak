package org.karnak.service.pull;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.dcm4che6.net.DicomServiceException;
import org.dcm4che6.net.Status;
import org.karnak.data.FileInfo;
import org.karnak.service.AbstractGateway;
import org.karnak.service.GatewayConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.weasis.core.util.FileUtil;
import org.weasis.dicom.param.DicomForwardDestination;
import org.weasis.dicom.param.ForwardDestination;
import org.weasis.dicom.param.ForwardDicomNode;
import org.weasis.dicom.util.ForwardUtil;
import org.weasis.dicom.util.ForwardUtil.Params;

public final class PullingService extends AbstractGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(PullingService.class);

    private static final HostnameVerifier HostnameVerifier = (hostname, sslSession) -> {
        try {
            InetAddress host = InetAddress.getByName(hostname);
            InetAddress peerHost = InetAddress.getByName(sslSession.getPeerHost());
            if (host.equals(peerHost)) {
                return true;
            }
        } catch (UnknownHostException e) {
            LOGGER.error("Hostname verifier", e);
        }

        LOGGER.warn("No matching hostname for SSL:{} != {}", hostname, sslSession.getPeerHost());
        return false;
    };

    private final Map<String, List<FileInfo>> downloadMap = new HashMap<>();
    private SSLSocketFactory sslFactory;

    public PullingService(GatewayConfig config) {
        super(config);
        try (InputStream trustStore = PullingService.class.getResourceAsStream(config.getTruststore())) {
            /* Load the keyStore that includes self-signed cert as a "trusted" entry. */
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(trustStore, config.getTruststorePwd().toCharArray());
            trustStore.close();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, tmf.getTrustManagers(), null);
            this.sslFactory = ctx.getSocketFactory();
            HttpsURLConnection.setDefaultSSLSocketFactory(sslFactory);
        } catch (Exception e) {
            LOGGER.debug("Getting sslFactory", e);
        }
    }

    @Override
    protected void check() {
        if (Thread.interrupted()) {
            return;
        }
        try {
            buildArchiveList();
            downloadImageAndForward();

        } catch (Exception e) {
            long currentTime = System.currentTimeMillis();
            // Do not send more than one notification by two hours
            if ((currentTime - lastErrorNotification) > 7200000) {
                lastErrorNotification = currentTime;
                // With notifyAdmin marker the error should be sent by mail
                LOGGER.error(config.toString());
                LOGGER.error(notifyAdmin, "Unexepted error:{}", e);
            }
            LOGGER.error("Unexepted error:{}", e.getMessage());
            LOGGER.debug(e.getMessage(), e);
        }
        int count = iterationCount.increment();
        // Check every 1000 interval if files must be deleted
        if (count > 1000) {
            iterationCount.resetCount();
            AbstractGateway.deleteOldFiles(config.getStorePath().toFile());
        }
    }

    public void buildArchiveList() throws Exception {
        InputStream in = null;

        try {
            URL url = new URL(config.getArchiveUrl() + "/archive.xml");

            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            if (httpCon instanceof HttpsURLConnection) {
                HttpsURLConnection secured = (HttpsURLConnection) httpCon;
                secured.setHostnameVerifier(HostnameVerifier);
                secured.setSSLSocketFactory(sslFactory);
                // SSLSocket socket = (SSLSocket) secured.getSSLSocketFactory().createSocket(url.getHost(),
                // url.getPort());
                // // start handshake, workaround to fix unknown certificate issue
                // socket.startHandshake();
            }
            in = httpCon.getInputStream();
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            docBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(in);
            // normalize text representation
            doc.getDocumentElement().normalize();

            NodeList aetList = doc.getElementsByTagName("aet");

            for (int m = 0; m < aetList.getLength(); m++) {
                Node aetNode = aetList.item(m);
                if (aetNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element aetElement = (Element) aetNode;
                    String name = aetElement.getAttribute("name");
                    ArrayList<FileInfo> uidList = new ArrayList<>();

                    NodeList fileList = aetElement.getElementsByTagName("file");

                    for (int i = 0; i < fileList.getLength(); i++) {
                        Node fileNode = fileList.item(i);

                        if (fileNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element imgElement = (Element) aetNode;
                            NodeList sopList = fileNode.getChildNodes();
                            if (sopList.getLength() > 0) {
                                String filename = sopList.item(0).getNodeValue();
                                uidList.add(new FileInfo(filename, imgElement.getAttribute("iuid"),
                                    imgElement.getAttribute("cuid"), imgElement.getAttribute("tsuid")));
                            }
                        }
                    }
                    if (!uidList.isEmpty()) {
                        downloadMap.put(name, uidList);
                    }
                }
            }
        } finally {
            FileUtil.safeClose(in);
        }
    }

    private void downloadImageAndForward() throws Exception {
        for (Iterator<Entry<String, List<FileInfo>>> iter = downloadMap.entrySet().iterator(); iter.hasNext();) {
            Entry<String, List<FileInfo>> entry = iter.next();
            String name = entry.getKey();
            List<FileInfo> uidList = entry.getValue();
            if (uidList != null) {
                List<ForwardDestination> destinations = config.getDestination(name);
                if (destinations.isEmpty()) {
                    return;
                }

                for (int i = uidList.size() - 1; i >= 0; i--) {
                    if (Thread.interrupted()) {
                        return;
                    }
                    FileInfo info = uidList.get(i);
                    try {
                        StringBuilder buf = new StringBuilder(config.getArchiveUrl());
                        buf.append("/download?aet=");
                        buf.append(name);
                        buf.append("&sopuid=");
                        buf.append(info.getFilename());
                        URL url = new URL(buf.toString());
                        HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                        if (httpCon == null) {
                            continue;
                        }
                        // Connect to server.
                        httpCon.connect();
                        // Make sure response code is in the 200 range.
                        if (httpCon.getResponseCode() / 100 != 2) {
                            LOGGER.error("Http Response error {} for {}", httpCon.getResponseCode(), url); //$NON-NLS-1$
                            continue;
                        }

                        Params p = new Params(info.getIuid(), info.getCuid(), (byte) 0,
                            new BufferedInputStream(httpCon.getInputStream()), null);
                        ForwardDicomNode srcNode = new ForwardDicomNode(name);
                        store(destinations, srcNode, p, buf);

                    } catch (Exception e) {
                        // addToRetryList(info, name, uid, destinations);
                        LOGGER.error("Failed to download {}", info.getFilename());
                        LOGGER.debug(e.getMessage(), e);
                    } finally {
                        uidList.remove(i);
                    }
                }

            }
        }
    }

    private void store(List<ForwardDestination> destinations, ForwardDicomNode sourceNode, Params p,
        StringBuilder bufUrl) throws IOException {
        try {
            boolean deleteRemoteImage = false;
            // Accept only DICOM destination
            List<DicomForwardDestination> dicomDest =
                destinations.stream().filter(DicomForwardDestination.class::isInstance)
                    .map(DicomForwardDestination.class::cast).collect(Collectors.toList());
            if (dicomDest.size() > 1) {
                List<ForwardDestination> dcmList =
                    dicomDest.stream().map(ForwardDestination.class::cast).collect(Collectors.toList());
                ForwardUtil.storeMulitpleDestination(sourceNode, dcmList, p);

                deleteRemoteImage = true;
                for (DicomForwardDestination f : dicomDest) {
                    if (f.getStreamSCU().getState().getStatus().orElse(Status.Pending) != Status.Success) {
                        deleteRemoteImage = false;
                        break;
                    }
                }
            } else {
                DicomForwardDestination d = dicomDest.get(0);
                ForwardUtil.storeOneDestination(sourceNode, d, p);
                deleteRemoteImage = d.getStreamSCU().getState().getStatus().orElse(Status.Pending) == Status.Success;
            }

            if (deleteRemoteImage) {
                // entry.addFileInfo(download);
                // Confirm download is complete, image can be removed remotely.
                bufUrl.append("&delete=true");
                notifyToDeleteRemoteImage(new URL(bufUrl.toString()), p.getIuid());
            }

        } catch (Exception e) {
            throw new DicomServiceException(Status.ProcessingFailure);
        }
    }

    private void notifyToDeleteRemoteImage(URL url, String uid) {
        InputStream stream = null;
        if (url != null) {
            try {
                URLConnection conn = url.openConnection();
                stream = conn.getInputStream();
                LOGGER.debug("Notify to delete the remote image:{}", uid);

            } catch (Exception e) {
                LOGGER.error("Cannot notify to delete the remote image: " + uid, e);
            } finally {
                FileUtil.safeClose(stream);
            }
        }
    }

}
