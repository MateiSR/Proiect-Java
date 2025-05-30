package com.javaproj.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    List<Enrollment> findByStudent(Student student);
    List<Enrollment> findBySchedule(Schedule schedule);
    Optional<Enrollment> findByStudentAndSchedule(Student student, Schedule schedule);
    List<Enrollment> findByStudent_StudentId(Integer studentId);
    List<Enrollment> findBySchedule_ScheduleId(Integer scheduleId);
    List<Enrollment> findByStudent_StudentIdAndSchedule_SemesterAndSchedule_AcademicYear(Integer studentId, String semester, String academicYear);

    @Query("SELECT e FROM Enrollment e WHERE e.student.studentId = :studentId AND e.schedule.course.courseId = :courseId")
    List<Enrollment> findByStudentIdAndCourseId(@Param("studentId") Integer studentId, @Param("courseId") Integer courseId);
}