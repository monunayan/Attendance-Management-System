package com.cams.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Base64;
import java.util.Random;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class EmailUtil {

    private static final String SENDER_EMAIL = "monunayan22@gmail.com";
    private static final String SENDER_APP_PASSWORD = "ahbd slaa idbn pals";

    // Generate 6-digit OTP
    public static String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    // Send OTP Email via Direct Gmail SSL Socket
    public static boolean sendOTPEmail(String recipientEmail, String otp) {
        System.out.println("=================================================");
        System.out.println("[SMTP SERVICE] Attempting to send OTP email to: " + recipientEmail);
        System.out.println("[SMTP SERVICE] Generated OTP Code: " + otp);
        System.out.println("=================================================");

        try {
            String pass = SENDER_APP_PASSWORD.replaceAll("\\s+", "");
            SSLSocketFactory sf = (SSLSocketFactory) SSLSocketFactory.getDefault();
            
            // Connect to Gmail SSL Port 465
            SSLSocket socket = (SSLSocket) sf.createSocket("smtp.gmail.com", 465);
            socket.setSoTimeout(10000); // 10s timeout

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            OutputStream out = socket.getOutputStream();

            readResponse(reader); // 220 greeting

            sendCommand(out, "EHLO localhost\r\n");
            readResponse(reader);

            sendCommand(out, "AUTH LOGIN\r\n");
            readResponse(reader); // 334

            sendCommand(out, Base64.getEncoder().encodeToString(SENDER_EMAIL.getBytes("UTF-8")) + "\r\n");
            readResponse(reader); // 334

            sendCommand(out, Base64.getEncoder().encodeToString(pass.getBytes("UTF-8")) + "\r\n");
            String authResp = readResponse(reader); // 235 Authentication successful
            
            if (authResp == null || !authResp.startsWith("235")) {
                System.err.println("[SMTP ERROR] Authentication failed with Gmail. Check App Password!");
                socket.close();
                return false;
            }

            sendCommand(out, "MAIL FROM:<" + SENDER_EMAIL + ">\r\n");
            readResponse(reader);

            sendCommand(out, "RCPT TO:<" + recipientEmail.trim() + ">\r\n");
            readResponse(reader);

            sendCommand(out, "DATA\r\n");
            readResponse(reader);

            String mailData = "From: College Attendance System <" + SENDER_EMAIL + ">\r\n"
                    + "To: " + recipientEmail.trim() + "\r\n"
                    + "Subject: OTP for Password Reset - College Attendance System\r\n"
                    + "Content-Type: text/html; charset=UTF-8\r\n\r\n"
                    + "<div style='font-family: Arial, sans-serif; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px; max-width: 500px; margin: 0 auto;'>"
                    + "<h2 style='color: #7c3aed; text-align: center;'>College Attendance System</h2>"
                    + "<p>Hello,</p>"
                    + "<p>You requested a password reset for your account. Please use the following 6-digit OTP:</p>"
                    + "<div style='background-color: #f3e8ff; color: #7c3aed; font-size: 28px; font-weight: bold; text-align: center; padding: 15px; border-radius: 8px; letter-spacing: 5px; margin: 20px 0;'>"
                    + otp
                    + "</div>"
                    + "<p style='color: #666; font-size: 13px;'>This OTP is valid for your current session.</p>"
                    + "<hr style='border: none; border-top: 1px solid #eee; margin-top: 20px;' />"
                    + "<p style='color: #999; font-size: 11px; text-align: center;'>VNS Group of Colleges, Bhopal</p>"
                    + "</div>\r\n.\r\n";

            sendCommand(out, mailData);
            readResponse(reader);

            sendCommand(out, "QUIT\r\n");
            readResponse(reader);

            socket.close();
            System.out.println("[SMTP SERVICE SUCCESS] Email sent to Inbox: " + recipientEmail);
            return true;
        } catch (Exception e) {
            System.err.println("[SMTP SERVICE ERROR] " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private static void sendCommand(OutputStream out, String cmd) throws Exception {
        out.write(cmd.getBytes("UTF-8"));
        out.flush();
    }

    private static String readResponse(BufferedReader reader) throws Exception {
        String line = reader.readLine();
        if (line != null) {
            System.out.println("[SMTP Server] " + line);
        }
        return line;
    }
}
