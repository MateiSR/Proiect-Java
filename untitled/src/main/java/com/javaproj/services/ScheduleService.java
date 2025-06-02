package com.javaproj.services;

import com.javaproj.db.*;
import com.javaproj.dto.ClassroomDTO; // Import DTOs
import com.javaproj.dto.CourseDTO;
import com.javaproj.dto.ProfessorDTO;
import com.javaproj.dto.ScheduleResponseDTO;
import com.javaproj.exceptions.ResourceNotFoundException;
import com.javaproj.dto.ScheduleGenerationRequest; // Assuming this was created earlier
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;
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

    // Existing ScheduleRequest static class remains the same...
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

    // Convert entity to DTO
    private ScheduleResponseDTO convertToDTO(Schedule schedule) {
        if (schedule == null) {
            return null;
        }

        Course course = schedule.getCourse();
        Professor professor = schedule.getProfessor();
        Classroom classroom = schedule.getClassroom();

        CourseDTO courseDTO = (course != null)
                ? new CourseDTO(course.getCourseId(), course.getCourseCode(), course.getCourseName())
                : null;
        ProfessorDTO professorDTO = (professor != null)
                ? new ProfessorDTO(professor.getProfessorId(), professor.getFirstName(), professor.getLastName())
                : null;
        ClassroomDTO classroomDTO = (classroom != null)
                ? new ClassroomDTO(classroom.getRoomId(), classroom.getRoomNumber(), classroom.getCapacity())
                : null;

        return new ScheduleResponseDTO(
                schedule.getScheduleId(),
                courseDTO,
                professorDTO,
                classroomDTO,
                schedule.getDayOfWeek(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getSemester(),
                schedule.getAcademicYear()
        );
    }

    @Transactional
    public ScheduleResponseDTO createSchedule(ScheduleRequest scheduleRequest) {
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

        Schedule savedSchedule = scheduleRepository.save(schedule);
        return convertToDTO(savedSchedule); // Convert to DTO before returning
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDTO> getAllSchedules() {
        return scheduleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ScheduleResponseDTO> getScheduleById(Integer scheduleId) {
        return scheduleRepository.findById(scheduleId).map(this::convertToDTO);
    }

    // Method to convert list of Schedule entities to list of ScheduleResponseDTOs
    private List<ScheduleResponseDTO> convertToDTOList(List<Schedule> schedules) {
        return schedules.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDTO> getSchedulesByCourseId(Integer courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        return convertToDTOList(scheduleRepository.findByCourse(course));
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDTO> getSchedulesByProfessorId(Integer professorId) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor not found with id: " + professorId));
        return convertToDTOList(scheduleRepository.findByProfessor(professor));
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDTO> getSchedulesByClassroomId(Integer classroomId) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with id: " + classroomId));
        return convertToDTOList(scheduleRepository.findByClassroom(classroom));
    }

    // findRoomConflicts and findProfessorConflicts can still return List<Schedule>
    // or be adapted to return DTOs if needed directly by the frontend for conflict display.
    // For now, keeping them as is, as they are primarily for internal conflict checking.
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
    public ScheduleResponseDTO updateSchedule(Integer scheduleId, ScheduleRequest scheduleRequestDetails) {
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

        // Conflict checking logic... (ensure it filters out the current schedule being updated)
        List<Schedule> roomConflicts = scheduleRepository.findRoomConflicts(
                classroom.getRoomId(), scheduleRequestDetails.dayOfWeek, scheduleRequestDetails.semester, scheduleRequestDetails.academicYear,
                scheduleRequestDetails.startTime, scheduleRequestDetails.endTime
        ).stream().filter(s -> !s.getScheduleId().equals(scheduleId)).collect(Collectors.toList());

        if (!roomConflicts.isEmpty()) {
            throw new IllegalStateException("Room conflict detected for classroom " + classroom.getRoomNumber() + " for the updated time slot.");
        }

        List<Schedule> professorConflicts = scheduleRepository.findProfessorConflicts(
                professor.getProfessorId(), scheduleRequestDetails.dayOfWeek, scheduleRequestDetails.semester, scheduleRequestDetails.academicYear,
                scheduleRequestDetails.startTime, scheduleRequestDetails.endTime
        ).stream().filter(s -> !s.getScheduleId().equals(scheduleId)).collect(Collectors.toList());

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

        Schedule updatedSchedule = scheduleRepository.save(schedule);
        return convertToDTO(updatedSchedule); // dto
    }

    @Transactional
    public void deleteSchedule(Integer scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + scheduleId));
        scheduleRepository.delete(schedule);
    }

    @Transactional
    public List<ScheduleResponseDTO> generateAutomaticSchedule(ScheduleGenerationRequest request) {
        List<Schedule> generatedSchedulesInternal = new ArrayList<>();
        List<Course> coursesToSchedule = new ArrayList<>();
        for (Integer courseId : request.getCourseIds()) {
            coursesToSchedule.add(courseRepository.findById(courseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId)));
        }

        List<Professor> allProfessors = professorRepository.findAll();
        List<Classroom> allClassrooms = classroomRepository.findAll();

        for (Course course : coursesToSchedule) {
            boolean scheduled = false;
            List<Professor> suitableProfessors = allProfessors.stream()
                    .filter(p -> p.getDepartment().equalsIgnoreCase(course.getDepartment()))
                    .collect(Collectors.toList());

            if (suitableProfessors.isEmpty()) {
                suitableProfessors = allProfessors;
            }
            if (suitableProfessors.isEmpty()){
                System.err.println("No professors available to schedule course: " + course.getCourseName());
                continue;
            }

            for (String day : request.getDaysOfWeek()) {
                if (scheduled) break;
                for (LocalTime startTime : request.getStartTimes()) {
                    if (scheduled) break;
                    LocalTime endTime = startTime.plusHours(request.getDefaultDurationHours());

                    for (Professor professor : suitableProfessors) {
                        if (scheduled) break;
                        List<Schedule> professorConflicts = scheduleRepository.findProfessorConflicts(
                                professor.getProfessorId(), day, request.getSemester(), request.getAcademicYear(), startTime, endTime);
                        if (!professorConflicts.isEmpty()) {
                            continue;
                        }

                        for (Classroom classroom : allClassrooms) {
                            List<Schedule> roomConflicts = scheduleRepository.findRoomConflicts(
                                    classroom.getRoomId(), day, request.getSemester(), request.getAcademicYear(), startTime, endTime);
                            if (!roomConflicts.isEmpty()) {
                                continue;
                            }

                            Schedule newSchedule = new Schedule();
                            newSchedule.setCourse(course);
                            newSchedule.setProfessor(professor);
                            newSchedule.setClassroom(classroom);
                            newSchedule.setDayOfWeek(day);
                            newSchedule.setStartTime(startTime);
                            newSchedule.setEndTime(endTime);
                            newSchedule.setSemester(request.getSemester());
                            newSchedule.setAcademicYear(request.getAcademicYear());

                            generatedSchedulesInternal.add(scheduleRepository.save(newSchedule));
                            scheduled = true;
                            break;
                        }
                    }
                }
            }
            if (!scheduled) {
                System.err.println("Could not schedule course: " + course.getCourseName() + " with given constraints.");
            }
        }
        return generatedSchedulesInternal.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
}