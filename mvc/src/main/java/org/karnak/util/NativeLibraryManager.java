package org.karnak.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

import org.dcm4che6.img.util.FileUtil;
import org.opencv.osgi.OpenCVNativeLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weasis.core.util.StringUtil;

public class NativeLibraryManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(NativeLibraryManager.class);

    public NativeLibraryManager(URL resource) {
        initNativeLibs(resource);
    }

    public static String getSystemSpecification() {
        // Follows the OSGI specification to use Bundle-NativeCode in the bundle fragment :
        // http://www.osgi.org/Specifications/Reference
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");
        if (StringUtil.hasText(osName) && StringUtil.hasText(osArch)) {
            if (osName.toLowerCase().startsWith("win")) {
                // All Windows versions with a specific processor architecture (x86 or x86-64) are grouped under
                // windows. If you need to make different native libraries for the Windows versions, define it in the
                // Bundle-NativeCode tag of the bundle fragment.
                osName = "windows";
            } else if (osName.equals("Mac OS X")) {
                osName = "macosx";
            } else if (osName.equals("SymbianOS")) {
                osName = "epoc32";
            } else if (osName.equals("hp-ux")) {
                osName = "hpux";
            } else if (osName.equals("Mac OS")) {
                osName = "macos";
            } else if (osName.equals("OS/2")) {
                osName = "os2";
            } else if (osName.equals("procnto")) {
                osName = "qnx";
            } else {
                osName = osName.toLowerCase();
            }

            if (osArch.equals("pentium") || osArch.equals("i386") || osArch.equals("i486") || osArch.equals("i586")
                || osArch.equals("i686")) {
                osArch = "x86";
            } else if (osArch.equals("amd64") || osArch.equals("em64t") || osArch.equals("x86_64")) {
                osArch = "x86-64";
            } else if (osArch.equals("power ppc")) {
                osArch = "powerpc";
            } else if (osArch.equals("psc1k")) {
                osArch = "ignite";
            } else {
                osArch = osArch.toLowerCase();
            }
            return osName + "-" + osArch;
        }
        throw new IllegalStateException("Cannot determine OS specification");
    }

    private void initNativeLibs(URL resource) {
        Optional<String> oLibPath = Arrays.stream(System.getProperty("java.library.path").split(File.pathSeparator))
            .filter(p -> p.contains("dicom-opencv")).findFirst();
        if(oLibPath.isEmpty()) {
            throw new IllegalStateException("OpenCV library is not configured in java.library.path");
        }

        String system = NativeLibraryManager.getSystemSpecification();
        String filename = system.startsWith("win") ? "opencv_java.dll"
            : system.startsWith("mac") ? "libopencv_java.jnilib" : "libopencv_java.so";
        Path outputFile = Path.of(oLibPath.get(), filename);
        System.setProperty("dicom.native.codec", oLibPath.get());

        try {
            Files.createDirectories(outputFile.getParent());
            String path = resource.toString() + "/" + system + "/" + filename;
            FileUtil.writeStream(new URL(path).openStream(), outputFile, true);
        } catch (IOException e) {
            LOGGER.error("copy native libs", e);
        }

        OpenCVNativeLoader loader = new OpenCVNativeLoader();
        loader.init();
    }
}
