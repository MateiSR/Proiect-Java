package com.javaproj;

import com.javaproj.db.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class UniversitySchedulerApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(UniversitySchedulerApplication.class);

    @Autowired private StudentRepository studentRepository;
    @Autowired private ProfessorRepository professorRepository;
    @Autowired private ClassroomRepository classroomRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private ScheduleRepository scheduleRepository;
    @Autowired private EnrollmentRepository enrollmentRepository;

    public static void main(String[] args) {
        SpringApplication.run(UniversitySchedulerApplication.class, args);
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("---- STARTING DATABASE SETUP/TEST ----");

        log.info("---- PROCESSING CLASSROOMS ----");
        Classroom c1 = classroomRepository.findByRoomNumber("C101").orElseGet(() -> {
            Classroom newC1 = new Classroom();
            newC1.setRoomNumber("C101");
            newC1.setCapacity(50);
            newC1.setBuilding("Main Building");
            newC1.setHasProjector(true);
            log.info("Creating Classroom C101");
            return classroomRepository.save(newC1);
        });
        log.info("Using Classroom C101: ID {}", c1.getRoomId());

        Classroom c2 = classroomRepository.findByRoomNumber("LAB2B").orElseGet(() -> {
            Classroom newC2 = new Classroom();
            newC2.setRoomNumber("LAB2B");
            newC2.setCapacity(30);
            newC2.setBuilding("Tech Wing");
            newC2.setHasProjector(false);
            log.info("Creating Classroom LAB2B");
            return classroomRepository.save(newC2);
        });
        log.info("Using Classroom LAB2B: ID {}", c2.getRoomId());

        log.info("---- PROCESSING PROFESSORS ----");
        Professor p1 = professorRepository.findByEmail("john.doe@example.com").orElseGet(() -> {
            Professor newP1 = new Professor();
            newP1.setFirstName("John");
            newP1.setLastName("Doe");
            newP1.setEmail("john.doe@example.com");
            newP1.setDepartment("Computer Science");
            newP1.setOffice("A305");
            log.info("Creating Professor John Doe");
            return professorRepository.save(newP1);
        });
        log.info("Using Professor John Doe: ID {}", p1.getProfessorId());

        Professor p2 = professorRepository.findByEmail("jane.smith@example.com").orElseGet(() -> {
            Professor newP2 = new Professor();
            newP2.setFirstName("Jane");
            newP2.setLastName("Smith");
            newP2.setEmail("jane.smith@example.com");
            newP2.setDepartment("Mathematics");
            newP2.setOffice("B102");
            log.info("Creating Professor Jane Smith");
            return professorRepository.save(newP2);
        });
        log.info("Using Professor Jane Smith: ID {}", p2.getProfessorId());

        log.info("---- PROCESSING COURSES ----");
        Course courseCS101 = courseRepository.findByCourseCode("CS101").orElseGet(() -> {
            Course newCourse = new Course();
            newCourse.setCourseCode("CS101");
            newCourse.setCourseName("Introduction to Programming");
            newCourse.setCredits(4);
            newCourse.setDepartment("Computer Science");
            newCourse.setDescription("Fundamental programming concepts.");
            log.info("Creating Course CS101");
            return courseRepository.save(newCourse);
        });
        log.info("Using Course CS101: ID {}", courseCS101.getCourseId());

        Course courseMA202 = courseRepository.findByCourseCode("MA202").orElseGet(() -> {
            Course newCourse = new Course();
            newCourse.setCourseCode("MA202");
            newCourse.setCourseName("Calculus II");
            newCourse.setCredits(3);
            newCourse.setDepartment("Mathematics");
            newCourse.setDescription("Advanced calculus topics.");
            log.info("Creating Course MA202");
            return courseRepository.save(newCourse);
        });
        log.info("Using Course MA202: ID {}", courseMA202.getCourseId());

        log.info("---- PROCESSING STUDENTS ----");
        Student s1 = studentRepository.findByEmail("alice.w@example.com").orElseGet(() -> {
            Student newS1 = new Student();
            newS1.setFirstName("Alice");
            newS1.setLastName("Wonderland");
            newS1.setEmail("alice.w@example.com");
            newS1.setMajor("Computer Science");
            newS1.setEnrollmentDate(LocalDate.now().minusYears(1));
            log.info("Creating Student Alice Wonderland");
            return studentRepository.save(newS1);
        });
        log.info("Using Student Alice Wonderland: ID {}", s1.getStudentId());

        Student s2 = studentRepository.findByEmail("bob.b@example.com").orElseGet(() -> {
            Student newS2 = new Student();
            newS2.setFirstName("Bob");
            newS2.setLastName("The Builder");
            newS2.setEmail("bob.b@example.com");
            newS2.setMajor("Engineering");
            newS2.setEnrollmentDate(LocalDate.now().minusMonths(6));
            log.info("Creating Student Bob The Builder");
            return studentRepository.save(newS2);
        });
        log.info("Using Student Bob The Builder: ID {}", s2.getStudentId());

        log.info("---- PROCESSING SCHEDULE ----");
        final String scheduleDay = "Luni";
        final LocalTime scheduleStartTime = LocalTime.of(9, 0);
        final LocalTime scheduleEndTime = LocalTime.of(11, 0);
        final String scheduleSemester = "Toamna 2024";
        final String scheduleAcademicYear = "2024-2025";

        Schedule sch1 = scheduleRepository.findByClassroomAndDayOfWeekAndStartTimeAndSemesterAndAcademicYear(
                        c1, scheduleDay, scheduleStartTime, scheduleSemester, scheduleAcademicYear)
                .orElseGet(() -> {
                    Schedule newSch = new Schedule();
                    newSch.setCourse(courseCS101);
                    newSch.setProfessor(p1);
                    newSch.setClassroom(c1);
                    newSch.setDayOfWeek(scheduleDay);
                    newSch.setStartTime(scheduleStartTime);
                    newSch.setEndTime(scheduleEndTime);
                    newSch.setSemester(scheduleSemester);
                    newSch.setAcademicYear(scheduleAcademicYear);
                    log.info("Creating Schedule for CS101 with Prof. Doe in C101 on Luni 9:00");
                    return scheduleRepository.save(newSch);
                });
        log.info("Using Schedule ID: {}", sch1.getScheduleId());

        List<Schedule> conflicts = scheduleRepository.findRoomConflicts(
                c1.getRoomId(), scheduleDay, scheduleSemester, scheduleAcademicYear,
                LocalTime.of(10,0), LocalTime.of(12,0)
        );
        log.info("Potential room conflicts for C101 Luni 10:00-12:00: {}", conflicts.size());
        conflicts.forEach(cfl -> log.info("Conflict Schedule ID: {} for course {}", cfl.getScheduleId(), cfl.getCourse().getCourseCode()));

        log.info("---- PROCESSING ENROLLMENT ----");
        Enrollment e1 = enrollmentRepository.findByStudentAndSchedule(s1, sch1).orElseGet(() -> {
            Enrollment newE = new Enrollment();
            newE.setStudent(s1);
            newE.setSchedule(sch1);
            newE.setEnrollmentDate(LocalDate.now());
            newE.setGrade(new BigDecimal("95.50"));
            log.info("Creating Enrollment for Alice in CS101 schedule");
            return enrollmentRepository.save(newE);
        });
        log.info("Using Enrollment ID: {} for student {}", e1.getEnrollmentId(), e1.getStudent().getFirstName());

        List<Enrollment> aliceEnrollments = enrollmentRepository.findByStudent_StudentId(s1.getStudentId());
        log.info("Alice's enrollments:");
        aliceEnrollments.forEach(enrollment -> log.info("Enrolled in: {}", enrollment.getSchedule().getCourse().getCourseName()));

        log.info("---- DATABASE SETUP/TEST FINISHED ----");
    }
}