package org.karnak.util;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.opencv.core.Core;
import org.opencv.osgi.OpenCVNativeLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weasis.core.api.util.FileUtil;
import org.weasis.core.api.util.StringUtil;

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
        String tempDir = System.getProperty("java.io.tmpdir");
        File dir;
        String folder = "dicom-native-codec-" + Core.VERSION;
        if (tempDir == null || tempDir.length() == 1) {
            dir = new File(System.getProperty("user.home", ""), folder);
        } else {
            dir = new File(tempDir, folder);
        }
        dir.mkdirs();

        String system =  NativeLibraryManager.getSystemSpecification();
        String filename =  system.startsWith("win") ? "opencv_java.dll" : system.startsWith("mac") ? "libopencv_java.jnilib":"libopencv_java.so";
        File outputFile = new File(dir, filename);
        
        System.setProperty("dicom.native.codec", outputFile.getParent());

        try {
            String path = resource.toString() + "/" + system + "/" + filename;
            FileUtil.writeStream(new URL(path).openStream(), outputFile);
        } catch (IOException e) {
            LOGGER.error("copy native libs", e);
        }

        String path = dir.getPath();
        String oldSysPaths = System.getProperty("java.library.path");
        if (StringUtil.hasText(oldSysPaths)) {
            path = oldSysPaths + File.pathSeparator + path;
        }

        System.setProperty("java.library.path", path);

        try {
            Lookup cl = MethodHandles.privateLookupIn(ClassLoader.class, MethodHandles.lookup());
            VarHandle sys_paths = cl.findStaticVarHandle(ClassLoader.class, "sys_paths", String[].class);
            sys_paths.set(null);
            // Trick to reload JMV setting for "java.library.path"
//            sysPath = ClassLoader.class.getDeclaredField("sys_paths");
//            sysPath.setAccessible(true);
//            sysPath.set(null, null);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            LOGGER.error("Cannot reload java.library.path", e);
        }

        OpenCVNativeLoader loader = new OpenCVNativeLoader();
        loader.init();
    }
}
