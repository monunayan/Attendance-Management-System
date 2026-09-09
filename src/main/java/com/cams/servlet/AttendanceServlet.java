package com.cams.servlet;

import com.cams.dao.AttendanceDAO;
import com.cams.model.Faculty;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AttendanceServlet extends HttpServlet {
    private AttendanceDAO attendanceDAO;

    @Override
    public void init() {
        attendanceDAO = new AttendanceDAO();
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

        String[] studentIds = request.getParameterValues("studentId");
        String subjectIdStr = request.getParameter("subjectId");
        
        if (studentIds == null || subjectIdStr == null) {
            response.sendRedirect("faculty_dashboard.jsp?error=Missing Parameters");
            return;
        }

        int subjectId = Integer.parseInt(subjectIdStr);
        Date sqlDate = Date.valueOf(LocalDate.now());

        boolean allSuccess = true;

        for (String sIdStr : studentIds) {
            int studentId = Integer.parseInt(sIdStr);
            String status = request.getParameter("status_" + studentId);
            if (status == null) status = "A";

            boolean success = attendanceDAO.markAttendance(studentId, faculty.getId(), subjectId, sqlDate, status);
            if (!success) {
                allSuccess = false;
            }
        }

        String activeSem = request.getParameter("activeSem");
        String semParam = (activeSem != null && !activeSem.isEmpty()) ? "&activeSem=" + activeSem : "";

        if (allSuccess) {
            response.sendRedirect("faculty_dashboard.jsp?msg=Attendance Saved Successfully!" + semParam);
        } else {
            response.sendRedirect("faculty_dashboard.jsp?error=Some records failed to save." + semParam);
        }
    }
}
