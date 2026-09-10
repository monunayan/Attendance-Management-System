package com.cams.util;

import java.util.Properties;
import java.util.Random;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailUtil {

    // =========================================================================
    // ⚙️ GMAIL CONFIGURATION: Apni Gmail ID aur App Password Yahan Daalein
    // =========================================================================
    private static final String SENDER_EMAIL = "YOUR_GMAIL_ADDRESS@gmail.com";
    private static final String SENDER_APP_PASSWORD = "YOUR_16_DIGIT_APP_PASSWORD";

    // Generate 6-digit OTP
    public static String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    // Send OTP Email via Gmail SMTP
    public static boolean sendOTPEmail(String recipientEmail, String otp) {
        System.out.println("=================================================");
        System.out.println("[EMAIL SERVICE] Generated OTP for " + recipientEmail + ": " + otp);
        System.out.println("=================================================");

        // Agar Sender Email replace nahi kiya gaya hai toh log kar ke return true karo (Testing Fallback)
        if (SENDER_EMAIL.contains("YOUR_GMAIL_ADDRESS")) {
            System.out.println("[EMAIL SERVICE] SMTP Credentials not configured yet in EmailUtil.java");
            System.out.println("[EMAIL SERVICE] Test OTP to use: " + otp);
            return true;
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_APP_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL, "College Attendance Management System"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("OTP for Password Reset - College Attendance System");
            
            String htmlContent = "<div style='font-family: Arial, sans-serif; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px; max-width: 500px; margin: 0 auto;'>"
                    + "<h2 style='color: #7c3aed; text-align: center;'>College Attendance System</h2>"
                    + "<p>Hello,</p>"
                    + "<p>You requested a password reset for your account. Please use the following 6-digit OTP to complete your request:</p>"
                    + "<div style='background-color: #f3e8ff; color: #7c3aed; font-size: 28px; font-weight: bold; text-align: center; padding: 15px; border-radius: 8px; letter-spacing: 5px; margin: 20px 0;'>"
                    + otp
                    + "</div>"
                    + "<p style='color: #666; font-size: 13px;'>This OTP is valid for the current session. Please do not share this OTP with anyone.</p>"
                    + "<hr style='border: none; border-top: 1px solid #eee; margin-top: 20px;' />"
                    + "<p style='color: #999; font-size: 11px; text-align: center;'>VNS Group of Colleges, Bhopal</p>"
                    + "</div>";

            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("[EMAIL SERVICE] Email successfully sent to: " + recipientEmail);
            return true;
        } catch (MessagingException e) {
            System.err.println("[EMAIL SERVICE ERROR] Failed to send email: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
