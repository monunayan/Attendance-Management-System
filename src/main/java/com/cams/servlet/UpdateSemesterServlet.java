package com.cams.servlet;

import com.cams.dao.UserDAO;
import com.cams.model.Student;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class UpdateSemesterServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Student student = (Student) session.getAttribute("currentUser");
        
        if (student == null || !"student".equals(session.getAttribute("userType"))) {
            response.sendRedirect("index.jsp");
            return;
        }

        String semesterIdStr = request.getParameter("semesterId");
        if (semesterIdStr != null && !semesterIdStr.isEmpty()) {
            int newSemId = Integer.parseInt(semesterIdStr);
            boolean success = userDAO.updateStudentSemester(student.getId(), newSemId);
            
            if (success) {
                // Update the session object as well
                student.setSemesterId(newSemId);
                session.setAttribute("currentUser", student);
                response.sendRedirect("student_dashboard.jsp?msg=Semester Updated Successfully!");
            } else {
                response.sendRedirect("student_dashboard.jsp?error=Failed to update semester.");
            }
        } else {
            response.sendRedirect("student_dashboard.jsp?error=Please select a valid semester.");
        }
    }
}
