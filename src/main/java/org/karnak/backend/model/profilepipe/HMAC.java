/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.model.profilepipe;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.weasis.core.util.StringUtil;

@Slf4j
public class HMAC {

	public static final int KEY_BYTE_LENGTH = 16;

	private static final String HMAC_SHA256 = "HmacSHA256";

	private static final String HEX_DIGITS = "0123456789abcdef";

	// Pre-computed lookup table for hex conversion (more efficient than array access)
	private static final char[] HEX_LOOKUP = HEX_DIGITS.toCharArray();

	private Mac mac;

	private HashContext hashContext;

	public HMAC(byte[] hmacKey) {
		initHMAC(hmacKey);
	}

	public HMAC(HashContext hashContext) {
		this.hashContext = hashContext;
		initHMAC(hashContext.getSecret());
	}

	/*
	 * Generate a random secret key of 32bytes
	 */
	public static byte[] generateRandomKey() {
		SecureRandom random = new SecureRandom();
		byte[] bytes = new byte[KEY_BYTE_LENGTH];
		random.nextBytes(bytes);
		return bytes;
	}

	public static String byteToHex(byte[] byteArray) {
		Objects.requireNonNull(byteArray, "Byte array cannot be null");
		final char[] hexChars = new char[byteArray.length * 2];
		for (int i = 0; i < byteArray.length; i++) {
			int byteValue = byteArray[i] & 0xFF;
			// Extract the upper 4 bits and lower 4 bits
			hexChars[i * 2] = HEX_LOOKUP[byteValue >>> 4];
			hexChars[i * 2 + 1] = HEX_LOOKUP[byteValue & 0x0F];
		}
		return new String(hexChars);
	}

	public static String showHexKey(String key) {
		return String.format("%s-%s-%s-%s-%s", key.substring(0, 8), key.substring(8, 12), key.substring(12, 16),
				key.substring(16, 20), key.substring(20));
	}

	public static byte[] hexToByte(String hexString) {
		Objects.requireNonNull(hexString, "Hex string cannot be null");

		// Remove optional 0x prefix
		String cleanHex = hexString.startsWith("0x") ? hexString.substring(2) : hexString;

		// Remove dashes (for UUID-like format)
		cleanHex = cleanHex.replace("-", "");

		if (cleanHex.isEmpty()) {
			return new byte[0];
		}

		// Handle odd length by padding with leading zero
		if (cleanHex.length() % 2 != 0) {
			cleanHex = "0" + cleanHex;
		}

		byte[] result = new byte[cleanHex.length() / 2];
		for (int i = 0; i < result.length; i++) {
			int index = i * 2;
			try {
				result[i] = (byte) Integer.parseInt(cleanHex.substring(index, index + 2), 16);
			}
			catch (NumberFormatException e) {
				throw new IllegalArgumentException("Invalid hex character at position " + index + " in: " + hexString,
						e);
			}
		}
		return result;
	}

	public static boolean validateKey(String hexKey) {
		String cleanHexKey = hexKey.replace("-", "");
		if (cleanHexKey.length() == 32) {
			try {
				hexToByte(cleanHexKey);
				return true;
			}
			catch (Exception e) {
				log.warn("Invalid hex key: {}", hexKey, e);
				return false;
			}
		}
		return false;
	}

	private void initHMAC(byte[] keyValue) {
		try {
			SecretKeySpec key = new SecretKeySpec(keyValue, HMAC_SHA256);
			this.mac = Mac.getInstance(HMAC_SHA256);
			this.mac.init(key);
		}
		catch (NoSuchAlgorithmException e) {
			log.error("Invalid algorithm for the HMAC", e);
		}
		catch (InvalidKeyException e) {
			log.error("Invalid key for the HMAC init", e);
		}
	}

	// returns value in [scaleMin..scaleMax)
	public double scaleHash(String value, int scaledMin, int scaledMax) {
		final byte[] hash = new byte[6];
		final double max = 0x1000000000000L;
		final double scale = scaledMax - (double) scaledMin;

		System.arraycopy(byteHash(value), 0, hash, 0, 6);
		double fraction = new BigInteger(1, hash).doubleValue() / max;
		return (int) (fraction * scale) + (double) scaledMin;
	}

	public String uidHash(String inputUID) {
		if (!StringUtil.hasText(inputUID)) {
			throw new IllegalArgumentException();
		}
		byte[] uuid = new byte[16];
		System.arraycopy(byteHash(inputUID), 0, uuid, 0, 16);
		// https://en.wikipedia.org/wiki/Universally_unique_identifier
		// GUID type 4
		// Version -> 4
		uuid[6] &= 0x0F;
		uuid[6] |= 0x40;
		// Variant 1 -> 10b
		uuid[8] &= 0x3F;
		uuid[8] |= 0x80;
		return "2.25." + new BigInteger(1, uuid);
	}

	public HashContext getHashContext() {
		return hashContext;
	}

	public byte[] byteHash(String value) {
		byte[] bytes = null;
		bytes = mac.doFinal(value.getBytes(StandardCharsets.US_ASCII));
		return bytes;
	}

}
