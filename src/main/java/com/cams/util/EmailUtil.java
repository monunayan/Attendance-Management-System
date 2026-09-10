package com.cams.util;

import java.util.Properties;
import java.util.Random;

public class EmailUtil {

    private static final String SENDER_EMAIL = "monunayan22@gmail.com";
    private static final String SENDER_APP_PASSWORD = "ahbd slaa idbn pals";

    // Generate 6-digit OTP
    public static String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    // Send OTP Email safely (No Error 500 even if mail jar missing)
    public static boolean sendOTPEmail(String recipientEmail, String otp) {
        System.out.println("=================================================");
        System.out.println("[EMAIL SERVICE] OTP Generated for " + recipientEmail + ": " + otp);
        System.out.println("=================================================");

        try {
            // Dynamically check if Mail API is available in Tomcat classpath
            Class.forName("jakarta.mail.Session");

            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            jakarta.mail.Session session = jakarta.mail.Session.getInstance(props, new jakarta.mail.Authenticator() {
                @Override
                protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new jakarta.mail.PasswordAuthentication(SENDER_EMAIL, SENDER_APP_PASSWORD.replaceAll("\\s+", ""));
                }
            });

            jakarta.mail.Message message = new jakarta.mail.internet.MimeMessage(session);
            message.setFrom(new jakarta.mail.internet.InternetAddress(SENDER_EMAIL, "College Attendance System"));
            message.setRecipients(jakarta.mail.Message.RecipientType.TO, jakarta.mail.internet.InternetAddress.parse(recipientEmail));
            message.setSubject("OTP for Password Reset - College Attendance System");

            String htmlContent = "<div style='font-family: Arial, sans-serif; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px; max-width: 500px; margin: 0 auto;'>"
                    + "<h2 style='color: #7c3aed; text-align: center;'>College Attendance System</h2>"
                    + "<p>Hello,</p>"
                    + "<p>You requested a password reset for your account. Please use the following 6-digit OTP:</p>"
                    + "<div style='background-color: #f3e8ff; color: #7c3aed; font-size: 28px; font-weight: bold; text-align: center; padding: 15px; border-radius: 8px; letter-spacing: 5px; margin: 20px 0;'>"
                    + otp
                    + "</div>"
                    + "<p style='color: #666; font-size: 13px;'>This OTP is valid for your current session.</p>"
                    + "<hr style='border: none; border-top: 1px solid #eee; margin-top: 20px;' />"
                    + "<p style='color: #999; font-size: 11px; text-align: center;'>VNS Group of Colleges, Bhopal</p>"
                    + "</div>";

            message.setContent(htmlContent, "text/html; charset=utf-8");

            // Send Email in background thread so UI doesn't block
            new Thread(() -> {
                try {
                    jakarta.mail.Transport.send(message);
                    System.out.println("[EMAIL SERVICE] Email sent successfully to: " + recipientEmail);
                } catch (Exception e) {
                    System.err.println("[EMAIL SERVICE ERROR] " + e.getMessage());
                }
            }).start();

            return true;
        } catch (Throwable t) {
            System.err.println("[EMAIL SERVICE WARNING] Mail library not loaded in Tomcat. OTP logged: " + otp);
            t.printStackTrace();
            return true; // Still allow user to proceed without Error 500!
        }
    }
}
