package com.javaproj.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EnrollmentResponseDTO {
    private Integer enrollmentId;
    private StudentDTO student;
    private ScheduleResponseDTO schedule;
    private LocalDate enrollmentDate;
    private BigDecimal grade;

    public EnrollmentResponseDTO(Integer enrollmentId, StudentDTO student, ScheduleResponseDTO schedule,
                                 LocalDate enrollmentDate, BigDecimal grade) {
        this.enrollmentId = enrollmentId;
        this.student = student;
        this.schedule = schedule;
        this.enrollmentDate = enrollmentDate;
        this.grade = grade;
    }

    public Integer getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(Integer enrollmentId) { this.enrollmentId = enrollmentId; }
    public StudentDTO getStudent() { return student; }
    public void setStudent(StudentDTO student) { this.student = student; }
    public ScheduleResponseDTO getSchedule() { return schedule; }
    public void setSchedule(ScheduleResponseDTO schedule) { this.schedule = schedule; }
    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }
    public BigDecimal getGrade() { return grade; }
    public void setGrade(BigDecimal grade) { this.grade = grade; }
}