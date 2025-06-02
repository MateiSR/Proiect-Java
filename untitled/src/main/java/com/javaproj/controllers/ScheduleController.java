package com.javaproj.controllers;

import com.javaproj.dto.ScheduleGenerationRequest;
import com.javaproj.services.ScheduleService;
import com.javaproj.dto.ScheduleResponseDTO;
import com.javaproj.exceptions.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;


@RestController
@RequestMapping("/api/v1/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping
    public ResponseEntity<?> createSchedule(@RequestBody ScheduleService.ScheduleRequest scheduleRequest) {
        try {
            ScheduleResponseDTO createdSchedule = scheduleService.createSchedule(scheduleRequest); // Returns DTO
            return new ResponseEntity<>(createdSchedule, HttpStatus.CREATED);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Database constraint violation: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<ScheduleResponseDTO>> getAllSchedules( // list of dtos
                                                                      @RequestParam(required = false) Integer courseId,
                                                                      @RequestParam(required = false) Integer professorId,
                                                                      @RequestParam(required = false) Integer classroomId) {
        List<ScheduleResponseDTO> schedules;
        try {
            if (courseId != null) {
                schedules = scheduleService.getSchedulesByCourseId(courseId);
            } else if (professorId != null) {
                schedules = scheduleService.getSchedulesByProfessorId(professorId);
            } else if (classroomId != null) {
                schedules = scheduleService.getSchedulesByClassroomId(classroomId);
            } else {
                schedules = scheduleService.getAllSchedules();
            }
            return ResponseEntity.ok(schedules);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponseDTO> getScheduleById(@PathVariable Integer id) { // Return DTO
        return scheduleService.getScheduleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/conflicts/room")
    public ResponseEntity<?> getRoomConflicts(
            @RequestParam Integer roomId,
            @RequestParam String dayOfWeek,
            @RequestParam String semester,
            @RequestParam String academicYear,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {
        try {
            List<com.javaproj.db.Schedule> conflicts = scheduleService.findRoomConflicts(roomId, dayOfWeek, semester, academicYear, startTime, endTime);
            return ResponseEntity.ok(conflicts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/conflicts/professor")
    public ResponseEntity<?> getProfessorConflicts(
            @RequestParam Integer professorId,
            @RequestParam String dayOfWeek,
            @RequestParam String semester,
            @RequestParam String academicYear,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {
        try {
            List<com.javaproj.db.Schedule> conflicts = scheduleService.findProfessorConflicts(professorId, dayOfWeek, semester, academicYear, startTime, endTime);
            return ResponseEntity.ok(conflicts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateSchedule(@PathVariable Integer id, @RequestBody ScheduleService.ScheduleRequest scheduleRequestDetails) {
        try {
            ScheduleResponseDTO updatedSchedule = scheduleService.updateSchedule(id, scheduleRequestDetails); // Returns DTO
            return ResponseEntity.ok(updatedSchedule);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Database constraint violation: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Integer id) {
        try {
            scheduleService.deleteSchedule(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Cannot delete schedule due to existing references. " + e.getMessage());
        }
    }

    @PostMapping("/generate-automatic")
    public ResponseEntity<?> generateAutomaticSchedule(@RequestBody ScheduleGenerationRequest request) {
        try {
            List<ScheduleResponseDTO> generatedSchedules = scheduleService.generateAutomaticSchedule(request);
            if (generatedSchedules.isEmpty() && !request.getCourseIds().isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Could not schedule any of the requested courses with the given constraints.");
            }
            return ResponseEntity.ok(generatedSchedules);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred during schedule generation: " + e.getMessage());
        }
    }
}