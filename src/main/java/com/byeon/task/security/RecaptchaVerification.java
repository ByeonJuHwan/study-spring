package com.byeon.task.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;

@Component
public class RecaptchaVerification {

    public static final String url = "https://www.google.com/recaptcha/api/siteverify";
    private final static String USER_AGENT = "Mozilla/5.0";

    private static String secret = "6LdNqJQpAAAAAHmkJpD-RlEsO_lHDM4WC0SXcehU";

    public boolean verify(String gRecaptchaResponse) {

        if (gRecaptchaResponse == null || "".equals(gRecaptchaResponse)) {
            return false;
        }

        try{
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            // add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            String postParams = "secret=" + secret + "&response=" + gRecaptchaResponse;

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer responseBuffer = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                responseBuffer.append(inputLine);
            }
            in.close();

            final String response = responseBuffer.toString();
            System.out.println("recaptcha > response = [" + response + "]");
            ObjectMapper om = new ObjectMapper();
            final RecaptchaResult recaptchaResult = om.readValue(response, RecaptchaResult.class);
            return recaptchaResult.isSuccess();
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
     }

}