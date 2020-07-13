package org.karnak.profilepipe.utils;

import org.karnak.data.DcmProfileConfig;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static io.swagger.codegen.v3.config.CodegenConfigurator.LOGGER;

public class HMAC {
    private Mac mac;

    private static final String HMAC_SHA256 = "HmacSHA256";
    private String hmackey;

    public HMAC() {
        hmackey = DcmProfileConfig.getInstance().getHmackey();
        initHMAC(hmackey);
    }

    public HMAC(String hmackey) {
        initHMAC(hmackey);
    }

    private void initHMAC(String keyValue) {
        try {
            SecretKeySpec key = new SecretKeySpec((keyValue).getBytes("UTF-8"), this.HMAC_SHA256);
            this.mac = Mac.getInstance(this.HMAC_SHA256);
            this.mac.init(key);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Unsupported Encoding exception for the HMACkey", e);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Invalid algorithm for the HMAC", e);
        } catch (InvalidKeyException e) {
            LOGGER.error("Invalid key for the HMAC init", e);
        }
    }


    public byte[] byteHash(String value) {
        byte[] bytes = null;
        try {
            bytes = mac.doFinal(value.getBytes("ASCII"));
        }
        catch(UnsupportedEncodingException e) {
            LOGGER.error("On hashed Value getBytes", e);
        }
        return bytes;
    }

    // returns value in [scaleMin..scaleMax)
    public double scaleHash(String value, int scaledMin, int scaledMax) {
        final byte[] hash = new byte[6];
        final double max = 0x1000000000000L;
        final double scale = scaledMax - scaledMin;

        System.arraycopy(byteHash(value), 0 , hash, 0, 6);
        double fraction = new BigInteger(1, hash).doubleValue()/max;
        return (int)(fraction * scale) + scaledMin;
    }

    public String uidHash(String inputPseudonym, String inputUID) {
        byte[] uuid = new byte[16];
        String value = inputPseudonym+inputUID;
        System.arraycopy(byteHash(value), 0 , uuid, 0, 16);
        uuid[6] &= 0x0F;
        uuid[6] |= 0x40;
        uuid[8] &= 0x3F;
        uuid[8] |= 0x80;
        return "2.25." + new BigInteger(1, uuid).toString();
    }
}
