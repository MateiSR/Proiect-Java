package com.javaproj.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    Optional<Student> findByEmail(String email);
    List<Student> findByMajor(String major);
    List<Student> findByLastNameOrderByFirstNameAsc(String lastName);
}
