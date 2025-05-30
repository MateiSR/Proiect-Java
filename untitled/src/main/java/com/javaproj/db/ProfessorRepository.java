package com.javaproj.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Integer> {
    Optional<Professor> findByEmail(String email);
    List<Professor> findByDepartment(String department);
    List<Professor> findByLastNameAndFirstName(String lastName, String firstName);
}
