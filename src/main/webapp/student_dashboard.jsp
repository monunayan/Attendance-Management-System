<%@ page import="com.cams.model.Student" %>
<%@ page import="com.cams.model.Attendance" %>
<%@ page import="com.cams.dao.AttendanceDAO" %>
<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    Object currentUserObj = session.getAttribute("currentUser");
    if (currentUserObj == null || !(currentUserObj instanceof Student) || !"student".equals(session.getAttribute("userType"))) {
        response.sendRedirect("index.jsp?error=Session expired or invalid access. Please login again.");
        return;
    }
    Student student = (Student) currentUserObj;
    AttendanceDAO attDao = new AttendanceDAO();
    List<Attendance> records = attDao.getStudentAttendance(student.getId());
    
    int totalClasses = records.size();
    int presentCount = 0;
    for (Attendance a : records) {
        if ("P".equals(a.getStatus())) presentCount++;
    }
    int percentage = (totalClasses > 0) ? (presentCount * 100 / totalClasses) : 0;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/brands.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700;800&display=swap" rel="stylesheet">
    <link href="css/style.css" rel="stylesheet">
    <style>
        .social-icons { display: flex; justify-content: center; gap: 14px; margin: 12px 0; }
        .social-icons a {
            display: inline-flex; align-items: center; justify-content: center;
            width: 40px; height: 40px; border-radius: 50%;
            transition: all 0.25s ease; text-decoration: none;
            border: 2px solid; flex-shrink: 0;
        }
        .social-icons a svg { width: 18px; height: 18px; fill: currentColor; }
        .social-icons a.insta  { color: #e1306c; border-color: #e1306c; }
        .social-icons a.insta:hover  { background: #e1306c; color: #fff; transform: translateY(-3px); box-shadow: 0 6px 18px rgba(225,48,108,.35); }
        .social-icons a.fb { color: #1877f2; border-color: #1877f2; }
        .social-icons a.fb:hover { background: #1877f2; color: #fff; transform: translateY(-3px); box-shadow: 0 6px 18px rgba(24,119,242,.35); }
        .social-icons a.li { color: #0a66c2; border-color: #0a66c2; }
        .social-icons a.li:hover { background: #0a66c2; color: #fff; transform: translateY(-3px); box-shadow: 0 6px 18px rgba(10,102,194,.35); }
        .college-link { font-weight: 700; color: #1e6fff !important; }
        .college-link:hover { background: #1e6fff !important; color: #fff !important; }
    </style>
</head>
<body>
    <div class="container py-4">
        <div class="card-modern mb-4">
            <div class="card-header-blue d-flex justify-content-between align-items-center">
                <div>
                    <h4 class="m-0"><i class="fa-solid fa-user-graduate me-2"></i>Student Dashboard</h4>
                    <small>Welcome, <%= student.getName() %> (<%= student.getEnrollmentId() %>)</small>
                </div>
                <a href="index.jsp" class="btn btn-sm btn-light text-primary fw-bold"><i class="fa-solid fa-right-from-bracket me-1"></i>Logout</a>
            </div>
            
            <div class="p-4">
                <% if(request.getParameter("msg") != null) { %>
                    <div class="alert alert-success d-flex align-items-center gap-2"><i class="fa-solid fa-circle-check"></i> <%= request.getParameter("msg") %></div>
                <% } %>
                <% if(request.getParameter("error") != null) { %>
                    <div class="alert alert-danger d-flex align-items-center gap-2"><i class="fa-solid fa-circle-exclamation"></i> <%= request.getParameter("error") %></div>
                <% } %>

                <div class="dash-stat-grid mb-4">
                    <div class="dash-stat bg-primary bg-opacity-10 text-primary border border-primary border-opacity-25">
                        <i class="fa-solid fa-calendar-check"></i>
                        <div><div class="val"><%= percentage %>%</div><div class="lbl">Overall Attendance</div></div>
                    </div>
                    <div class="dash-stat bg-success bg-opacity-10 text-success border border-success border-opacity-25">
                        <i class="fa-solid fa-check-circle"></i>
                        <div><div class="val"><%= presentCount %></div><div class="lbl">Classes Present</div></div>
                    </div>
                    <div class="dash-stat bg-danger bg-opacity-10 text-danger border border-danger border-opacity-25">
                        <i class="fa-solid fa-circle-xmark"></i>
                        <div><div class="val"><%= (totalClasses - presentCount) %></div><div class="lbl">Classes Absent</div></div>
                    </div>
                </div>

                <div class="row mb-4">
                    <div class="col-md-6">
                        <div class="alert alert-info d-flex align-items-center gap-2 h-100 m-0">
                            <i class="fa-solid fa-circle-info fs-4"></i>
                            <div><strong>Course Information:</strong><br>Stream: <%= student.getStream() %> | Current Semester: <%= student.getSemesterId() %></div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="card border border-warning border-opacity-50 h-100 bg-warning bg-opacity-10">
                            <div class="card-body py-2 px-3">
                                <label class="form-label text-warning-emphasis fw-bold mb-1"><i class="fa-solid fa-pen-to-square me-1"></i>Promoted? Update Semester</label>
                                <form action="UpdateSemesterServlet" method="POST" class="d-flex gap-2">
                                    <select name="semesterId" class="form-select form-select-sm" required>
                                        <% for(int i=1; i<=8; i++) { %>
                                            <option value="<%= i %>" <%= (student.getSemesterId() == i) ? "selected" : "" %>>Semester <%= i %></option>
                                        <% } %>
                                    </select>
                                    <button type="submit" class="btn btn-warning btn-sm fw-bold">Update</button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>

                <h5 class="fw-bold mb-3 text-secondary text-uppercase" style="font-size: 0.9rem; letter-spacing: 0.5px;">Recent Attendance Records</h5>
                <div class="table-responsive">
                    <table class="att-table">
                        <thead>
                            <tr>
                                <th>Date</th>
                                <th>Subject</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if(records.isEmpty()) { %>
                                <tr><td colspan="3" class="text-center py-4 text-muted">No attendance records found.</td></tr>
                            <% } else {
                                for(Attendance a : records) { %>
                                <tr>
                                    <td class="fw-bold"><%= a.getDate() %></td>
                                    <td><%= a.getSubjectName() %></td>
                                    <td>
                                        <% if("P".equals(a.getStatus())) { %>
                                            <span class="badge bg-success rounded-pill px-3">Present</span>
                                        <% } else { %>
                                            <span class="badge bg-danger rounded-pill px-3">Absent</span>
                                        <% } %>
                                    </td>
                                </tr>
                            <% } } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <div class="footer-modern container">
        <div class="mb-3">
            <a href="https://vns.ac.in/" target="_blank" style="background: transparent !important; padding: 0; display: inline-block;">
                <img src="image/vns_logo.png" alt="VNS Group of Colleges" style="max-height: 55px; width: auto;">
            </a>
        </div>
        <p class="small text-muted">
            <i class="fa-solid fa-location-dot me-1"></i>Neelbud, Bhopal-462044
            &nbsp;|&nbsp;
            <i class="fa-solid fa-phone me-1"></i>+91-0755-2696654
        </p>
        <div class="social-icons">
            <!-- Instagram -->
            <a href="https://www.instagram.com/vns_bhopal/" target="_blank" class="insta" title="Instagram">
                <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                    <path d="M12 2.163c3.204 0 3.584.012 4.85.07 3.252.148 4.771 1.691 4.919 4.919.058 1.265.069 1.645.069 4.849 0 3.205-.012 3.584-.069 4.849-.149 3.225-1.664 4.771-4.919 4.919-1.266.058-1.644.07-4.85.07-3.204 0-3.584-.012-4.849-.07-3.26-.149-4.771-1.699-4.919-4.92-.058-1.265-.07-1.644-.07-4.849 0-3.204.013-3.583.07-4.849.149-3.227 1.664-4.771 4.919-4.919 1.266-.057 1.645-.069 4.849-.069zm0-2.163c-3.259 0-3.667.014-4.947.072-4.358.2-6.78 2.618-6.98 6.98-.059 1.281-.073 1.689-.073 4.948 0 3.259.014 3.668.072 4.948.2 4.358 2.618 6.78 6.98 6.98 1.281.058 1.689.072 4.948.072 3.259 0 3.668-.014 4.948-.072 4.354-.2 6.782-2.618 6.979-6.98.059-1.28.073-1.689.073-4.948 0-3.259-.014-3.667-.072-4.947-.196-4.354-2.617-6.78-6.979-6.98-1.281-.059-1.69-.073-4.949-.073zm0 5.838c-3.403 0-6.162 2.759-6.162 6.162s2.759 6.163 6.162 6.163 6.162-2.759 6.162-6.163c0-3.403-2.759-6.162-6.162-6.162zm0 10.162c-2.209 0-4-1.79-4-4 0-2.209 1.791-4 4-4s4 1.791 4 4c0 2.21-1.791 4-4 4zm6.406-11.845c-.796 0-1.441.645-1.441 1.44s.645 1.44 1.441 1.44c.795 0 1.439-.645 1.439-1.44s-.644-1.44-1.439-1.44z"/>
                </svg>
            </a>
            <!-- Facebook -->
            <a href="https://www.facebook.com/vnsbhopal/" target="_blank" class="fb" title="Facebook">
                <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                    <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/>
                </svg>
            </a>
            <!-- LinkedIn -->
            <a href="https://www.linkedin.com/school/vns-group-of-institutions/" target="_blank" class="li" title="LinkedIn">
                <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                    <path d="M20.447 20.452h-3.554v-5.569c0-1.328-.027-3.037-1.852-3.037-1.853 0-2.136 1.445-2.136 2.939v5.667H9.351V9h3.414v1.561h.046c.477-.9 1.637-1.85 3.37-1.85 3.601 0 4.267 2.37 4.267 5.455v6.286zM5.337 7.433c-1.144 0-2.063-.926-2.063-2.065 0-1.138.92-2.063 2.063-2.063 1.14 0 2.064.925 2.064 2.063 0 1.139-.925 2.065-2.064 2.065zm1.782 13.019H3.555V9h3.564v11.452zM22.225 0H1.771C.792 0 0 .774 0 1.729v20.542C0 23.227.792 24 1.771 24h20.451C23.2 24 24 23.227 24 22.271V1.729C24 .774 23.2 0 22.222 0h.003z"/>
                </svg>
            </a>
        </div>
        <p class="mt-2 mb-0 small">
            Designed &amp; Developed by
            <a href="https://portfolio-website-4o9l.onrender.com/" target="_blank">
                <i class="fa-solid fa-laptop-code me-1"></i>Monu Nayan
            </a>
        </p>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
