package com.javaproj.controllers;

import com.javaproj.db.Classroom;
import com.javaproj.services.ClassroomService;
import com.javaproj.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/classrooms")
public class ClassroomController {

    private final ClassroomService classroomService;

    @Autowired
    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }

    @PostMapping
    public ResponseEntity<?> createClassroom(@RequestBody Classroom classroom) {
        try {
            Classroom createdClassroom = classroomService.createClassroom(classroom);
            return new ResponseEntity<>(createdClassroom, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Classroom with this room number may already exist. " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Classroom>> getAllClassrooms(
            @RequestParam(required = false) String building,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) Boolean hasProjector) {
        List<Classroom> classrooms;
        if (building != null) {
            classrooms = classroomService.getClassroomsByBuilding(building);
        } else if (minCapacity != null) {
            classrooms = classroomService.getClassroomsByCapacityGreaterThanOrEqual(minCapacity);
        } else if (hasProjector != null) {
            classrooms = classroomService.getClassroomsByHasProjector(hasProjector);
        } else {
            classrooms = classroomService.getAllClassrooms();
        }
        return ResponseEntity.ok(classrooms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Classroom> getClassroomById(@PathVariable Integer id) {
        return classroomService.getClassroomById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/room-number/{roomNumber}")
    public ResponseEntity<Classroom> getClassroomByRoomNumber(@PathVariable String roomNumber) {
        return classroomService.getClassroomByRoomNumber(roomNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateClassroom(@PathVariable Integer id, @RequestBody Classroom classroomDetails) {
        try {
            Classroom updatedClassroom = classroomService.updateClassroom(id, classroomDetails);
            return ResponseEntity.ok(updatedClassroom);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Update may violate a unique constraint (e.g. room number). " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClassroom(@PathVariable Integer id) {
        try {
            classroomService.deleteClassroom(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Cannot delete classroom due to existing references (schedules). " + e.getMessage());
        }
    }
}