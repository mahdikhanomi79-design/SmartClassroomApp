package com.ruet.cse.smartclassroom.model;
public class ClassSchedule {
    private String id;
    private String courseName;
    private String courseCode;
    private String teacherName;
    private String teacherId;
    private String room;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private int semester;

    public ClassSchedule() { }

    public ClassSchedule(String id, String courseName, String courseCode, String teacherName,
                          String teacherId, String room, String dayOfWeek,
                          String startTime, String endTime, int semester) {
        this.id = id;
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.teacherName = teacherName;
        this.teacherId = teacherId;
        this.room = room;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.semester = semester;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    public String getTeacherId() { return teacherId; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }
    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }
}
