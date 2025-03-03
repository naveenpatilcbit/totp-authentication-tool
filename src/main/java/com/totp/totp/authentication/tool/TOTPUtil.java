package com.totp.totp.authentication.tool;

import org.apache.commons.codec.binary.Base32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class TOTPUtil {

    public static String buildOtpAuthUrl(String issuer, String accountName, String userSecret, String algo, int digits, int period) {
        String label = issuer + ":" + accountName;
        return String.format("otpauth://totp/%s?secret=%s&issuer=%s&algorithm=%s&digits=%d&period=%d",
                label, userSecret, issuer, algo, digits, period);
    }

    /**
     * Generates a TOTP code using HMAC-SHA1.
     *
     * @param secret A shared secret key (in a real-world app, decode your Base32 secret to a byte array)
     * @param timeMillis The current time in milliseconds
     * @return The generated TOTP code as an integer
     */
    public static int generateTOTP(String secret, long timeMillis) {
        // Calculate the number of time steps since Unix epoch
        long counter = (timeMillis / 1000) / TOTPConstants.PERIOD;

        // Convert counter to an 8-byte array in big-endian order
        byte[] counterBytes = ByteBuffer.allocate(8).putLong(counter).array();

        try {
            // Initialize the HMAC with the shared secret
            // (In a real-world implementation, decode the Base32 secret into bytes first)
            Base32 base32 = new Base32();
            byte[] keyBytes = base32.decode(secret);
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, TOTPConstants.ALGO);

            Mac mac = Mac.getInstance(TOTPConstants.ALGO);
            mac.init(signingKey);

            // Compute the HMAC on the counter
            byte[] hmac = mac.doFinal(counterBytes);

            // Dynamic truncation to extract a 4-byte dynamic binary code:
            int offset = hmac[hmac.length - 1] & 0x0F;
            int binaryCode = ((hmac[offset] & 0x7F) << 24)
                    | ((hmac[offset + 1] & 0xFF) << 16)
                    | ((hmac[offset + 2] & 0xFF) << 8)
                    | (hmac[offset + 3] & 0xFF);

            // Reduce the binary code to the required number of digits
            int otp = binaryCode % (int) Math.pow(10, TOTPConstants.digits);
            return otp;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error generating TOTP", e);
        }
    }
}
