package com.javaproj.controllers;

import com.javaproj.dto.EnrollmentResponseDTO;
import com.javaproj.services.EnrollmentService;
import com.javaproj.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @Autowired
    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    public ResponseEntity<?> createEnrollment(@RequestBody EnrollmentService.EnrollmentRequest enrollmentRequest) {
        try {
            EnrollmentResponseDTO createdEnrollment = enrollmentService.createEnrollment(enrollmentRequest);
            return new ResponseEntity<>(createdEnrollment, HttpStatus.CREATED);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) { // Handles student already enrolled
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Database constraint violation: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllEnrollments(
            @RequestParam(required = false) Integer studentId,
            @RequestParam(required = false) Integer scheduleId,
            @RequestParam(required = false) Integer courseId // For student enrollments in a specific course
    ) {
        List<EnrollmentResponseDTO> enrollments;
        try {
            if (studentId != null && courseId != null) {
                enrollments = enrollmentService.getEnrollmentsByStudentIdAndCourseId(studentId, courseId);
            } else if (studentId != null) {
                enrollments = enrollmentService.getEnrollmentsByStudentId(studentId);
            } else if (scheduleId != null) {
                enrollments = enrollmentService.getEnrollmentsByScheduleId(scheduleId);
            } else {
                enrollments = enrollmentService.getAllEnrollments();
            }
            return ResponseEntity.ok(enrollments);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponseDTO> getEnrollmentById(@PathVariable Integer id) {
        Optional<EnrollmentResponseDTO> enrollmentDTO = enrollmentService.getEnrollmentById(id);
        return enrollmentDTO.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/student/{studentId}/schedule/{scheduleId}")
    public ResponseEntity<?> getEnrollmentByStudentIdAndScheduleId(
            @PathVariable Integer studentId,
            @PathVariable Integer scheduleId) {
        try {
            Optional<EnrollmentResponseDTO> enrollmentDTO = enrollmentService.getEnrollmentByStudentIdAndScheduleId(studentId, scheduleId);
            return enrollmentDTO.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEnrollment(@PathVariable Integer id, @RequestBody EnrollmentService.EnrollmentRequest enrollmentDetails) {
        try {
            EnrollmentResponseDTO updatedEnrollment = enrollmentService.updateEnrollment(id, enrollmentDetails);
            return ResponseEntity.ok(updatedEnrollment);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) { // For business rule violations like cannot change student/schedule
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEnrollment(@PathVariable Integer id) {
        try {
            enrollmentService.deleteEnrollment(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}