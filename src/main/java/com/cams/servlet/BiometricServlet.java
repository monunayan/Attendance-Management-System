package com.cams.servlet;

import com.cams.dao.BiometricDAO;
import com.cams.dao.UserDAO;
import com.cams.model.Faculty;
import com.cams.model.Student;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class BiometricServlet extends HttpServlet {
    private BiometricDAO biometricDAO;
    private UserDAO userDAO;

    @Override
    public void init() {
        biometricDAO = new BiometricDAO();
        userDAO = new UserDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String action = request.getParameter("action");

        if ("register".equalsIgnoreCase(action)) {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("currentUser") == null) {
                out.print("{\"status\":\"error\", \"message\":\"Please login first to enable Biometric Login.\"}");
                return;
            }

            String credentialId = request.getParameter("credentialId");
            if (credentialId == null || credentialId.trim().isEmpty()) {
                out.print("{\"status\":\"error\", \"message\":\"Invalid credential ID.\"}");
                return;
            }

            Object userObj = session.getAttribute("currentUser");
            String userType = (String) session.getAttribute("userType");
            String userId = "";

            if (userObj instanceof Student) {
                userId = ((Student) userObj).getEnrollmentId();
                if (userType == null || userType.isEmpty()) userType = "student";
            } else if (userObj instanceof Faculty) {
                userId = ((Faculty) userObj).getEmployeeId();
                if (userType == null || userType.isEmpty()) userType = "faculty";
            }

            if (userId.isEmpty() || userType == null || userType.isEmpty()) {
                out.print("{\"status\":\"error\", \"message\":\"User details not found in session. Please login again.\"}");
                return;
            }

            boolean saved = biometricDAO.saveBiometric(userId, userType, credentialId, "WEBAUTHN_KEY");
            if (saved) {
                out.print("{\"status\":\"success\", \"message\":\"Biometric registered successfully for " + userId + "! You can now login using Fingerprint / Face ID.\"}");
            } else {
                out.print("{\"status\":\"error\", \"message\":\"Failed to save biometric credential in database. Please try again.\"}");
            }
            return;
        }

        if ("login".equalsIgnoreCase(action)) {
            String credentialId = request.getParameter("credentialId");
            if (credentialId == null || credentialId.trim().isEmpty()) {
                out.print("{\"status\":\"error\", \"message\":\"Invalid credential.\"}");
                return;
            }

            String[] userInfo = biometricDAO.findUserByCredentialId(credentialId);
            if (userInfo == null) {
                out.print("{\"status\":\"error\", \"message\":\"Biometric credential not registered. Please login with Password first and enable Biometric Login in your dashboard.\"}");
                return;
            }

            String userId = userInfo[0];
            String userType = userInfo[1];

            HttpSession session = request.getSession();

            if ("student".equalsIgnoreCase(userType)) {
                // Find student by enrollmentId
                Student student = userDAO.loginStudent(userId, getStudentPasswordByEnrollment(userId));
                if (student == null) {
                    student = getStudentByEnrollmentDirect(userId);
                }
                if (student != null) {
                    session.setAttribute("currentUser", student);
                    session.setAttribute("userType", "student");
                    out.print("{\"status\":\"success\", \"redirectUrl\":\"student_dashboard.jsp\"}");
                    return;
                }
            } else if ("faculty".equalsIgnoreCase(userType)) {
                Faculty faculty = getFacultyByEmployeeIdDirect(userId);
                if (faculty != null) {
                    session.setAttribute("currentUser", faculty);
                    session.setAttribute("userType", "faculty");
                    out.print("{\"status\":\"success\", \"redirectUrl\":\"faculty_dashboard.jsp\"}");
                    return;
                }
            }

            out.print("{\"status\":\"error\", \"message\":\"Account not found or invalid user.\"}");
            return;
        }

        out.print("{\"status\":\"error\", \"message\":\"Invalid action.\"}");
    }

    private String getStudentPasswordByEnrollment(String enrollmentId) {
        String sql = "SELECT password FROM students WHERE enrollment_id = ?";
        try (java.sql.Connection conn = com.cams.util.DBConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, enrollmentId);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("password");
            }
        } catch (Exception ignored) {}
        return "";
    }

    private Student getStudentByEnrollmentDirect(String enrollmentId) {
        String sql = "SELECT * FROM students WHERE enrollment_id = ?";
        try (java.sql.Connection conn = com.cams.util.DBConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, enrollmentId);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Student student = new Student();
                    student.setId(rs.getInt("id"));
                    student.setName(rs.getString("name"));
                    student.setEnrollmentId(rs.getString("enrollment_id"));
                    student.setEmail(rs.getString("email"));
                    student.setStream(rs.getString("stream"));
                    student.setSemesterId(rs.getInt("semester_id"));
                    return student;
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    private Faculty getFacultyByEmployeeIdDirect(String employeeId) {
        String sql = "SELECT * FROM faculty WHERE employee_id = ?";
        try (java.sql.Connection conn = com.cams.util.DBConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Faculty faculty = new Faculty();
                    faculty.setId(rs.getInt("id"));
                    faculty.setName(rs.getString("name"));
                    faculty.setEmployeeId(rs.getString("employee_id"));
                    faculty.setEmail(rs.getString("email"));
                    faculty.setRole(rs.getString("role"));
                    faculty.setDepartment(rs.getString("department"));
                    return faculty;
                }
            }
        } catch (Exception ignored) {}
        return null;
    }
}
