package com.javaproj.services;

import com.javaproj.db.Classroom;
import com.javaproj.db.ClassroomRepository;
import com.javaproj.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClassroomService {

    private final ClassroomRepository classroomRepository;

    @Autowired
    public ClassroomService(ClassroomRepository classroomRepository) {
        this.classroomRepository = classroomRepository;
    }

    @Transactional
    public Classroom createClassroom(Classroom classroom) {
        return classroomRepository.save(classroom);
    }

    @Transactional(readOnly = true)
    public List<Classroom> getAllClassrooms() {
        return classroomRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Classroom> getClassroomById(Integer roomId) {
        return classroomRepository.findById(roomId);
    }

    @Transactional(readOnly = true)
    public Optional<Classroom> getClassroomByRoomNumber(String roomNumber) {
        return classroomRepository.findByRoomNumber(roomNumber);
    }

    @Transactional(readOnly = true)
    public List<Classroom> getClassroomsByBuilding(String building) {
        return classroomRepository.findByBuilding(building);
    }

    @Transactional(readOnly = true)
    public List<Classroom> getClassroomsByCapacityGreaterThanOrEqual(int capacity) {
        return classroomRepository.findByCapacityGreaterThanEqual(capacity);
    }

    @Transactional(readOnly = true)
    public List<Classroom> getClassroomsByHasProjector(boolean hasProjector) {
        return classroomRepository.findByHasProjector(hasProjector);
    }

    @Transactional
    public Classroom updateClassroom(Integer roomId, Classroom classroomDetails) {
        Classroom classroom = classroomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with id: " + roomId));

        classroom.setRoomNumber(classroomDetails.getRoomNumber());
        classroom.setCapacity(classroomDetails.getCapacity());
        classroom.setHasProjector(classroomDetails.isHasProjector());
        classroom.setBuilding(classroomDetails.getBuilding());

        return classroomRepository.save(classroom);
    }

    @Transactional
    public void deleteClassroom(Integer roomId) {
        Classroom classroom = classroomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with id: " + roomId));

        if (!classroom.getSchedules().isEmpty()) {
            throw new IllegalStateException("Cannot delete classroom with ID " + roomId + " as it has associated schedules. Please remove or reassign schedules first.");
        }
        classroomRepository.delete(classroom);
    }
}