package com.javaproj.dto;

import java.time.LocalTime;
import java.util.List;

public class ScheduleGenerationRequest {
    private List<Integer> courseIds;
    private String semester;
    private String academicYear;
    private List<String> daysOfWeek;
    private List<LocalTime> startTimes;
    private int defaultDurationHours = 2;

    public List<Integer> getCourseIds() {
        return courseIds;
    }

    public void setCourseIds(List<Integer> courseIds) {
        this.courseIds = courseIds;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public List<String> getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(List<String> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public List<LocalTime> getStartTimes() {
        return startTimes;
    }

    public void setStartTimes(List<LocalTime> startTimes) {
        this.startTimes = startTimes;
    }

    public int getDefaultDurationHours() {
        return defaultDurationHours;
    }

    public void setDefaultDurationHours(int defaultDurationHours) {
        this.defaultDurationHours = defaultDurationHours;
    }
}