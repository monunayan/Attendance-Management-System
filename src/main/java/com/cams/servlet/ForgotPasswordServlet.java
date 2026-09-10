package com.cams.servlet;

import com.cams.dao.UserDAO;
import com.cams.util.EmailUtil;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class ForgotPasswordServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession();

        if ("send_otp".equals(action)) {
            String userType = request.getParameter("userType");
            String userId = request.getParameter("userId") != null ? request.getParameter("userId").trim() : "";
            String email = request.getParameter("email") != null ? request.getParameter("email").trim() : "";

            if (userType == null || userId.isEmpty() || email.isEmpty()) {
                response.sendRedirect("forgot_password.jsp?error=Please fill all required fields!");
                return;
            }

            boolean isValid = false;
            if ("student".equals(userType)) {
                isValid = userDAO.verifyStudentEmail(userId, email);
            } else if ("faculty".equals(userType)) {
                isValid = userDAO.verifyFacultyEmail(userId, email);
            }

            if (isValid) {
                String otp = EmailUtil.generateOTP();
                EmailUtil.sendOTPEmail(email, otp);

                // Store OTP & metadata in session (expires in current session)
                session.setAttribute("reset_otp", otp);
                session.setAttribute("reset_user_type", userType);
                session.setAttribute("reset_user_id", userId);
                session.setAttribute("reset_email", email);

                response.sendRedirect("forgot_password.jsp?step=2&msg=OTP has been sent to " + email);
            } else {
                response.sendRedirect("forgot_password.jsp?error=Invalid User ID or Email. Account not found!");
            }
        } 
        else if ("verify_reset".equals(action)) {
            String inputOtp = request.getParameter("otp") != null ? request.getParameter("otp").trim() : "";
            String newPassword = request.getParameter("newPassword") != null ? request.getParameter("newPassword").trim() : "";

            String sessionOtp = (String) session.getAttribute("reset_otp");
            String userType = (String) session.getAttribute("reset_user_type");
            String userId = (String) session.getAttribute("reset_user_id");

            if (sessionOtp == null || userType == null || userId == null) {
                response.sendRedirect("forgot_password.jsp?error=Session expired. Please try again.");
                return;
            }

            if (!sessionOtp.equals(inputOtp)) {
                response.sendRedirect("forgot_password.jsp?step=2&error=Invalid OTP! Please enter correct OTP.");
                return;
            }

            // OTP verified, update password
            boolean updated = false;
            if ("student".equals(userType)) {
                updated = userDAO.updateStudentPassword(userId, newPassword);
            } else if ("faculty".equals(userType)) {
                updated = userDAO.updateFacultyPassword(userId, newPassword);
            }

            if (updated) {
                // Clear session attributes
                session.removeAttribute("reset_otp");
                session.removeAttribute("reset_user_type");
                session.removeAttribute("reset_user_id");
                session.removeAttribute("reset_email");

                response.sendRedirect("index.jsp?msg=Password updated successfully! Please login with your new password.");
            } else {
                response.sendRedirect("forgot_password.jsp?step=2&error=Failed to update password. Try again.");
            }
        } else {
            response.sendRedirect("forgot_password.jsp");
        }
    }
}
