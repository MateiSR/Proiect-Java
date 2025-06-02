package com.javaproj.dto;

import java.time.LocalTime;

public class ScheduleResponseDTO {
    private Integer scheduleId;
    private CourseDTO course;
    private ProfessorDTO professor;
    private ClassroomDTO classroom;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String semester;
    private String academicYear;

    public ScheduleResponseDTO(Integer scheduleId, CourseDTO course, ProfessorDTO professor, ClassroomDTO classroom,
                               String dayOfWeek, LocalTime startTime, LocalTime endTime, String semester, String academicYear) {
        this.scheduleId = scheduleId;
        this.course = course;
        this.professor = professor;
        this.classroom = classroom;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.semester = semester;
        this.academicYear = academicYear;
    }

    public Integer getScheduleId() { return scheduleId; }
    public void setScheduleId(Integer scheduleId) { this.scheduleId = scheduleId; }
    public CourseDTO getCourse() { return course; }
    public void setCourse(CourseDTO course) { this.course = course; }
    public ProfessorDTO getProfessor() { return professor; }
    public void setProfessor(ProfessorDTO professor) { this.professor = professor; }
    public ClassroomDTO getClassroom() { return classroom; }
    public void setClassroom(ClassroomDTO classroom) { this.classroom = classroom; }
    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
}