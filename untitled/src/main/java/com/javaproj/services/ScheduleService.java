package com.javaproj.services;

import com.javaproj.db.*;
import com.javaproj.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final CourseRepository courseRepository;
    private final ProfessorRepository professorRepository;
    private final ClassroomRepository classroomRepository;

    @Autowired
    public ScheduleService(ScheduleRepository scheduleRepository,
                           CourseRepository courseRepository,
                           ProfessorRepository professorRepository,
                           ClassroomRepository classroomRepository) {
        this.scheduleRepository = scheduleRepository;
        this.courseRepository = courseRepository;
        this.professorRepository = professorRepository;
        this.classroomRepository = classroomRepository;
    }

    public static class ScheduleRequest {
        public Integer courseId;
        public Integer professorId;
        public Integer classroomId;
        public String dayOfWeek;
        public LocalTime startTime;
        public LocalTime endTime;
        public String semester;
        public String academicYear;

        public ScheduleRequest() {}

        public Integer getCourseId() { return courseId; }
        public void setCourseId(Integer courseId) { this.courseId = courseId; }
        public Integer getProfessorId() { return professorId; }
        public void setProfessorId(Integer professorId) { this.professorId = professorId; }
        public Integer getClassroomId() { return classroomId; }
        public void setClassroomId(Integer classroomId) { this.classroomId = classroomId; }
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


    @Transactional
    public Schedule createSchedule(ScheduleRequest scheduleRequest) {
        Course course = courseRepository.findById(scheduleRequest.courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + scheduleRequest.courseId));
        Professor professor = professorRepository.findById(scheduleRequest.professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor not found with id: " + scheduleRequest.professorId));
        Classroom classroom = classroomRepository.findById(scheduleRequest.classroomId)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with id: " + scheduleRequest.classroomId));

        if (scheduleRequest.endTime.isBefore(scheduleRequest.startTime) || scheduleRequest.endTime.equals(scheduleRequest.startTime)) {
            throw new IllegalArgumentException("End time must be after start time.");
        }

        List<Schedule> roomConflicts = scheduleRepository.findRoomConflicts(
                classroom.getRoomId(), scheduleRequest.dayOfWeek, scheduleRequest.semester, scheduleRequest.academicYear,
                scheduleRequest.startTime, scheduleRequest.endTime
        );
        if (!roomConflicts.isEmpty()) {
            throw new IllegalStateException("Room conflict detected for classroom " + classroom.getRoomNumber() + " at the given time slot.");
        }

        List<Schedule> professorConflicts = scheduleRepository.findProfessorConflicts(
                professor.getProfessorId(), scheduleRequest.dayOfWeek, scheduleRequest.semester, scheduleRequest.academicYear,
                scheduleRequest.startTime, scheduleRequest.endTime
        );
        if (!professorConflicts.isEmpty()) {
            throw new IllegalStateException("Professor " + professor.getFirstName() + " " + professor.getLastName() + " conflict detected for the given time slot.");
        }

        Schedule schedule = new Schedule();
        schedule.setCourse(course);
        schedule.setProfessor(professor);
        schedule.setClassroom(classroom);
        schedule.setDayOfWeek(scheduleRequest.dayOfWeek);
        schedule.setStartTime(scheduleRequest.startTime);
        schedule.setEndTime(scheduleRequest.endTime);
        schedule.setSemester(scheduleRequest.semester);
        schedule.setAcademicYear(scheduleRequest.academicYear);

        return scheduleRepository.save(schedule);
    }

    @Transactional(readOnly = true)
    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Schedule> getScheduleById(Integer scheduleId) {
        return scheduleRepository.findById(scheduleId);
    }

    @Transactional(readOnly = true)
    public List<Schedule> getSchedulesByCourseId(Integer courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        return scheduleRepository.findByCourse(course);
    }

    @Transactional(readOnly = true)
    public List<Schedule> getSchedulesByProfessorId(Integer professorId) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor not found with id: " + professorId));
        return scheduleRepository.findByProfessor(professor);
    }

    @Transactional(readOnly = true)
    public List<Schedule> getSchedulesByClassroomId(Integer classroomId) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with id: " + classroomId));
        return scheduleRepository.findByClassroom(classroom);
    }

    @Transactional(readOnly = true)
    public List<Schedule> findRoomConflicts(Integer roomId, String dayOfWeek, String semester, String academicYear, LocalTime startTime, LocalTime endTime) {
        if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
            throw new IllegalArgumentException("End time must be after start time for conflict checking.");
        }
        return scheduleRepository.findRoomConflicts(roomId, dayOfWeek, semester, academicYear, startTime, endTime);
    }

    @Transactional(readOnly = true)
    public List<Schedule> findProfessorConflicts(Integer professorId, String dayOfWeek, String semester, String academicYear, LocalTime startTime, LocalTime endTime) {
        if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
            throw new IllegalArgumentException("End time must be after start time for conflict checking.");
        }
        return scheduleRepository.findProfessorConflicts(professorId, dayOfWeek, semester, academicYear, startTime, endTime);
    }

    @Transactional
    public Schedule updateSchedule(Integer scheduleId, ScheduleRequest scheduleRequestDetails) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + scheduleId));

        Course course = courseRepository.findById(scheduleRequestDetails.courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + scheduleRequestDetails.courseId));
        Professor professor = professorRepository.findById(scheduleRequestDetails.professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor not found with id: " + scheduleRequestDetails.professorId));
        Classroom classroom = classroomRepository.findById(scheduleRequestDetails.classroomId)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with id: " + scheduleRequestDetails.classroomId));

        if (scheduleRequestDetails.endTime.isBefore(scheduleRequestDetails.startTime) || scheduleRequestDetails.endTime.equals(scheduleRequestDetails.startTime)) {
            throw new IllegalArgumentException("End time must be after start time.");
        }

        List<Schedule> roomConflicts = scheduleRepository.findRoomConflicts(
                classroom.getRoomId(), scheduleRequestDetails.dayOfWeek, scheduleRequestDetails.semester, scheduleRequestDetails.academicYear,
                scheduleRequestDetails.startTime, scheduleRequestDetails.endTime
        ).stream().filter(s -> s.getScheduleId() != scheduleId).collect(Collectors.toList());

        if (!roomConflicts.isEmpty()) {
            throw new IllegalStateException("Room conflict detected for classroom " + classroom.getRoomNumber() + " for the updated time slot.");
        }


        List<Schedule> professorConflicts = scheduleRepository.findProfessorConflicts(
                professor.getProfessorId(), scheduleRequestDetails.dayOfWeek, scheduleRequestDetails.semester, scheduleRequestDetails.academicYear,
                scheduleRequestDetails.startTime, scheduleRequestDetails.endTime
        ).stream().filter(s -> s.getScheduleId() != scheduleId).collect(Collectors.toList());

        if (!professorConflicts.isEmpty()) {
            throw new IllegalStateException("Professor " + professor.getFirstName() + " " + professor.getLastName() + " conflict detected for the updated time slot.");
        }

        schedule.setCourse(course);
        schedule.setProfessor(professor);
        schedule.setClassroom(classroom);
        schedule.setDayOfWeek(scheduleRequestDetails.dayOfWeek);
        schedule.setStartTime(scheduleRequestDetails.startTime);
        schedule.setEndTime(scheduleRequestDetails.endTime);
        schedule.setSemester(scheduleRequestDetails.semester);
        schedule.setAcademicYear(scheduleRequestDetails.academicYear);

        return scheduleRepository.save(schedule);
    }

    @Transactional
    public void deleteSchedule(Integer scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + scheduleId));
        scheduleRepository.delete(schedule);
    }
}