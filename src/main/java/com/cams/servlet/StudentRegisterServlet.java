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
        String password = request.getParameter("password") != null ? request.getParameter("password").trim() : "";
        String stream = request.getParameter("stream");
        String semesterIdStr = request.getParameter("semesterId");

        int semesterId = 1;
        if (semesterIdStr != null && !semesterIdStr.isEmpty()) {
            semesterId = Integer.parseInt(semesterIdStr);
        }

        Student student = new Student();
        student.setName(name);
        student.setEnrollmentId(enrollmentId);
        student.setPassword(password);
        student.setStream(stream);
        student.setSemesterId(semesterId);

        boolean success = userDAO.registerStudent(student);

        if (success) {
            response.sendRedirect("index.jsp?msg=Student Registered Successfully! Please Login.");
        } else {
            response.sendRedirect("student_register.jsp?error=Registration Failed. Please try again.");
        }
    }
}
