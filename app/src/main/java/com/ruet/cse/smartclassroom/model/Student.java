package com.ruet.cse.smartclassroom.model;
public class Student {

    private String uid;
    private String email;
    private int semester;
    private String section;

    public Student() {
    }

    public Student(String uid, String email, int semester, String section) {
        this.uid = uid;
        this.email = email;
        this.semester = semester;
        this.section = section;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }
}