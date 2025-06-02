package com.javaproj.dto;

public class ProfessorDTO {
    private Integer professorId;
    private String firstName;
    private String lastName;

    public ProfessorDTO(Integer professorId, String firstName, String lastName) {
        this.professorId = professorId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Integer getProfessorId() { return professorId; }
    public void setProfessorId(Integer professorId) { this.professorId = professorId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
}