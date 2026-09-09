package com.cams.servlet;

import com.cams.dao.UserDAO;
import com.cams.model.Faculty;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class DeleteSubjectServlet extends HttpServlet {
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

        String subjectIdStr = request.getParameter("subjectId");
        String activeSem   = request.getParameter("activeSem");
        String semParam    = (activeSem != null && !activeSem.isEmpty()) ? "&activeSem=" + activeSem : "";

        if (subjectIdStr == null || subjectIdStr.trim().isEmpty()) {
            response.sendRedirect("faculty_dashboard.jsp?error=Invalid subject ID." + semParam);
            return;
        }

        try {
            int subjectId = Integer.parseInt(subjectIdStr.trim());
            boolean success = userDAO.deleteSubject(subjectId);
            if (success) {
                response.sendRedirect("faculty_dashboard.jsp?msg=Subject deleted successfully!" + semParam);
            } else {
                response.sendRedirect("faculty_dashboard.jsp?error=Failed to delete subject." + semParam);
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("faculty_dashboard.jsp?error=Invalid subject ID." + semParam);
        }
    }
}
