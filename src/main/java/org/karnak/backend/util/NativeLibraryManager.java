/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.weasis.core.util.FileUtil;
import org.weasis.opencv.natives.NativeLibrary;

@Slf4j
public class NativeLibraryManager {

	private NativeLibraryManager() {
	}

	public static void initNativeLibs() {
		String libPath = Arrays.stream(System.getProperty("java.library.path", "").split(File.pathSeparator))
			.filter(p -> p.contains("dicom-opencv"))
			.findFirst()
			.orElseThrow(() -> new IllegalStateException(
					"OpenCV library folder (a path containing \"dicom-opencv\") is not configured in java.library.path"));

		String system = NativeLibrary.getNativeLibSpecification();
		String filename = system.startsWith("win") ? "opencv_java.dll"
				: system.startsWith("mac") ? "libopencv_java.dylib" : "libopencv_java.so";
		Path outputFile = Path.of(libPath, filename);
		System.setProperty("dicom.native.codec", libPath);

		// When running from the build output (IntelliJ, mvn) the native library is on the
		// classpath under /lib/<system>/<filename> and gets copied next to java.library.path.
		// In the portable package it has already been extracted into the dicom-opencv folder
		// (and may have been stripped from the jar), so we just load whatever is there.
		String resourcePath = "/lib/" + system + "/" + filename;
		try (InputStream in = NativeLibraryManager.class.getResourceAsStream(resourcePath)) {
			if (in != null) {
				Files.createDirectories(outputFile.getParent());
				FileUtil.writeStream(in, outputFile, true);
			}
			else if (!Files.isReadable(outputFile)) {
				throw new IllegalStateException("Native OpenCV library is neither on the classpath (" + resourcePath
						+ ") nor already present at " + outputFile);
			}
		}
		catch (IOException e) {
			throw new IllegalStateException("Cannot copy the native OpenCV library to " + outputFile, e);
		}

		NativeLibrary.loadLibraryFromAbsolutePath(outputFile);
	}

}
