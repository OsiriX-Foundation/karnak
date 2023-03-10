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
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.opencv.osgi.OpenCVNativeLoader;
import org.weasis.core.util.FileUtil;
import org.weasis.core.util.NativeLibrary;

@Slf4j
public class NativeLibraryManager {

	private NativeLibraryManager() {
	}

	public static void initNativeLibs(URL resource) {
		Optional<String> oLibPath = Arrays.stream(System.getProperty("java.library.path").split(File.pathSeparator))
				.filter(p -> p.contains("dicom-opencv")).findFirst();
		if (oLibPath.isEmpty()) {
			throw new IllegalStateException("OpenCV library is not configured in java.library.path");
		}

		String system = NativeLibrary.getNativeLibSpecification();
		String filename = system.startsWith("win") ? "opencv_java.dll"
				: system.startsWith("mac") ? "libopencv_java.dylib" : "libopencv_java.so";
		Path outputFile = Path.of(oLibPath.get(), filename);
		System.setProperty("dicom.native.codec", oLibPath.get());

		try {
			Files.createDirectories(outputFile.getParent());
			String path = resource.toString() + "/" + system + "/" + filename;
			FileUtil.writeStream(new URL(path).openStream(), outputFile, true);
		}
		catch (IOException e) {
			log.error("copy native libs", e);
		}

		OpenCVNativeLoader loader = new OpenCVNativeLoader();
		loader.init();
	}

}
