package com.cams.model;

public class Student {
    private int id;
    private String name;
    private String enrollmentId;
    private String email;
    private String password;
    private String stream;
    private int semesterId;

    public Student() {}

    public Student(int id, String name, String enrollmentId, String email, String password, String stream, int semesterId) {
        this.id = id;
        this.name = name;
        this.enrollmentId = enrollmentId;
        this.email = email;
        this.password = password;
        this.stream = stream;
        this.semesterId = semesterId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(String enrollmentId) { this.enrollmentId = enrollmentId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getStream() { return stream; }
    public void setStream(String stream) { this.stream = stream; }

    public int getSemesterId() { return semesterId; }
    public void setSemesterId(int semesterId) { this.semesterId = semesterId; }
}

