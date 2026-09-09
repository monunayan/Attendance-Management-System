package com.cams.servlet;

import com.cams.dao.UserDAO;
import com.cams.model.Faculty;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AddSubjectServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Faculty faculty = (Faculty) session.getAttribute("currentUser");
        
        if (faculty == null || !"faculty".equals(session.getAttribute("userType"))) {
            response.sendRedirect("index.jsp");
            return;
        }

        String subjectName = request.getParameter("subjectName");
        String stream = request.getParameter("stream");
        String activeSem = request.getParameter("activeSem");

        if (subjectName == null || subjectName.trim().isEmpty() || stream == null || stream.trim().isEmpty()) {
            response.sendRedirect("faculty_dashboard.jsp?error=Subject name and stream are required.&activeSem=" + activeSem);
            return;
        }

        boolean success = userDAO.addSubject(subjectName.trim(), stream.trim());

        if (success) {
            response.sendRedirect("faculty_dashboard.jsp?msg=Subject '" + subjectName.trim() + "' added successfully!&activeSem=" + activeSem);
        } else {
            response.sendRedirect("faculty_dashboard.jsp?error=Failed to add subject. It may already exist.&activeSem=" + activeSem);
        }
    }
}
