package com.cams.model;

import java.util.List;
import java.util.ArrayList;

public class Faculty {
    private int id;
    private String name;
    private String employeeId;
    private String email;
    private String password;
    private String role;
    private String department;
    private List<Integer> assignedSemesters = new ArrayList<>();

    // Getters and Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmployeeId() {
        return employeeId;
    }
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }

    public List<Integer> getAssignedSemesters() {
        return assignedSemesters;
    }
    public void setAssignedSemesters(List<Integer> assignedSemesters) {
        this.assignedSemesters = assignedSemesters;
    }
}
