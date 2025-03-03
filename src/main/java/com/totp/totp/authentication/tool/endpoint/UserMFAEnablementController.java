package com.totp.totp.authentication.tool.endpoint;

import com.google.zxing.WriterException;
import com.totp.totp.authentication.tool.QRCodeGenerator;
import com.totp.totp.authentication.tool.TOTPConstants;
import com.totp.totp.authentication.tool.TOTPUtil;

import com.totp.totp.authentication.tool.pojo.QRResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserMFAEnablementController {

    @GetMapping("/generateQRCode")
    public ResponseEntity<QRResponse> getQRCodeInformation(){
        // Build the otpauth URI
        String otpAuthUrl = TOTPUtil.buildOtpAuthUrl(TOTPConstants.ISSUER, TOTPConstants.ACCOUNT_NAME,
                getUserSecret(), TOTPConstants.ALGO, TOTPConstants.digits, TOTPConstants.PERIOD);
        System.out.println("otpauth URL: " + otpAuthUrl);

        // Generate QR code as a Base64 PNG image (you can send this string to the UI)
        String qrCodeBase64 = null;
        try {
            qrCodeBase64 = QRCodeGenerator.generateQRCodeBase64(otpAuthUrl, 250, 250);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
        System.out.println("QR Code (Base64): " + qrCodeBase64);
        QRResponse response = new QRResponse(qrCodeBase64);
        return ResponseEntity.ok(response);
      
    }

    @GetMapping("/validateCode")
    public boolean validateQRCode(@RequestParam("otp") String keyedInOtp){
        int otp = TOTPUtil.generateTOTP(getUserSecret(),System.currentTimeMillis());
        Integer userOTP = Integer.parseInt(keyedInOtp);
        System.out.println(userOTP + "::" + otp);
        return userOTP.equals(otp);

    }


    private String getUserSecret() {
        return "naveen";
    }
}
