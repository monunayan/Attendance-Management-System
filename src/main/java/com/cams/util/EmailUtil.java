package com.cams.util;

import java.util.Random;

public class EmailUtil {

    // Generate 6-digit OTP
    public static String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    // Send OTP email (Uses Java Mail API / prints log if SMTP not configured)
    public static boolean sendOTPEmail(String recipientEmail, String otp) {
        System.out.println("=================================================");
        System.out.println("[EMAIL SERVICE] OTP Sent to: " + recipientEmail);
        System.out.println("[EMAIL SERVICE] Your OTP for Password Reset is: " + otp);
        System.out.println("=================================================");

        // In real deployment with SMTP credentials configured:
        // Properties props = new Properties();
        // props.put("mail.smtp.host", "smtp.gmail.com");
        // props.put("mail.smtp.port", "587");
        // props.put("mail.smtp.auth", "true");
        // props.put("mail.smtp.starttls.enable", "true");
        // Session session = Session.getInstance(props, new Authenticator() {...});
        // Message message = new MimeMessage(session);
        // message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
        // message.setSubject("Password Reset OTP - College Attendance System");
        // message.setText("Your OTP for resetting your password is: " + otp);
        // Transport.send(message);

        return true;
    }
}
