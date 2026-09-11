package com.cams.servlet;

import com.cams.dao.UserDAO;
import com.cams.model.Student;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class StudentRegisterServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("name") != null ? request.getParameter("name").trim() : "";
        String enrollmentId = request.getParameter("enrollmentId") != null ? request.getParameter("enrollmentId").trim() : "";
        String email = request.getParameter("email") != null ? request.getParameter("email").trim() : "";
        String password = request.getParameter("password") != null ? request.getParameter("password").trim() : "";
        String stream = request.getParameter("stream") != null ? request.getParameter("stream").trim() : "";
        String semesterIdStr = request.getParameter("semesterId") != null ? request.getParameter("semesterId").trim() : "";

        if (name.isEmpty() || enrollmentId.isEmpty() || email.isEmpty() || password.isEmpty() || stream.isEmpty() || semesterIdStr.isEmpty()) {
            response.sendRedirect("student_register.jsp?error=Please fill in all required fields.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            response.sendRedirect("student_register.jsp?error=Please enter a valid email address.");
            return;
        }

        if (!enrollmentId.matches("^0161[A-Za-z0-9]{8}$")) {
            response.sendRedirect("student_register.jsp?error=Enrollment ID is invalid according to the VNS Group. Please check it.");
            return;
        }

        int semesterId = 1;
        try {
            semesterId = Integer.parseInt(semesterIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect("student_register.jsp?error=Invalid semester selected.");
            return;
        }

        if (userDAO.isStudentEnrollmentExists(enrollmentId)) {
            response.sendRedirect("student_register.jsp?error=Enrollment ID (" + enrollmentId + ") is already registered!");
            return;
        }

        if (userDAO.isStudentEmailExists(email)) {
            response.sendRedirect("student_register.jsp?error=Email address (" + email + ") is already registered!");
            return;
        }

        Student student = new Student();
        student.setName(name);
        student.setEnrollmentId(enrollmentId);
        student.setEmail(email);
        student.setPassword(password);
        student.setStream(stream);
        student.setSemesterId(semesterId);

        boolean success = userDAO.registerStudent(student);

        if (success) {
            Student loggedInStudent = userDAO.loginStudent(enrollmentId, password);
            if (loggedInStudent == null) {
                // Fallback: construct student object directly if login query delayed
                loggedInStudent = student;
            }
            jakarta.servlet.http.HttpSession session = request.getSession();
            session.setAttribute("currentUser", loggedInStudent);
            session.setAttribute("userType", "student");
            response.sendRedirect("student_dashboard.jsp");
        } else {
            response.sendRedirect("student_register.jsp?error=Registration Failed. Database connection failed or invalid data provided.");
        }
    }
}

