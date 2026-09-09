<%@ page import="com.cams.model.Faculty" %>
<%@ page import="com.cams.model.Student" %>
<%@ page import="com.cams.dao.AttendanceDAO" %>
<%@ page import="com.cams.dao.UserDAO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    Object currentUserObj = session.getAttribute("currentUser");
    if (currentUserObj == null || !(currentUserObj instanceof Faculty) || !"faculty".equals(session.getAttribute("userType"))) {
        response.sendRedirect("index.jsp?error=Session expired or invalid access. Please login again.");
        return;
    }
    Faculty faculty = (Faculty) currentUserObj;

    // Which semester tab is active
    String activeSemStr = request.getParameter("activeSem");
    int activeSem = 0;
    List<Integer> assignedSems = faculty.getAssignedSemesters();
    if (activeSemStr != null && !activeSemStr.isEmpty()) {
        try { activeSem = Integer.parseInt(activeSemStr); } catch(Exception e) {}
    }
    // Default to first assigned semester if none selected
    if (activeSem == 0 && assignedSems != null && !assignedSems.isEmpty()) {
        activeSem = assignedSems.get(0);
    }

    // Load students for the active semester
    AttendanceDAO attDao = new AttendanceDAO();
    List<Student> studentList = new ArrayList<>();
    if (activeSem > 0) {
        studentList = attDao.getStudentsByStreamAndSemester(faculty.getDepartment(), activeSem);
    }

    // Load subjects for faculty's department
    UserDAO userDAO = new UserDAO();
    List<String[]> subjects = userDAO.getSubjectsByStream(faculty.getDepartment());
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Faculty Dashboard - CAMS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700;800&display=swap" rel="stylesheet">
    <link href="css/style.css" rel="stylesheet">
    <style>
        .sem-btn {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            min-width: 120px;
            padding: 0.6rem 1.2rem;
            border-radius: 50px;
            font-weight: 700;
            font-size: 0.9rem;
            cursor: pointer;
            border: 2px solid #dc3545;
            background: #fff;
            color: #dc3545;
            transition: all 0.22s ease;
            text-decoration: none;
        }
        .sem-btn:hover, .sem-btn.active {
            background: #dc3545;
            color: #fff;
            transform: translateY(-2px);
            box-shadow: 0 4px 16px rgba(220,53,69,0.25);
        }
        .sem-btn i { margin-right: 6px; }
        .section-card {
            background: #fff;
            border-radius: 16px;
            border: 1px solid #e9ecef;
            box-shadow: 0 2px 12px rgba(0,0,0,0.06);
            margin-bottom: 1.5rem;
        }
        .section-card-header {
            background: linear-gradient(135deg,#dc3545,#b02a37);
            color: #fff;
            border-radius: 16px 16px 0 0;
            padding: 1rem 1.5rem;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }
        .section-card-body { padding: 1.25rem 1.5rem; }
        .no-sem-msg {
            background: #fff8e1;
            border: 1.5px dashed #ffc107;
            border-radius: 12px;
            padding: 2rem;
            text-align: center;
            color: #856404;
        }
        .subject-badge {
            display: inline-flex; align-items: center;
            background: #f8f9fa; border: 1px solid #dee2e6;
            border-radius: 50px; padding: 4px 6px 4px 14px; font-size: 0.85rem;
            margin: 3px; gap: 6px;
        }
        .subject-badge .del-btn {
            display: inline-flex; align-items: center; justify-content: center;
            width: 22px; height: 22px; border-radius: 50%;
            border: none; background: #dc3545; color: #fff;
            font-size: 0.7rem; cursor: pointer; padding: 0;
            transition: background 0.18s, transform 0.15s;
            flex-shrink: 0;
        }
        .subject-badge .del-btn:hover {
            background: #b02a37; transform: scale(1.15);
        }
        .add-subject-card {
            background: linear-gradient(135deg, #f8f9fa, #e9f7ef);
            border: 1.5px dashed #28a745;
            border-radius: 12px;
            padding: 1.25rem;
        }
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

        <!-- Dashboard Header -->
        <div class="section-card mb-4">
            <div class="section-card-header">
                <div>
                    <h4 class="m-0 fw-bold"><i class="fa-solid fa-chalkboard-user me-2"></i>Faculty Dashboard</h4>
                    <small class="opacity-85"><%= faculty.getRole() %> &mdash; <%= faculty.getName() %> &nbsp;|&nbsp; Dept: <%= faculty.getDepartment() %></small>
                </div>
                <a href="index.jsp" class="btn btn-sm btn-light text-danger fw-bold">
                    <i class="fa-solid fa-right-from-bracket me-1"></i>Logout
                </a>
            </div>

            <div class="section-card-body">
                <% if(request.getParameter("msg") != null) { %>
                    <div class="alert alert-success d-flex align-items-center gap-2">
                        <i class="fa-solid fa-circle-check"></i> <%= request.getParameter("msg") %>
                    </div>
                <% } %>
                <% if(request.getParameter("error") != null) { %>
                    <div class="alert alert-danger d-flex align-items-center gap-2">
                        <i class="fa-solid fa-circle-exclamation"></i> <%= request.getParameter("error") %>
                    </div>
                <% } %>

                <!-- Semester Buttons -->
                <h6 class="fw-bold text-muted mb-3"><i class="fa-solid fa-layer-group me-1"></i>Your Assigned Semesters</h6>
                <% if (assignedSems == null || assignedSems.isEmpty()) { %>
                    <div class="no-sem-msg">
                        <i class="fa-solid fa-triangle-exclamation fa-2x mb-2"></i>
                        <p class="mb-0 fw-bold">No semesters assigned to your account.</p>
                        <small>Please contact the administrator to assign semesters during registration.</small>
                    </div>
                <% } else { %>
                    <div class="d-flex flex-wrap gap-2 mb-2">
                        <% for (int sem : assignedSems) { %>
                            <a href="faculty_dashboard.jsp?activeSem=<%= sem %>"
                               class="sem-btn <%= (activeSem == sem) ? "active" : "" %>">
                                <i class="fa-solid fa-book-open"></i> Semester <%= sem %>
                            </a>
                        <% } %>
                    </div>
                <% } %>
            </div>
        </div>

        <% if (activeSem > 0) { %>

        <!-- Student Attendance Section -->
        <div class="section-card">
            <div class="section-card-header">
                <span class="fw-bold"><i class="fa-solid fa-users me-2"></i>Semester <%= activeSem %> &mdash; Student Attendance</span>
                <span class="badge bg-light text-dark fw-bold px-3 py-2">
                    <%= studentList.size() %> student<%= studentList.size() != 1 ? "s" : "" %>
                </span>
            </div>
            <div class="section-card-body">
                <% if (studentList.isEmpty()) { %>
                    <div class="text-center py-4 text-muted">
                        <i class="fa-solid fa-user-slash fa-2x mb-2 d-block"></i>
                        No students found in <strong><%= faculty.getDepartment() %></strong> &ndash; Semester <%= activeSem %>.
                        <br><small>Students must register with matching Stream and Semester.</small>
                    </div>
                <% } else { %>
                    <form action="AttendanceServlet" method="POST">
                        <input type="hidden" name="activeSem" value="<%= activeSem %>">
                        <div class="row mb-3">
                            <div class="col-md-5">
                                <label class="form-label fw-bold"><i class="fa-solid fa-book me-1 text-danger"></i>Select Subject</label>
                                <select name="subjectId" class="form-select" required>
                                    <option value="" disabled selected>-- Choose Subject --</option>
                                    <% if (subjects.isEmpty()) { %>
                                        <option disabled>No subjects added yet — add below</option>
                                    <% } else {
                                        for (String[] sub : subjects) { %>
                                            <option value="<%= sub[0] %>"><%= sub[1] %></option>
                                    <%  } } %>
                                </select>
                            </div>
                        </div>

                        <div class="table-responsive mb-3">
                            <table class="att-table">
                                <thead>
                                    <tr>
                                        <th>#</th>
                                        <th>Enrollment ID</th>
                                        <th>Student Name</th>
                                        <th>Stream</th>
                                        <th>Attendance</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <% int rowNum = 1; for(Student s : studentList) { %>
                                    <tr>
                                        <td class="text-muted"><%= rowNum++ %></td>
                                        <td class="fw-bold"><%= s.getEnrollmentId() %></td>
                                        <td><%= s.getName() %></td>
                                        <td>
                                            <span class="badge bg-primary bg-opacity-10 text-primary border border-primary border-opacity-25">
                                                <%= s.getStream() %>
                                            </span>
                                        </td>
                                        <td>
                                            <input type="hidden" name="studentId" value="<%= s.getId() %>">
                                            <div class="att-toggle">
                                                <input type="radio" name="status_<%= s.getId() %>" id="p_<%= s.getId() %>" value="P" checked>
                                                <label for="p_<%= s.getId() %>" class="present">Present</label>

                                                <input type="radio" name="status_<%= s.getId() %>" id="a_<%= s.getId() %>" value="A">
                                                <label for="a_<%= s.getId() %>" class="absent">Absent</label>
                                            </div>
                                        </td>
                                    </tr>
                                    <% } %>
                                </tbody>
                            </table>
                        </div>
                        <button type="submit" class="btn btn-success fw-bold px-4 py-2">
                            <i class="fa-solid fa-floppy-disk me-2"></i>Save Attendance
                        </button>
                    </form>
                <% } %>
            </div>
        </div>

        <!-- Add Subject Section -->
        <div class="section-card">
            <div class="section-card-header">
                <span class="fw-bold"><i class="fa-solid fa-plus-circle me-2"></i>Manage Subjects &mdash; <%= faculty.getDepartment() %></span>
            </div>
            <div class="section-card-body">
                <!-- Current subjects list -->
                <% if (!subjects.isEmpty()) { %>
                    <p class="fw-bold mb-2 text-muted small">Current subjects (<%= subjects.size() %>):</p>
                    <div class="mb-3">
                        <% for (String[] sub : subjects) { %>
                            <span class="subject-badge">
                                <i class="fa-solid fa-book-open text-primary"></i>
                                <%= sub[1] %>
                                <form action="DeleteSubjectServlet" method="POST" class="d-inline m-0 p-0"
                                      onsubmit="return confirmDelete('<%= sub[1].replace("'","\\'") %>')">
                                    <input type="hidden" name="subjectId" value="<%= sub[0] %>">
                                    <input type="hidden" name="activeSem" value="<%= activeSem %>">
                                    <button type="submit" class="del-btn" title="Delete subject">
                                        <i class="fa-solid fa-xmark"></i>
                                    </button>
                                </form>
                            </span>
                        <% } %>
                    </div>
                <% } %>
                <!-- Add subject form -->
                <div class="add-subject-card">
                    <p class="fw-bold mb-3 text-success"><i class="fa-solid fa-plus me-1"></i>Add New Subject for <%= faculty.getDepartment() %></p>
                    <form action="AddSubjectServlet" method="POST" class="row g-2 align-items-end">
                        <input type="hidden" name="stream" value="<%= faculty.getDepartment() %>">
                        <input type="hidden" name="activeSem" value="<%= activeSem %>">
                        <div class="col-md-8">
                            <label class="form-label">Subject Name</label>
                            <input type="text" name="subjectName" class="form-control" required
                                   placeholder="e.g. Operating Systems, Mathematics III...">
                        </div>
                        <div class="col-md-4">
                            <button type="submit" class="btn btn-success fw-bold w-100">
                                <i class="fa-solid fa-plus me-1"></i>Add Subject
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <% } %>

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
    <script>
        function confirmDelete(name) {
            return confirm('"' + name + '" subject delete karna chahte hain?\nIs subject ki sari attendance records bhi delete ho sakti hain!');
        }
    </script>
</body>
</html>
