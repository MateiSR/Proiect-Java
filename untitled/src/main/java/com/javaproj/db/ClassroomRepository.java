package com.javaproj.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Integer> {
    Optional<Classroom> findByRoomNumber(String roomNumber);
    List<Classroom> findByBuilding(String building);
    List<Classroom> findByCapacityGreaterThanEqual(int capacity);
    List<Classroom> findByHasProjector(boolean hasProjector);
}
