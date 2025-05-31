package com.javaproj.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    List<Schedule> findByCourse(Course course);
    List<Schedule> findByProfessor(Professor professor);
    List<Schedule> findByClassroom(Classroom classroom);
    List<Schedule> findBySemesterAndAcademicYear(String semester, String academicYear);
    List<Schedule> findByDayOfWeekAndSemesterAndAcademicYear(String dayOfWeek, String semester, String academicYear);
    List<Schedule> findByProfessorAndSemesterAndAcademicYear(Professor professor, String semester, String academicYear);
    List<Schedule> findByClassroomAndSemesterAndAcademicYear(Classroom classroom, String semester, String academicYear);

    @Query("SELECT s FROM Schedule s WHERE s.classroom.roomId = :roomId " +
            "AND s.dayOfWeek = :dayOfWeek " +
            "AND s.semester = :semester " +
            "AND s.academicYear = :academicYear " +
            "AND ((s.startTime < :endTime AND s.endTime > :startTime))")
    List<Schedule> findRoomConflicts(@Param("roomId") Integer roomId,
                                     @Param("dayOfWeek") String dayOfWeek,
                                     @Param("semester") String semester,
                                     @Param("academicYear") String academicYear,
                                     @Param("startTime") LocalTime startTime,
                                     @Param("endTime") LocalTime endTime);

    @Query("SELECT s FROM Schedule s WHERE s.professor.professorId = :professorId " +
            "AND s.dayOfWeek = :dayOfWeek " +
            "AND s.semester = :semester " +
            "AND s.academicYear = :academicYear " +
            "AND ((s.startTime < :endTime AND s.endTime > :startTime))")
    List<Schedule> findProfessorConflicts(@Param("professorId") Integer professorId,
                                          @Param("dayOfWeek") String dayOfWeek,
                                          @Param("semester") String semester,
                                          @Param("academicYear") String academicYear,
                                          @Param("startTime") LocalTime startTime,
                                          @Param("endTime") LocalTime endTime);

    @Query("SELECT s FROM Schedule s WHERE s.course.courseId = :courseId AND s.semester = :semester AND s.academicYear = :academicYear")
    List<Schedule> findByCourseIdAndSemesterAndAcademicYear(@Param("courseId") Integer courseId, @Param("semester") String semester, @Param("academicYear") String academicYear);

    Optional<Schedule> findByClassroomAndDayOfWeekAndStartTimeAndSemesterAndAcademicYear(
            Classroom classroom, String dayOfWeek, LocalTime startTime, String semester, String academicYear
    );
}