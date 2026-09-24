package com.ruet.cse.smartclassroom.model;

/** Room status model used by the Empty Classroom Finder screen. */
public class Classroom {
    private String roomNumber;
    private boolean occupied;
    private String occupiedByCourse;
    private String freeAt;

    public Classroom() { }

    public Classroom(String roomNumber, boolean occupied, String occupiedByCourse, String freeAt) {
        this.roomNumber = roomNumber;
        this.occupied = occupied;
        this.occupiedByCourse = occupiedByCourse;
        this.freeAt = freeAt;
    }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public boolean isOccupied() { return occupied; }
    public void setOccupied(boolean occupied) { this.occupied = occupied; }
    public String getOccupiedByCourse() { return occupiedByCourse; }
    public void setOccupiedByCourse(String occupiedByCourse) { this.occupiedByCourse = occupiedByCourse; }
    public String getFreeAt() { return freeAt; }
    public void setFreeAt(String freeAt) { this.freeAt = freeAt; }
}
