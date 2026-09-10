package com.cams.servlet;

import com.cams.dao.UserDAO;
import com.cams.model.Faculty;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FacultyRegisterServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("name") != null ? request.getParameter("name").trim() : "";
        String employeeId = request.getParameter("employeeId") != null ? request.getParameter("employeeId").trim() : "";
        String email = request.getParameter("email") != null ? request.getParameter("email").trim() : "";
        String password = request.getParameter("password") != null ? request.getParameter("password").trim() : "";
        String role = request.getParameter("role");
        String department = request.getParameter("department");
        String[] semesters = request.getParameterValues("semesters"); // Multiple semesters

        Faculty faculty = new Faculty();
        faculty.setName(name);
        faculty.setEmployeeId(employeeId);
        faculty.setEmail(email);
        faculty.setPassword(password);
        faculty.setRole(role);
        faculty.setDepartment(department);

        boolean success = userDAO.registerFaculty(faculty, semesters);

        if (success) {
            response.sendRedirect("index.jsp?msg=Faculty Registered Successfully! Please Login.");
        } else {
            response.sendRedirect("faculty_register.jsp?error=Registration Failed. Please try again.");
        }
    }
}
