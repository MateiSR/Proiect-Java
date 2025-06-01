package com.javaproj.controllers;

import com.javaproj.db.Enrollment;
import com.javaproj.services.EnrollmentService;
import com.javaproj.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            Enrollment createdEnrollment = enrollmentService.createEnrollment(enrollmentRequest);
            return new ResponseEntity<>(createdEnrollment, HttpStatus.CREATED);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Database constraint violation (e.g., unique student-schedule): " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllEnrollments(
            @RequestParam(required = false) Integer studentId,
            @RequestParam(required = false) Integer scheduleId,
            @RequestParam(required = false) Integer courseId
    ) {
        List<Enrollment> enrollments;
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
    public ResponseEntity<Enrollment> getEnrollmentById(@PathVariable Integer id) {
        return enrollmentService.getEnrollmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/student/{studentId}/schedule/{scheduleId}")
    public ResponseEntity<?> getEnrollmentByStudentIdAndScheduleId(
            @PathVariable Integer studentId,
            @PathVariable Integer scheduleId) {
        try {
            return enrollmentService.getEnrollmentByStudentIdAndScheduleId(studentId, scheduleId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateEnrollment(@PathVariable Integer id, @RequestBody EnrollmentService.EnrollmentRequest enrollmentDetails) {
        try {
            Enrollment updatedEnrollment = enrollmentService.updateEnrollment(id, enrollmentDetails);
            return ResponseEntity.ok(updatedEnrollment);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
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