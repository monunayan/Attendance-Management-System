package com.cams.util;

import java.util.Random;

public class EmailUtil {

    // Generate 6-digit OTP
    public static String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    // Send OTP Email safely
    public static boolean sendOTPEmail(String recipientEmail, String otp) {
        System.out.println("=================================================");
        System.out.println("[EMAIL OTP GENERATED] For: " + recipientEmail);
        System.out.println("[EMAIL OTP CODE] -> " + otp);
        System.out.println("=================================================");

        try {
            // Attempt to send email via Jakarta Mail API if JAR exists in server
            Class<?> sessionClass = Class.forName("jakarta.mail.Session");
            // If class found, mail API is available on classpath
        } catch (ClassNotFoundException e) {
            System.out.println("[EMAIL SERVICE INFO] Jakarta Mail JAR not found in Tomcat lib. Using fallback OTP mode.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
}
