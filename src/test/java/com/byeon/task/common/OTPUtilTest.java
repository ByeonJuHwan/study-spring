package com.byeon.task.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OTPUtilTest {

    String directory = "/Users/yoe21c/dev/coach/byeon/study-spring/src/test/java/com/byeon/task/common/";

    @Test
    public void QR이미지생성하기() throws Exception {

        final String secretKey = OTPUtil.getSecretKey();

        System.out.println("secretKey = [" + secretKey + "]");

        final String googleOTPAuthURL = OTPUtil.getGoogleOTPAuthURL(secretKey, "마이클", "나이스");

        OTPUtil.getQRImage(googleOTPAuthURL, directory + "myQr.png", 400, 400);
    }

    @Test
    public void OTP_검증하기() throws Exception {

        final String sixDigitsOtp = OTPUtil.getTOTPCode("NFSVWKRSWONRFN5AIJLEAGKXWS3JFJLT");

        System.out.println("sixDigitsOtp = [" + sixDigitsOtp + "]");
    }
}