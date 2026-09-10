package com.cams.dao;

import com.cams.model.Student;
import com.cams.model.Faculty;
import com.cams.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // Check if Student enrollment_id or email already exists
    public boolean isStudentExists(String enrollmentId, String email) {
        String sql = "SELECT id FROM students WHERE enrollment_id = ? OR LOWER(email) = LOWER(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, enrollmentId.trim());
            ps.setString(2, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Check if Faculty employee_id or email already exists
    public boolean isFacultyExists(String employeeId, String email) {
        String sql = "SELECT id FROM faculty WHERE employee_id = ? OR LOWER(email) = LOWER(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId.trim());
            ps.setString(2, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean registerStudent(Student student) {
        String sql = "INSERT INTO students (name, enrollment_id, email, password, stream, semester_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getName());
            ps.setString(2, student.getEnrollmentId());
            ps.setString(3, student.getEmail());
            ps.setString(4, student.getPassword());
            ps.setString(5, student.getStream());
            ps.setInt(6, student.getSemesterId());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean registerFaculty(Faculty faculty, String[] semesterIds) {
        String sql = "INSERT INTO faculty (name, employee_id, email, password, role, department) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, faculty.getName());
                ps.setString(2, faculty.getEmployeeId());
                ps.setString(3, faculty.getEmail());
                ps.setString(4, faculty.getPassword());
                ps.setString(5, faculty.getRole());
                ps.setString(6, faculty.getDepartment());
                
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            int facultyId = rs.getInt(1);
                            
                            // Insert into faculty_semester table
                            if (semesterIds != null && semesterIds.length > 0) {
                                String mapSql = "INSERT INTO faculty_semester (faculty_id, semester_id) VALUES (?, ?)";
                                try (PreparedStatement mapPs = conn.prepareStatement(mapSql)) {
                                    for (String semIdStr : semesterIds) {
                                        try {
                                            int semId = Integer.parseInt(semIdStr.trim());
                                            mapPs.setInt(1, facultyId);
                                            mapPs.setInt(2, semId);
                                            mapPs.addBatch();
                                        } catch (NumberFormatException ignored) {}
                                    }
                                    mapPs.executeBatch();
                                }
                            }
                        }
                    }
                    conn.commit();
                    return true;
                }
            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    public Student loginStudent(String enrollmentId, String password) {
        String sql = "SELECT * FROM students WHERE enrollment_id = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, enrollmentId);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Faculty loginFaculty(String employeeId, String password) {
        String sql = "SELECT * FROM faculty WHERE employee_id = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Faculty faculty = new Faculty();
                    int fid = rs.getInt("id");
                    faculty.setId(fid);
                    faculty.setName(rs.getString("name"));
                    faculty.setEmployeeId(rs.getString("employee_id"));
                    faculty.setEmail(rs.getString("email"));
                    faculty.setRole(rs.getString("role"));
                    faculty.setDepartment(rs.getString("department"));
                    
                    // Load assigned semesters
                    faculty.setAssignedSemesters(getFacultySemesters(fid));
                    return faculty;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Integer> getFacultySemesters(int facultyId) {
        List<Integer> semesters = new ArrayList<>();
        String sql = "SELECT semester_id FROM faculty_semester WHERE faculty_id = ? ORDER BY semester_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, facultyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    semesters.add(rs.getInt("semester_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return semesters;
    }
    
    public boolean updateStudentSemester(int studentId, int newSemesterId) {
        String sql = "UPDATE students SET semester_id = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newSemesterId);
            ps.setInt(2, studentId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Returns all subjects for a given stream
    public List<String[]> getSubjectsByStream(String stream) {
        List<String[]> subjects = new ArrayList<>();
        String sql = "SELECT id, subject_name FROM subjects WHERE stream = ? ORDER BY subject_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, stream);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    subjects.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("subject_name")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subjects;
    }

    // Add a new subject for a given stream
    public boolean addSubject(String subjectName, String stream) {
        String sql = "INSERT INTO subjects (subject_name, stream) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, subjectName);
            ps.setString(2, stream);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete a subject by its id
    public boolean deleteSubject(int subjectId) {
        String sql = "DELETE FROM subjects WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, subjectId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Verify student by enrollment_id and email
    public boolean verifyStudentEmail(String enrollmentId, String email) {
        if (enrollmentId == null || email == null) return false;
        String sql = "SELECT id FROM students WHERE enrollment_id = ? AND LOWER(TRIM(email)) = LOWER(TRIM(?))";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, enrollmentId.trim());
            ps.setString(2, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Verify faculty by employee_id and email
    public boolean verifyFacultyEmail(String employeeId, String email) {
        if (employeeId == null || email == null) return false;
        String sql = "SELECT id FROM faculty WHERE employee_id = ? AND LOWER(TRIM(email)) = LOWER(TRIM(?))";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId.trim());
            ps.setString(2, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update student password by enrollment_id
    public boolean updateStudentPassword(String enrollmentId, String newPassword) {
        String sql = "UPDATE students SET password = ? WHERE enrollment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setString(2, enrollmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update faculty password by employee_id
    public boolean updateFacultyPassword(String employeeId, String newPassword) {
        String sql = "UPDATE faculty SET password = ? WHERE employee_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setString(2, employeeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}


