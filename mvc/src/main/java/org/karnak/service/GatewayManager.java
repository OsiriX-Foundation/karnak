package org.karnak.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.karnak.data.NodeEvent;
import org.karnak.service.pull.PullingService;
import org.karnak.util.NativeLibraryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.weasis.core.api.util.FileUtil;
import org.weasis.core.api.util.StringUtil;
import org.weasis.dicom.param.GatewayParams;
import org.weasis.dicom.param.ListenerParams;
import org.weasis.dicom.tool.DicomGateway;
import org.weasis.dicom.tool.DicomListener;

@DependsOn({ "GatewayPersistence" })
@Component
public class GatewayManager implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayManager.class);

    @Autowired
    private Environment environment;

    private NativeLibraryManager manager;
    private GatewayConfig globalConfig;

    private DicomGateway dicomForwardOut;

    private DicomListener dicomListenerOut;
    private PullingService httpPullIn;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        LOGGER.info("Application Event:" + event.toString());
    }

    @PostConstruct
    public void init() {
        LOGGER.info("{}", "Start the gateway manager running as a background process");
        try {
            URL resource = this.getClass().getResource("/lib");
            manager = new NativeLibraryManager(resource);
            globalConfig = new GatewayConfig(environment);
        } catch (Exception e1) {
            throw new IllegalStateException("Cannot register DICOM native librairies", e1);
        }
        initGateway();
    }

    @EventListener
    public void reloadOutboundNodes(NodeEvent event) {
        globalConfig.update(event);
    }

    private void initGateway() {
        dicomForwardOut = buildDicomGateway(globalConfig);
        // } else if (Mode.ARCHIVE.equals(outMode)) {
        // dicomListenerOut = buildDicomListener(configOut);
        // }

        // httpPullIn = new PullingService(configIn);
        // httpPullIn.start();

    }

    @PreDestroy
    public void destroy() {
        if (dicomListenerOut != null) {
            try {
                dicomListenerOut.stop();
            } catch (IOException e) {
                LOGGER.error("Cannot stop DICOM listener", e);
            }
        }
        if (dicomForwardOut != null) {
            try {
                dicomForwardOut.stop();
            } catch (IOException e) {
                LOGGER.error("Cannot stop gateway", e);
            }
        }
        if (httpPullIn != null) {
            httpPullIn.stop();
        }
        LOGGER.info("{}", "Gateway has been stopped");

        String dir = System.getProperty("dicom.native.codec");
        if (StringUtil.hasText(dir)) {
            FileUtil.delete(new File(dir));
        }
    }

    private static DicomGateway buildDicomGateway(GatewayConfig config) {
        DicomGateway gateway;
        try {
            String[] acceptedCallingAETitles = GatewayParams.getAcceptedCallingAETitles(config.getDestinations());
            GatewayParams gparams = new GatewayParams(config.getAdvancedParams(), false, null, acceptedCallingAETitles);
            gateway = new DicomGateway(config.getDestinations());
            gateway.start(config.getCallingDicomNode(), gparams);
            LOGGER.info("Karnak DICOM gateway servlet is running: {}", config);
            return gateway;
        } catch (Exception e) {
            LOGGER.error("Cannot start DICOM gateway", e);
            return null;
        }
    }

    private static DicomListener buildDicomListener(GatewayConfig config) {
        DicomListener dicomListener;
        try {
            dicomListener = new DicomListener(config.getStorePath());
            String[] acceptedCallingAETitles = GatewayParams.getAcceptedCallingAETitles(config.getDestinations());
            ListenerParams params = new ListenerParams(config.getAdvancedParams(), false, "{00020016}/{00020003}", null,
                acceptedCallingAETitles);
            dicomListener.start(config.getCallingDicomNode(), params);
            LOGGER.info("Gateway DICOM listener is running: {}", config);
            return dicomListener;
        } catch (Exception e) {
            LOGGER.error("Cannot start {}-stream DICOM listener", e);
            return null;
        }
    }
}
