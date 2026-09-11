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
        String role = request.getParameter("role") != null ? request.getParameter("role").trim() : "";
        String department = request.getParameter("department") != null ? request.getParameter("department").trim() : "";
        String[] semesters = request.getParameterValues("semesters"); // Multiple semesters

        if (name.isEmpty() || employeeId.isEmpty() || email.isEmpty() || password.isEmpty() || role.isEmpty() || department.isEmpty()) {
            response.sendRedirect("faculty_register.jsp?error=Please fill in all required fields.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            response.sendRedirect("faculty_register.jsp?error=Please enter a valid email address.");
            return;
        }

        if (userDAO.isFacultyEmployeeIdExists(employeeId)) {
            response.sendRedirect("faculty_register.jsp?error=Employee ID (" + employeeId + ") is already registered!");
            return;
        }

        if (userDAO.isFacultyEmailExists(email)) {
            response.sendRedirect("faculty_register.jsp?error=Email address (" + email + ") is already registered!");
            return;
        }

        Faculty faculty = new Faculty();
        faculty.setName(name);
        faculty.setEmployeeId(employeeId);
        faculty.setEmail(email);
        faculty.setPassword(password);
        faculty.setRole(role);
        faculty.setDepartment(department);

        boolean success = userDAO.registerFaculty(faculty, semesters);

        if (success) {
            Faculty loggedInFaculty = userDAO.loginFaculty(employeeId, password);
            if (loggedInFaculty == null) {
                // Fallback: construct faculty object directly
                loggedInFaculty = faculty;
            }
            jakarta.servlet.http.HttpSession session = request.getSession();
            session.setAttribute("currentUser", loggedInFaculty);
            session.setAttribute("userType", "faculty");
            response.sendRedirect("faculty_dashboard.jsp");
        } else {
            response.sendRedirect("faculty_register.jsp?error=Registration Failed. Database connection failed or invalid data provided.");
        }
    }
}

