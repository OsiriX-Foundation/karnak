package org.karnak.profilepipe.utils;

import org.karnak.data.DcmProfileConfig;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

import org.karnak.data.gateway.Destination;
import org.karnak.data.profile.ProfileElement;
import org.slf4j.*;

public class HMAC {
    private final Logger LOGGER = LoggerFactory.getLogger(HMAC.class);
    private Mac mac;

    private static final String HMAC_SHA256 = "HmacSHA256";
    private String hmackey;
    private HashContext hashContext;

    public HMAC() {
        hmackey = DcmProfileConfig.getInstance().getHmackey();
        initHMAC(hmackey);
    }

    public HMAC(String hmackey) {
        initHMAC(hmackey);
    }

    public HMAC(HashContext hashContext) {
        this.hashContext = hashContext;
        initHMAC(hashContext.getSecret());
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

    public String uidHash(String inputUID) {
        byte[] uuid = new byte[16];
        System.arraycopy(byteHash(inputUID), 0 , uuid, 0, 16);
        // https://en.wikipedia.org/wiki/Universally_unique_identifier
        // GUID type 4
        // Version -> 4
        uuid[6] &= 0x0F;
        uuid[6] |= 0x40;
        // Variant 1 -> 10b
        uuid[8] &= 0x3F;
        uuid[8] |= 0x80;
        return "2.25." + new BigInteger(1, uuid).toString();
    }

    public static String generatePatientIDProfile(String PatientID, Destination destination) {
        if (destination.getProfile() != null) {
            final List<ProfileElement> listProfileElement = destination.getProfile().getProfileElements();
            String profilesCodeName = String.join(
                    "-" , listProfileElement.stream().map(profile -> profile.getCodename()).collect(Collectors.toList())
            );
            return PatientID.concat(profilesCodeName);
        }
        return PatientID;
    }
}
