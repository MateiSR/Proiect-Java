package com.javaproj.services;

import com.javaproj.db.*;
import com.javaproj.dto.EnrollmentResponseDTO;
import com.javaproj.dto.ScheduleResponseDTO;
import com.javaproj.dto.StudentDTO;
import com.javaproj.dto.CourseDTO;
import com.javaproj.dto.ProfessorDTO;
import com.javaproj.dto.ClassroomDTO;
import com.javaproj.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final ScheduleRepository scheduleRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             StudentRepository studentRepository,
                             ScheduleRepository scheduleRepository,
                             CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.scheduleRepository = scheduleRepository;
        this.courseRepository = courseRepository;
    }

    // Existing EnrollmentRequest static class
    public static class EnrollmentRequest {
        public Integer studentId;
        public Integer scheduleId;
        public LocalDate enrollmentDate;
        public BigDecimal grade;

        public EnrollmentRequest() {}

        public Integer getStudentId() { return studentId; }
        public void setStudentId(Integer studentId) { this.studentId = studentId; }
        public Integer getScheduleId() { return scheduleId; }
        public void setScheduleId(Integer scheduleId) { this.scheduleId = scheduleId; }
        public LocalDate getEnrollmentDate() { return enrollmentDate; }
        public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }
        public BigDecimal getGrade() { return grade; }
        public void setGrade(BigDecimal grade) { this.grade = grade; }
    }

    private ScheduleResponseDTO convertScheduleToDTO(Schedule schedule) {
        if (schedule == null) return null;
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
                schedule.getScheduleId(), courseDTO, professorDTO, classroomDTO,
                schedule.getDayOfWeek(), schedule.getStartTime(), schedule.getEndTime(),
                schedule.getSemester(), schedule.getAcademicYear()
        );
    }


    private EnrollmentResponseDTO convertToDTO(Enrollment enrollment) {
        if (enrollment == null) {
            return null;
        }
        Student student = enrollment.getStudent();
        Schedule schedule = enrollment.getSchedule();

        StudentDTO studentDTO = (student != null)
                ? new StudentDTO(student.getStudentId(), student.getFirstName(), student.getLastName(),
                student.getEmail(), student.getMajor(), student.getEnrollmentDate())
                : null;

        ScheduleResponseDTO scheduleResponseDTO = convertScheduleToDTO(schedule);

        return new EnrollmentResponseDTO(
                enrollment.getEnrollmentId(),
                studentDTO,
                scheduleResponseDTO,
                enrollment.getEnrollmentDate(),
                enrollment.getGrade()
        );
    }

    private List<EnrollmentResponseDTO> convertToDTOList(List<Enrollment> enrollments) {
        return enrollments.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public EnrollmentResponseDTO createEnrollment(EnrollmentRequest enrollmentRequest) {
        Student student = studentRepository.findById(enrollmentRequest.studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + enrollmentRequest.studentId));
        Schedule schedule = scheduleRepository.findById(enrollmentRequest.scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + enrollmentRequest.scheduleId));

        if (enrollmentRepository.findByStudentAndSchedule(student, schedule).isPresent()) {
            throw new IllegalStateException("Student " + student.getFirstName() + " " + student.getLastName() +
                    " is already enrolled in schedule ID " + schedule.getScheduleId() +
                    " (Course: " + schedule.getCourse().getCourseName() + ").");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setSchedule(schedule);
        enrollment.setEnrollmentDate(enrollmentRequest.enrollmentDate != null ? enrollmentRequest.enrollmentDate : LocalDate.now());
        enrollment.setGrade(enrollmentRequest.grade);

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return convertToDTO(savedEnrollment);
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponseDTO> getAllEnrollments() {
        return convertToDTOList(enrollmentRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Optional<EnrollmentResponseDTO> getEnrollmentById(Integer enrollmentId) {
        return enrollmentRepository.findById(enrollmentId).map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponseDTO> getEnrollmentsByStudentId(Integer studentId) {
        studentRepository.findById(studentId) // Check if student exists
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        return convertToDTOList(enrollmentRepository.findByStudent_StudentId(studentId));
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponseDTO> getEnrollmentsByScheduleId(Integer scheduleId) {
        scheduleRepository.findById(scheduleId) // Check if schedule exists
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + scheduleId));
        return convertToDTOList(enrollmentRepository.findBySchedule_ScheduleId(scheduleId));
    }

    @Transactional(readOnly = true)
    public Optional<EnrollmentResponseDTO> getEnrollmentByStudentIdAndScheduleId(Integer studentId, Integer scheduleId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + scheduleId));
        return enrollmentRepository.findByStudentAndSchedule(student, schedule).map(this::convertToDTO);
    }


    @Transactional
    public EnrollmentResponseDTO updateEnrollment(Integer enrollmentId, EnrollmentRequest enrollmentDetails) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));

        if (enrollmentDetails.getStudentId() != null && !enrollmentDetails.getStudentId().equals(enrollment.getStudent().getStudentId())) {
            throw new IllegalArgumentException("Cannot change student for an existing enrollment. Delete and create a new one.");
        }
        if (enrollmentDetails.getScheduleId() != null && !enrollmentDetails.getScheduleId().equals(enrollment.getSchedule().getScheduleId())) {
            throw new IllegalArgumentException("Cannot change schedule for an existing enrollment. Delete and create a new one.");
        }

        if (enrollmentDetails.enrollmentDate != null) {
            enrollment.setEnrollmentDate(enrollmentDetails.enrollmentDate);
        }
        enrollment.setGrade(enrollmentDetails.grade); // Allows setting grade to null

        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);
        return convertToDTO(updatedEnrollment);
    }

    @Transactional
    public void deleteEnrollment(Integer enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));
        enrollmentRepository.delete(enrollment);
    }

    @Transactional(readOnly=true)
    public List<EnrollmentResponseDTO> getEnrollmentsByStudentIdAndCourseId(Integer studentId, Integer courseId) {
        studentRepository.findById(studentId).orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        return convertToDTOList(enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId));
    }
}