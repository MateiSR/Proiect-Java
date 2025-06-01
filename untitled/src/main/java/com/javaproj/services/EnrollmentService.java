package com.javaproj.services;

import com.javaproj.db.*;
import com.javaproj.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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


    @Transactional
    public Enrollment createEnrollment(EnrollmentRequest enrollmentRequest) {
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

        return enrollmentRepository.save(enrollment);
    }

    @Transactional(readOnly = true)
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Enrollment> getEnrollmentById(Integer enrollmentId) {
        return enrollmentRepository.findById(enrollmentId);
    }

    @Transactional(readOnly = true)
    public List<Enrollment> getEnrollmentsByStudentId(Integer studentId) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        return enrollmentRepository.findByStudent_StudentId(studentId);
    }

    @Transactional(readOnly = true)
    public List<Enrollment> getEnrollmentsByScheduleId(Integer scheduleId) {
        scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + scheduleId));
        return enrollmentRepository.findBySchedule_ScheduleId(scheduleId);
    }

    @Transactional(readOnly = true)
    public Optional<Enrollment> getEnrollmentByStudentIdAndScheduleId(Integer studentId, Integer scheduleId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + scheduleId));
        return enrollmentRepository.findByStudentAndSchedule(student, schedule);
    }

    @Transactional
    public Enrollment updateEnrollment(Integer enrollmentId, EnrollmentRequest enrollmentDetails) {
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
        enrollment.setGrade(enrollmentDetails.grade);

        return enrollmentRepository.save(enrollment);
    }

    @Transactional
    public void deleteEnrollment(Integer enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));
        enrollmentRepository.delete(enrollment);
    }

    @Transactional(readOnly=true)
    public List<Enrollment> getEnrollmentsByStudentIdAndCourseId(Integer studentId, Integer courseId) {
        studentRepository.findById(studentId).orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        return enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId);
    }
}