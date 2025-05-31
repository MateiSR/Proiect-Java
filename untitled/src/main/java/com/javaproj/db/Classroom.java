package com.javaproj.db;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.Set;
import java.util.HashSet;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@Entity
@Table(name = "Classrooms")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private int roomId;

    @Column(name = "room_number", nullable = false, unique = true, length = 20)
    private String roomNumber;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @Column(name = "has_projector")
    private boolean hasProjector;

    @Column(name = "building", length = 100)
    private String building;

    // FIXED:
    /*In a bidirectional relationship, @JsonManagedReference is typically used on the "parent" side
     (the "one" in a one-to-many, or the forward part of the reference that gets serialized),
     and @JsonBackReference is on the "child" side (the "many" in a one-to-many).
     */
    @OneToMany(mappedBy = "classroom", fetch = FetchType.LAZY)
    @JsonManagedReference("schedule-classroom")
    private Set<Schedule> schedules = new HashSet<>();

    public Classroom() {
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isHasProjector() {
        return hasProjector;
    }

    public void setHasProjector(boolean hasProjector) {
        this.hasProjector = hasProjector;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public Set<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(Set<Schedule> schedules) {
        this.schedules = schedules;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Classroom classroom = (Classroom) o;
        return roomId == classroom.roomId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(roomId);
    }
}