package org.karnak.service;

import java.io.File;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.karnak.data.InputNodeEvent;
import org.karnak.data.OutputNodeEvent;
import org.karnak.service.GatewayConfig.Mode;
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

@DependsOn({ "InputNodes", "OutputNodes" })
@Component
public class GatewayManager implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayManager.class);

    @Autowired
    private Environment environment;

    private NativeLibraryManager manager;
    private GlobalConfig globalConfig;

    private DicomListener dicomListenerIn;
    private DicomListener dicomListenerOut;

    private DicomGateway dicomForwardIn;
    private DicomGateway dicomForwardOut;

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
            globalConfig = new GlobalConfig(environment);
        } catch (Exception e1) {
            throw new IllegalStateException("Cannot register DICOM native librairies", e1);
        }
        initGateway();
    }

    @EventListener
    public void reloadInboundNodes(InputNodeEvent event) {
       globalConfig.getConfigIn().update(event);
    }

    @EventListener
    public void reloadOutboundNodes(OutputNodeEvent event) {
        globalConfig.getConfigOut().update(event);
    }

    private void initGateway() {

        GatewayConfig configIn = globalConfig.getConfigIn();
        GatewayConfig configOut = globalConfig.getConfigOut();
        if (configIn == null || configOut == null) {
            throw new IllegalStateException("Configuration is null!");
        }

        Mode outMode = configOut.getMode();
        if (Mode.FORWARD.equals(outMode)) {
            dicomForwardOut = buildDicomGateway(configOut);
        } else if (Mode.ARCHIVE.equals(outMode)) {
            dicomListenerOut = buildDicomListener(configOut);
        }

        Mode inMode = configIn.getMode();
        if (Mode.FORWARD.equals(inMode)) {
            dicomForwardIn = buildDicomGateway(configIn);
        } else if (Mode.ARCHIVE.equals(inMode)) {
            dicomListenerIn = buildDicomListener(configIn);
        } else if (Mode.PULL.equals(inMode)) {
            httpPullIn = new PullingService(configIn);
            httpPullIn.start();
        }
    }

    @PreDestroy
    public void destroy() {
        if (dicomListenerOut != null) {
            dicomListenerOut.stop();
        }
        if (dicomForwardOut != null) {
            dicomForwardOut.stop();
        }
        if (dicomListenerIn != null) {
            dicomListenerIn.stop();
        }
        if (dicomForwardIn != null) {
            dicomForwardIn.stop();
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
            LOGGER.info("Karnak {}-stream gateway servlet is running: {}", config.getStream(), config);
            return gateway;
        } catch (Exception e) {
            LOGGER.error("Cannot start {}-stream gateway", config.getStream(), e);
            return null;
        }
    }

    private static DicomListener buildDicomListener(GatewayConfig config) {
        DicomListener dicomListener;
        try {
            dicomListener = new DicomListener(config.getStoreDir());
            String[] acceptedCallingAETitles = GatewayParams.getAcceptedCallingAETitles(config.getDestinations());
            ListenerParams params = new ListenerParams(config.getAdvancedParams(), false, "{00020016}/{00020003}", null,
                acceptedCallingAETitles);
            dicomListener.start(config.getCallingDicomNode(), params);
            LOGGER.info("Karnak {}-stream DICOM listener is running: {}", config.getStream(), config);
            return dicomListener;
        } catch (Exception e) {
            LOGGER.error("Cannot start {}-stream DICOM listener", e);
            return null;
        }
    }
}
