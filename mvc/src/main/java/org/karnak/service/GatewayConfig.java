package org.karnak.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.dcm4che6.data.DicomObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.karnak.data.*;
import org.karnak.data.gateway.*;
import org.karnak.data.gateway.Destination;
import org.karnak.data.gateway.DestinationType;
import org.karnak.data.gateway.DicomSourceNode;
import org.karnak.data.gateway.ForwardNode;
import org.karnak.data.gateway.GatewayPersistence;
import org.karnak.kheops.SwitchingAlbum;
import org.karnak.ui.data.GatewayConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.weasis.core.util.LangUtil;
import org.weasis.core.util.StringUtil;
import org.weasis.dicom.param.*;
import org.weasis.dicom.web.WebForwardDestination;

public class GatewayConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayConfig.class);

    protected static final String T_NODES = "forwardNodes";
    protected static final String T_NODE = "forwardNode";
    protected static final String T_SRCNODES = "sourceNodes";
    protected static final String T_SRCNODE = "sourceNode";
    protected static final String T_DESTINATIONS = "destinations";
    protected static final String T_DESTINATION = "destination";
    protected static final String T_HEADERS = "headers";
    protected static final String T_HEADER = "header";
    protected static final String T_KEY = "key";
    protected static final String T_VALUE = "value";

    protected static final String T_FWD_AET = "fwdAeTitle";
    protected static final String T_AETITLE = "aeTitle";
    protected static final String T_HOST = "hostname";
    protected static final String T_PORT = "port";
    protected static final String T_TYPE = "type";

    protected static final String T_URL = "url";

    private final Map<ForwardDicomNode, List<ForwardDestination>> destMap = new HashMap<>();

    private final Path storePath;

    private final String listenerAET;
    private final int listenerPort;
    private final Boolean listenerTLS;
    private final int intervalCheck;

    private final String archiveUrl;
    private final String smtpHost;
    private final String mailAuthType;
    private final String mailAuthUser;
    private final String mailAuthPwd;
    private final String smtpPort;

    private final NotificationConfiguration notifConfiguration;

    private String clientKey;
    private String clientKeyPwd;
    private String truststorePwd;
    private String truststore;

    public GatewayConfig(Environment env) throws Exception {
        String path = getProperty("GATEWAY_ARCHIVE_PATH", null); // Only Archive and Pull mode
        storePath = StringUtil.hasText(path) ? Path.of(path) : null;
        intervalCheck = StringUtil.getInt(getProperty("GATEWAY_PULL_CHECK_INTERNAL", "5")); // Only Pull mode
        archiveUrl = getProperty("GATEWAY_ARCHIVE_URL", ""); // Only Archive mode

        listenerAET = getProperty("DICOM_LISTENER_AET", "KARNAK-GATEWAY");
        listenerPort = 11119;
        listenerTLS = LangUtil.getEmptytoFalse(getProperty("DICOM_LISTENER_TLS", null));

        clientKey = getProperty("TLS_KEYSTORE_PATH", null);
        clientKeyPwd = getProperty("TLS_KEYSTORE_SECRET", null);
        truststorePwd = getProperty("TLS_TRUSTSTORE_PATH", null);
        truststore = getProperty("TLS_TRUSTSTORE_SECRET", null);

        smtpHost = getProperty("MAIL_SMTP_HOST", null);
        smtpPort = getProperty("MAIL_SMTP_PORT", null);
        mailAuthType = getProperty("MAIL_SMTP_TYPE", null);
        mailAuthUser = getProperty("MAIL_SMTP_USER", null);
        mailAuthPwd = getProperty("MAIL_SMTP_SECRET", null);

        String notifyObjectErrorPrefix = getProperty("NOTIFY_OBJECT_ERROR_PREFIX", "**ERROR**");
        String notifyObjectPattern = getProperty("NOTIFY_OBJECT_PATTERN", "[Karnak Notification] %s %.30s");
        String[] notifyObjectValues = getProperty("NOTIFY_OBJECT_VALUES", "PatientID,StudyDescription").split(",");
        int notifyInterval = StringUtil.getInt(getProperty("NOTIFY_INTERNAL", "45"));
        this.notifConfiguration = new NotificationConfiguration(notifyObjectErrorPrefix, notifyObjectPattern,
                notifyObjectValues, notifyInterval);

        reloadGatewayPersistence();
    }

    private String getProperty(String key, String defaultValue) {
        String val = System.getProperty(key);
        if (!StringUtil.hasText(val)) {
            val = System.getenv(key);
            if (!StringUtil.hasText(val)) {
                return defaultValue;
            }
        }
        return val;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("Hostname=");
        buf.append(DicomNode.convertToIP(null));
        buf.append(" AETitle=");
        buf.append(listenerAET);
        buf.append(" Port=");
        buf.append(listenerPort);
        return buf.toString();
    }

    public String getArchiveUrl() {
        return archiveUrl;
    }

    public Path getStorePath() {
        return storePath;
    }

    public String getListenerAET() {
        return listenerAET;
    }

    public int getListenerPort() {
        return listenerPort;
    }

    public Boolean getListenerTLS() {
        return listenerTLS;
    }

    public int getIntervalCheck() {
        return intervalCheck;
    }

    public String getClientKey() {
        return clientKey;
    }

    public String getClientKeyPwd() {
        return clientKeyPwd;
    }

    public String getTruststorePwd() {
        return truststorePwd;
    }

    public String getTruststore() {
        return truststore;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public String getMailAuthType() {
        return mailAuthType;
    }

    public String getMailAuthUser() {
        return mailAuthUser;
    }

    public String getMailAuthPwd() {
        return mailAuthPwd;
    }

    public String getSmtpPort() {
        return smtpPort;
    }

    public NotificationConfiguration getNotifConfiguration() {
        return notifConfiguration;
    }

    public AdvancedParams getAdvancedParams() {
        AdvancedParams options = new AdvancedParams();
        ConnectOptions connectOptions = new ConnectOptions();
        connectOptions.setConnectTimeout(3000);
        connectOptions.setAcceptTimeout(5000);
        // Concurrent DICOM operations
        connectOptions.setMaxOpsInvoked(15);
        connectOptions.setMaxOpsPerformed(15);
        if (getListenerTLS()) {
            TlsOptions tls = new TlsOptions(false, getClientKey(), "JKS", getClientKeyPwd(), getClientKeyPwd(),
                    getTruststore(), "JKS", getTruststorePwd());
            options.setTlsOptions(tls);
        }
        options.setConnectOptions(connectOptions);
        return options;
    }

    public DicomNode getCallingDicomNode() {
        return new DicomNode(getListenerAET(), null, getListenerPort());
    }

    public Optional<ForwardDicomNode> getDestinationNode(String fwdAET) {
        return destMap.keySet().stream().filter(n -> n.getForwardAETitle().equals(fwdAET)).findFirst();
    }

    public List<ForwardDestination> getDestination(String fwdAET) {
        Optional<ForwardDicomNode> node = getDestinationNode(fwdAET);
        if (node.isPresent()) {
            getDestinations(node.get());
        }
        return Collections.emptyList();
    }

    public List<ForwardDestination> getDestinations(ForwardDicomNode fwdSrcNode) {
        List<ForwardDestination> destList = null;
        if (fwdSrcNode != null) {
            destList = destMap.get(fwdSrcNode);
        }
        if (destList == null) {
            return Collections.emptyList();
        }
        return destList;
    }

    public Map<ForwardDicomNode, List<ForwardDestination>> getDestinations() {
        return destMap;
    }

    public Set<ForwardDicomNode> getKeys() {
        return destMap.keySet();
    }

    private void addDestinationNode(List<ForwardDestination> dstList, ForwardDicomNode fwdSrcNode,
                                    Destination dstNode) {
        try {
            List<AttributeEditor> editors = new ArrayList<>();
            final boolean filterBySOPClassesEnable = dstNode.getFilterBySOPClasses();
            if(filterBySOPClassesEnable) {
                editors.add(new FilterEditor(dstNode.getSOPClassUIDFilters()));
            }

            final List<KheopsAlbums> listKheopsAlbums = dstNode.getKheopsAlbums();
            SwitchingAlbum switchingAlbum = new SwitchingAlbum();
            editors.add((DicomObject dcm, AttributeEditorContext context) -> {
                if (listKheopsAlbums != null) {
                    listKheopsAlbums.forEach(kheopsAlbums -> {
                        switchingAlbum.apply(dstNode, kheopsAlbums, dcm);
                    });
                }
            });

            final boolean desidentificationEnable = dstNode.getDesidentification();
            final boolean profileDefined = dstNode.getProject() != null && dstNode.getProject().getProfile() != null;
            if(desidentificationEnable && profileDefined){ //TODO add an option in destination model
                editors.add(new DeidentifyEditor(dstNode));
            }

            DicomProgress progress = new DicomProgress();
            StreamRegistry streamRegistry = new StreamRegistry();
            editors.add(streamRegistry);
            String[] emails = dstNode.getNotify().split(",");
            NotificationConfiguration notifConfig = null;
            String notifyObjectErrorPrefix = dstNode.getNotifyObjectErrorPrefix();
            String notifyObjectPattern = dstNode.getNotifyObjectPattern();
            String[] notifyObjectValues = dstNode.getNotifyObjectValues().split(",");
            Integer notifyInterval = dstNode.getNotifyInterval();

            if (notifyObjectErrorPrefix != null || notifyObjectPattern != null || notifyObjectValues != null
                    || notifyInterval != null) {
                if (notifyObjectErrorPrefix == null) {
                    notifyObjectErrorPrefix = getNotifConfiguration().getNotifyObjectErrorPrefix();
                }
                if (notifyObjectPattern == null) {
                    notifyObjectPattern = getNotifConfiguration().getNotifyObjectPattern();
                }
                if (notifyObjectValues == null) {
                    notifyObjectValues = getNotifConfiguration().getNotifyObjectValues();
                }
                if (notifyInterval == null || notifyInterval <= 0) {
                    notifyInterval = getNotifConfiguration().getNotifyInterval();
                }
                notifConfig = new NotificationConfiguration(notifyObjectErrorPrefix, notifyObjectPattern,
                        notifyObjectValues, notifyInterval);
            }

            if (dstNode.getType() == DestinationType.stow) {
                // parse headers to hashmap
                HashMap<String, String> map = new HashMap<>();
                String headers = dstNode.getHeaders();
                Document doc = Jsoup.parse(headers);
                String key = doc.getElementsByTag("key").text();
                String value = doc.getElementsByTag("value").text();
                if (StringUtil.hasText(key)) {
                    map.put(key, value);
                }

                WebForwardDestination fwd = new WebForwardDestination(dstNode.getId(), fwdSrcNode, dstNode.getUrl(),
                        map, progress, editors);
                progress.addProgressListener(new EmailNotifyProgress(streamRegistry, fwd, emails, this, notifConfig));
                progress.addProgressListener((DicomProgress dicomProgress) -> {
                    DicomObject dcm = dicomProgress.getAttributes();
                    if (listKheopsAlbums != null) {
                        listKheopsAlbums.forEach(kheopsAlbums -> {
                            switchingAlbum.applyAfterTransfer(kheopsAlbums, dcm);
                        });
                    }
                });
                dstList.add(fwd);
            } else {
                DicomNode destinationNode =
                        new DicomNode(dstNode.getAeTitle(), dstNode.getHostname(), dstNode.getPort());
                DicomForwardDestination dest =
                        new DicomForwardDestination(dstNode.getId(), getDefaultAdvancedParameters(), fwdSrcNode,
                                destinationNode, dstNode.getUseaetdest(), progress, editors);
                progress.addProgressListener(new EmailNotifyProgress(streamRegistry, dest, emails, this, notifConfig));
                dstList.add(dest);
            }
        } catch (IOException e) {
            LOGGER.error("Cannot build ForwardDestination", e);
        }
    }

    private static AdvancedParams getDefaultAdvancedParameters() {
        AdvancedParams params = new AdvancedParams();
        ConnectOptions connectOptions = new ConnectOptions();
        connectOptions.setConnectTimeout(3000);
        connectOptions.setAcceptTimeout(5000);
        // Concurrent DICOM operations
        connectOptions.setMaxOpsInvoked(3);
        connectOptions.setMaxOpsPerformed(3);
        params.setConnectOptions(connectOptions);
        return params;
    }

    public void reloadGatewayPersistence() {
        GatewayPersistence gatewayPersistence = GatewayConfiguration.getInstance().getGatewayPersistence();
        List<ForwardNode> list = new ArrayList<>(gatewayPersistence.findAll());
        for (ForwardNode forwardNode : list) {
            ForwardDicomNode fwdSrcNode = new ForwardDicomNode(forwardNode.getFwdAeTitle(), null, forwardNode.getId());
            addAcceptedSourceNodes(fwdSrcNode, forwardNode);
            List<ForwardDestination> dstList = new ArrayList<>(forwardNode.getDestinations().size());
            for (Destination dstNode : forwardNode.getDestinations()) {
                addDestinationNode(dstList, fwdSrcNode, dstNode);
            }
            destMap.put(fwdSrcNode, dstList);
        }
    }

    public void update(NodeEvent event) {
        NodeEventType type = event.getEventType();
        Object src = event.getSource();
        Long id = event.getForwardNode().getId();
        String aet = event.getForwardNode().getFwdAeTitle();
        Optional<ForwardDicomNode> val = destMap.keySet().stream().filter(f -> id.equals(f.getId())).findFirst();
        ForwardDicomNode fwdNode;
        if (val.isEmpty()) {
            fwdNode = new ForwardDicomNode(aet, null, id);
            destMap.put(fwdNode, new ArrayList<>(2));
        } else {
            fwdNode = val.get();
        }
        if (src instanceof DicomSourceNode) {
            DicomSourceNode srcNode = (DicomSourceNode) src;
            if (type == NodeEventType.ADD) {
                fwdNode.addAcceptedSourceNode(srcNode.getId(), srcNode.getAeTitle(), srcNode.getHostname());
            } else if (type == NodeEventType.REMOVE) {
                fwdNode.getAcceptedSourceNodes().removeIf(s -> srcNode.getId().equals(s.getId()));
            } else if (type == NodeEventType.UPDATE) {
                fwdNode.getAcceptedSourceNodes().removeIf(s -> srcNode.getId().equals(s.getId()));
                fwdNode.addAcceptedSourceNode(srcNode.getId(), srcNode.getAeTitle(), srcNode.getHostname());
            }
        } else if (src instanceof Destination) {
            Destination dstNode = (Destination) src;
            if (type == NodeEventType.ADD) {
                addDestinationNode(destMap.get(fwdNode), fwdNode, dstNode);
            } else if (type == NodeEventType.REMOVE) {
                destMap.get(fwdNode).removeIf(d -> dstNode.getId().equals(d.getId()));
            } else if (type == NodeEventType.UPDATE) {
                destMap.get(fwdNode).removeIf(d -> dstNode.getId().equals(d.getId()));
                addDestinationNode(destMap.get(fwdNode), fwdNode, dstNode);
            }
        } else if (src instanceof ForwardNode) {
            ForwardNode fw = (ForwardNode) src;
            if (type == NodeEventType.ADD) {
                addAcceptedSourceNodes(fwdNode, fw);
                destMap.put(fwdNode, addDestinationNodes(fwdNode, fw));
            } else if (type == NodeEventType.REMOVE) {
                destMap.remove(fwdNode);
            } else if (type == NodeEventType.UPDATE) {
                if (!aet.equals(fwdNode.getAet())) {
                    ForwardDicomNode newfwdNode = new ForwardDicomNode(aet, null, id);
                    for (DicomNode srcNode : fwdNode.getAcceptedSourceNodes()) {
                        newfwdNode.getAcceptedSourceNodes().add(srcNode);
                    }
                    destMap.put(newfwdNode, destMap.remove(fwdNode));
                }
            }
        } else {
            reloadGatewayPersistence();
        }
    }

    private void addAcceptedSourceNodes(ForwardDicomNode fwdSrcNode, ForwardNode forwardNode) {
        for (DicomSourceNode srcNode : forwardNode.getSourceNodes()) {
            fwdSrcNode.addAcceptedSourceNode(srcNode.getId(), srcNode.getAeTitle(), srcNode.getHostname());
        }
    }

    private List<ForwardDestination> addDestinationNodes(ForwardDicomNode fwdSrcNode, ForwardNode forwardNode) {
        List<ForwardDestination> dstList = new ArrayList<>(forwardNode.getDestinations().size());
        for (Destination dstNode : forwardNode.getDestinations()) {
            addDestinationNode(dstList, fwdSrcNode, dstNode);
        }
        return dstList;
    }

}
