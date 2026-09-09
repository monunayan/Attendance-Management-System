package com.cams.dao;

import com.cams.model.Attendance;
import com.cams.model.Student;
import com.cams.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    public boolean markAttendance(int studentId, int facultyId, int subjectId, Date date, String status) {
        String sql = "INSERT INTO attendance (student_id, faculty_id, subject_id, date, status) VALUES (?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE status = VALUES(status), faculty_id = VALUES(faculty_id)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, facultyId);
            ps.setInt(3, subjectId);
            ps.setDate(4, date);
            ps.setString(5, status);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Student> getStudentsByStreamAndSemester(String stream, int semesterId) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE stream = ? AND semester_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, stream);
            ps.setInt(2, semesterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Student s = new Student();
                    s.setId(rs.getInt("id"));
                    s.setName(rs.getString("name"));
                    s.setEnrollmentId(rs.getString("enrollment_id"));
                    s.setStream(rs.getString("stream"));
                    s.setSemesterId(rs.getInt("semester_id"));
                    students.add(s);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public List<Attendance> getStudentAttendance(int studentId) {
        List<Attendance> records = new ArrayList<>();
        String sql = "SELECT a.*, s.subject_name FROM attendance a " +
                     "JOIN subjects s ON a.subject_id = s.id " +
                     "WHERE a.student_id = ? ORDER BY a.date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Attendance att = new Attendance();
                    att.setStudentId(rs.getInt("student_id"));
                    att.setFacultyId(rs.getInt("faculty_id"));
                    att.setSubjectId(rs.getInt("subject_id"));
                    att.setDate(rs.getDate("date"));
                    att.setStatus(rs.getString("status"));
                    att.setSubjectName(rs.getString("subject_name"));
                    records.add(att);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }
}
