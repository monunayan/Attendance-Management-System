package com.cams.servlet;

import com.cams.dao.UserDAO;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ForgotPasswordServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userId = request.getParameter("userId") != null ? request.getParameter("userId").trim() : "";
        String name = request.getParameter("name") != null ? request.getParameter("name").trim() : "";
        String newPassword = request.getParameter("newPassword") != null ? request.getParameter("newPassword").trim() : "";
        String userType = request.getParameter("userType");

        if (userId.isEmpty() || name.isEmpty() || newPassword.isEmpty() || userType == null) {
            response.sendRedirect("forgot_password.jsp?error=All fields are required!");
            return;
        }

        boolean success = false;
        if ("student".equals(userType)) {
            success = userDAO.resetStudentPassword(userId, name, newPassword);
        } else if ("faculty".equals(userType)) {
            success = userDAO.resetFacultyPassword(userId, name, newPassword);
        }

        if (success) {
            response.sendRedirect("index.jsp?msg=Password reset successful! Please login with your new password.");
        } else {
            response.sendRedirect("forgot_password.jsp?error=Invalid User ID or Name verification failed!");
        }
    }
}
