package com.cams.servlet;

import com.cams.dao.UserDAO;
import com.cams.model.Student;
import com.cams.model.Faculty;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userId = request.getParameter("userId") != null ? request.getParameter("userId").trim() : "";
        String password = request.getParameter("password") != null ? request.getParameter("password").trim() : "";
        String userType = request.getParameter("userType"); // "student" or "faculty"

        HttpSession session = request.getSession();

        if ("student".equals(userType)) {
            Student student = userDAO.loginStudent(userId, password);
            if (student != null) {
                session.setAttribute("currentUser", student);
                session.setAttribute("userType", "student");
                response.sendRedirect("student_dashboard.jsp");
            } else {
                response.sendRedirect("index.jsp?error=Invalid Credentials");
            }
        } else if ("faculty".equals(userType)) {
            Faculty faculty = userDAO.loginFaculty(userId, password);
            if (faculty != null) {
                session.setAttribute("currentUser", faculty);
                session.setAttribute("userType", "faculty");
                response.sendRedirect("faculty_dashboard.jsp");
            } else {
                response.sendRedirect("index.jsp?error=Invalid Credentials");
            }
        } else {
            response.sendRedirect("index.jsp?error=Please select user type");
        }
    }
}
