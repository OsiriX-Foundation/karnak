package org.karnak.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.karnak.data.EmailNotifyProgress;
import org.karnak.data.NodeEventType;
import org.karnak.data.NotificationConfiguration;
import org.karnak.data.OutputNodeEvent;
import org.karnak.data.StreamRegistry;
import org.karnak.data.gateway.Destination;
import org.karnak.data.gateway.DestinationType;
import org.karnak.data.gateway.ForwardNode;
import org.karnak.data.gateway.GatewayPersistence;
import org.karnak.ui.gateway.GatewayConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.weasis.core.api.util.LangUtil;
import org.weasis.core.api.util.StringUtil;
import org.weasis.dicom.param.AdvancedParams;
import org.weasis.dicom.param.ConnectOptions;
import org.weasis.dicom.param.DicomForwardDestination;
import org.weasis.dicom.param.DicomNode;
import org.weasis.dicom.param.DicomProgress;
import org.weasis.dicom.param.ForwardDestination;
import org.weasis.dicom.param.ForwardDicomNode;
import org.weasis.dicom.param.TlsOptions;
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

    public enum Stream {
        IN, OUT
    }

    public enum Mode {
        FORWARD, ARCHIVE, PULL, DISABLE;

        public static Mode getMode(String str) {
            try {
                return valueOf(str.toUpperCase());
            } catch (Exception ex) {
                return DISABLE;
            }
        }
    }

    private final Map<ForwardDicomNode, List<ForwardDestination>> destMap = new HashMap<>();

    private final Stream stream;
    private final File storeDir;

    private final String listenerAET;
    private final int listenerPort;
    private final Boolean listenerTLS;
    private final int intervalCheck;

    private final Mode mode;
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

    private final Map<Long, Object> idMap = new HashMap<>();

    public GatewayConfig(Stream stream, Environment env) throws Exception {
        this.stream = stream;

        String storePath = getProperty("_GATEWAY_ARCHIVE_PATH", null); // Only Archive and Pull mode
        storeDir = StringUtil.hasText(storePath) ? new File(storePath) : null;
        intervalCheck = StringUtil.getInt(getProperty("_GATEWAY_PULL_CHECK_INTERNAL", "5")); // Only Pull mode
        archiveUrl = getProperty("_GATEWAY_ARCHIVE_URL", ""); // Only Archive mode

        // Default value is disable (in case the properties file does not exist)
        mode = Mode.getMode(getProperty("_GATEWAY_MODE", "disable"));

        listenerAET = getProperty("_DICOM_LISTENER_AET", "KARNAK-" + stream.name().toUpperCase());
        listenerPort = stream == Stream.IN ? 11115 : 11119;
        listenerTLS = LangUtil.getEmptytoFalse(getProperty("_DICOM_LISTENER_TLS", null));

        clientKey = getProperty("_TLS_KEYSTORE_PATH", null);
        clientKeyPwd = getProperty("_TLS_KEYSTORE_SECRET", null);
        truststorePwd = getProperty("_TLS_TRUSTSTORE_PATH", null);
        truststore = getProperty("_TLS_TRUSTSTORE_SECRET", null);

        smtpHost = getProperty("_MAIL_SMTP_HOST", null);
        smtpPort = getProperty("_MAIL_SMTP_PORT", null);
        mailAuthType = getProperty("_MAIL_SMTP_TYPE", null);
        mailAuthUser = getProperty("_MAIL_SMTP_USER", null);
        mailAuthPwd = getProperty("_MAIL_SMTP_SECRET", null);

        String notifyObjectErrorPrefix = getProperty("_NOTIFY_OBJECT_ERROR_PREFIX", "**ERROR**");
        String notifyObjectPattern = getProperty("_NOTIFY_OBJECT_PATTERN", "[Karnak Notification] %s %.30s");
        String[] notifyObjectValues = getProperty("_NOTIFY_OBJECT_VALUES", "PatientID,StudyDescription").split(",");
        int notifyInterval = StringUtil.getInt(getProperty("_NOTIFY_INTERNAL", "45"));
        this.notifConfiguration = new NotificationConfiguration(notifyObjectErrorPrefix, notifyObjectPattern,
            notifyObjectValues, notifyInterval);

        reloadNodesConfiguration();
    }

    private String getProperty(String key, String defaultValue) {
        String k = stream.name() + key;
        String val = System.getProperty(k);
        if (!StringUtil.hasText(val)) {
            val = System.getenv(k);
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
        buf.append(" Mode=");
        buf.append(mode);
        return buf.toString();
    }

    public Stream getStream() {
        return stream;
    }

    public String getArchiveUrl() {
        return archiveUrl;
    }

    public Mode getMode() {
        return mode;
    }

    public File getStoreDir() {
        return storeDir;
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

    public void reloadNodesConfiguration() {
            reloadGatewayPersistence();
    }

    private void addDestinationNode(List<ForwardDestination> dstList, ForwardDicomNode fwdSrcNode,
        Destination dstNode) {
        try {
            DicomProgress progress = new DicomProgress();
            StreamRegistry streamRegistry = new StreamRegistry();
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
                HashMap<String, String> map = new HashMap<>();
                for (String h : dstNode.getHeaders().split(";")) {
                    // map.put(key, value);
                }
                WebForwardDestination fwd =
                    new WebForwardDestination(fwdSrcNode, dstNode.getUrl(), map, progress, streamRegistry);
                progress.addProgressListener(new EmailNotifyProgress(streamRegistry, fwd, emails, this, notifConfig));
                dstList.add(fwd);
            } else {
                DicomNode destinationNode =
                    new DicomNode(dstNode.getAeTitle(), dstNode.getHostname(), dstNode.getPort());
                DicomForwardDestination dest = new DicomForwardDestination(getDefaultAdvancedParameters(), fwdSrcNode,
                    destinationNode, dstNode.getUseaetdest(), progress, streamRegistry);
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
            ForwardDicomNode fwdSrcNode = new ForwardDicomNode(forwardNode.getFwdAeTitle());
            for (org.karnak.data.gateway.SourceNode srcNode : forwardNode.getSourceNodes()) {
                fwdSrcNode.addAcceptedSourceNode(srcNode.getAeTitle(), srcNode.getHostname());
            }

            List<ForwardDestination> dstList = new ArrayList<>(forwardNode.getDestinations().size());
            for (Destination dstNode : forwardNode.getDestinations()) {
                addDestinationNode(dstList, fwdSrcNode, dstNode);
            }
            idMap.put(forwardNode.getId(), fwdSrcNode);
            destMap.put(fwdSrcNode, dstList);
        }
    }

    public void update(OutputNodeEvent event) {
        NodeEventType type = event.getEventType();
        Object src = event.getSource();
        ForwardDicomNode fwdNode = getForwardDicomNode(event.getForwardNode().getFwdAeTitle());
        idMap.put(event.getForwardNode().getId(), fwdNode);
        if (src instanceof org.karnak.data.gateway.SourceNode) {
            org.karnak.data.gateway.SourceNode srcNode = (org.karnak.data.gateway.SourceNode) src;
            if (type == NodeEventType.ADD) {
                fwdNode.addAcceptedSourceNode(srcNode.getAeTitle(), srcNode.getHostname());
            } else if (type == NodeEventType.REMOVE) {
                DicomNode node = new DicomNode(srcNode.getAeTitle(), srcNode.getHostname(), null);
                fwdNode.getAcceptedSourceNodes().removeIf(n -> n.equals(node));
            } else if (type == NodeEventType.UPDATE) {

            }
        } else if (src instanceof org.karnak.data.gateway.Destination) {
            org.karnak.data.gateway.Destination dstNode = (org.karnak.data.gateway.Destination) src;
            if (type == NodeEventType.ADD) {
                addDestinationNode(destMap.get(fwdNode), fwdNode, dstNode);
            } else if (type == NodeEventType.REMOVE) {
                if (dstNode.getType() == DestinationType.stow) {
                    destMap.get(fwdNode).removeIf(d -> (d instanceof WebForwardDestination)
                        && ((WebForwardDestination) d).getRequestURL() == dstNode.getUrl());
                } else {
                    DicomNode node = new DicomNode(dstNode.getAeTitle(), dstNode.getHostname(), dstNode.getPort());
                    // destMap.get(fwdNode).removeIf(d -> d.getForwardDicomNode());
                }
            } else if (type == NodeEventType.UPDATE) {

            }
        } else if (src instanceof ForwardNode) {
            ForwardNode fw = (ForwardNode) src;
            if (type == NodeEventType.ADD) {
                addAcceptedSourceNodes(fwdNode, fw);
                destMap.put(fwdNode, addDestinationNodes(fwdNode, fw));
            } else if (type == NodeEventType.REMOVE) {
                destMap.remove(fwdNode);
            } else if (type == NodeEventType.UPDATE) {
                // TODO remove old aet
            }
        } else {
            reloadGatewayPersistence();
        }
    }

    private ForwardDicomNode getForwardDicomNode(String aet) {
        Optional<ForwardDicomNode> val = destMap.keySet().stream().filter(f -> f.getAet().equals(aet)).findFirst();
        ForwardDicomNode fwdNode;
        if (val.isEmpty()) {
            fwdNode = new ForwardDicomNode(aet);
            destMap.put(fwdNode, new ArrayList<>(2));
        } else {
            fwdNode = val.get();
        }
        return fwdNode;
    }

    private void addAcceptedSourceNodes(ForwardDicomNode fwdSrcNode, ForwardNode forwardNode) {
        for (org.karnak.data.gateway.SourceNode srcNode : forwardNode.getSourceNodes()) {
            fwdSrcNode.addAcceptedSourceNode(srcNode.getAeTitle(), srcNode.getHostname());
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
