package com.ruet.cse.smartclassroom.model;
public class Teacher {

    private String id;
    private String code;
    private String name;

    private boolean currentlyTeaching;

    private String currentCourse;
    private String currentRoom;
    private String freeAfter;
    private String nextClassInfo;

    public Teacher() {
    }

    public Teacher(
            String id,
            String code,
            String name,
            boolean currentlyTeaching,
            String currentCourse,
            String currentRoom,
            String freeAfter,
            String nextClassInfo) {

        this.id = id;
        this.code = code;
        this.name = name;
        this.currentlyTeaching = currentlyTeaching;
        this.currentCourse = currentCourse;
        this.currentRoom = currentRoom;
        this.freeAfter = freeAfter;
        this.nextClassInfo = nextClassInfo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCurrentlyTeaching() {
        return currentlyTeaching;
    }

    public void setCurrentlyTeaching(
            boolean currentlyTeaching) {

        this.currentlyTeaching =
                currentlyTeaching;
    }

    public String getCurrentCourse() {
        return currentCourse;
    }

    public void setCurrentCourse(
            String currentCourse) {

        this.currentCourse =
                currentCourse;
    }

    public String getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(
            String currentRoom) {

        this.currentRoom =
                currentRoom;
    }

    public String getFreeAfter() {
        return freeAfter;
    }

    public void setFreeAfter(
            String freeAfter) {

        this.freeAfter =
                freeAfter;
    }

    public String getNextClassInfo() {
        return nextClassInfo;
    }

    public void setNextClassInfo(
            String nextClassInfo) {

        this.nextClassInfo =
                nextClassInfo;
    }
}