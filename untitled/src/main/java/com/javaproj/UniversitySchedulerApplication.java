package com.javaproj;

//
//
// TEST CLASS GENERATED WITH AI
//
//

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
        log.info("---- STARTING DATABASE TESTS ----");

        // --- Test Classroom ---
        log.info("---- TESTING CLASSROOMS ----");
        Classroom c1 = new Classroom();
        c1.setRoomNumber("C101");
        c1.setCapacity(50);
        c1.setBuilding("Main Building");
        c1.setHasProjector(true);
        classroomRepository.save(c1);

        Classroom c2 = new Classroom();
        c2.setRoomNumber("LAB2B");
        c2.setCapacity(30);
        c2.setBuilding("Tech Wing");
        c2.setHasProjector(false);
        classroomRepository.save(c2);

        log.info("Classrooms found with findAll():");
        classroomRepository.findAll().forEach(classroom -> log.info(classroom.getRoomNumber()));
        Optional<Classroom> foundC1 = classroomRepository.findByRoomNumber("C101");
        foundC1.ifPresent(value -> log.info("Found C101: " + value.getBuilding()));


        // --- Test Professor ---
        log.info("---- TESTING PROFESSORS ----");
        Professor p1 = new Professor();
        p1.setFirstName("John");
        p1.setLastName("Doe");
        p1.setEmail("john.doe@example.com");
        p1.setDepartment("Computer Science");
        p1.setOffice("A305");
        professorRepository.save(p1);

        Professor p2 = new Professor();
        p2.setFirstName("Jane");
        p2.setLastName("Smith");
        p2.setEmail("jane.smith@example.com");
        p2.setDepartment("Mathematics");
        p2.setOffice("B102");
        professorRepository.save(p2);

        log.info("Professors found with findAll():");
        professorRepository.findAll().forEach(prof -> log.info(prof.getFirstName() + " " + prof.getLastName()));
        Optional<Professor> foundP1 = professorRepository.findByEmail("john.doe@example.com");
        foundP1.ifPresent(value -> log.info("Found John Doe, Department: " + value.getDepartment()));


        // --- Test Course ---
        log.info("---- TESTING COURSES ----");
        Course courseCS101 = new Course();
        courseCS101.setCourseCode("CS101");
        courseCS101.setCourseName("Introduction to Programming");
        courseCS101.setCredits(4);
        courseCS101.setDepartment("Computer Science");
        courseCS101.setDescription("Fundamental programming concepts.");
        courseRepository.save(courseCS101);

        Course courseMA202 = new Course();
        courseMA202.setCourseCode("MA202");
        courseMA202.setCourseName("Calculus II");
        courseMA202.setCredits(3);
        courseMA202.setDepartment("Mathematics");
        courseMA202.setDescription("Advanced calculus topics.");
        courseRepository.save(courseMA202);

        log.info("Courses found with findAll():");
        courseRepository.findAll().forEach(course -> {
            log.info(course.getCourseCode() + ": " + course.getCourseName() + " (Desc: " + course.getDescription() + ")");
        });

        List<Course> csCourses = courseRepository.findByDepartment("Computer Science");
        log.info("Computer Science courses:");
        csCourses.forEach(course -> {
            log.info(course.getCourseName() + " Description: " + course.getDescription());
        });


        // --- Test Student ---
        log.info("---- TESTING STUDENTS ----");
        Student s1 = new Student();
        s1.setFirstName("Alice");
        s1.setLastName("Wonderland");
        s1.setEmail("alice.w@example.com");
        s1.setMajor("Computer Science");
        s1.setEnrollmentDate(LocalDate.now().minusYears(1));
        studentRepository.save(s1);

        Student s2 = new Student();
        s2.setFirstName("Bob");
        s2.setLastName("The Builder");
        s2.setEmail("bob.b@example.com");
        s2.setMajor("Engineering");
        s2.setEnrollmentDate(LocalDate.now().minusMonths(6));
        studentRepository.save(s2);

        log.info("Students found with findAll():");
        studentRepository.findAll().forEach(student -> log.info(student.getFirstName() + " " + student.getLastName()));
        Optional<Student> foundS1 = studentRepository.findByEmail("alice.w@example.com");
        foundS1.ifPresent(value -> log.info("Found Alice, Major: " + value.getMajor()));


        // --- Test Schedule ---
        log.info("---- TESTING SCHEDULE ----");
        Classroom managedC1_schedule = classroomRepository.findByRoomNumber("C101").orElse(null);
        Professor managedP1_schedule = professorRepository.findByEmail("john.doe@example.com").orElse(null);
        Course managedCourseCS101_schedule = courseRepository.findByCourseCode("CS101").orElse(null);

        if (managedC1_schedule != null && managedP1_schedule != null && managedCourseCS101_schedule != null) {
            Schedule sch1 = new Schedule();
            sch1.setCourse(managedCourseCS101_schedule);
            sch1.setProfessor(managedP1_schedule);
            sch1.setClassroom(managedC1_schedule);
            sch1.setDayOfWeek("Luni");
            sch1.setStartTime(LocalTime.of(9, 0));
            sch1.setEndTime(LocalTime.of(11, 0));
            sch1.setSemester("Toamna 2024");
            sch1.setAcademicYear("2024-2025");
            scheduleRepository.save(sch1);
            log.info("Saved Schedule ID: " + sch1.getScheduleId());

            List<Schedule> conflicts = scheduleRepository.findRoomConflicts(
                    managedC1_schedule.getRoomId(), "Luni", "Toamna 2024", "2024-2025",
                    LocalTime.of(10,0), LocalTime.of(12,0)
            );
            log.info("Potential room conflicts for C101 Luni 10:00-12:00: " + (conflicts != null ? conflicts.size() : "0"));
            if (conflicts != null) {
                conflicts.forEach(c -> log.info("Conflict Schedule ID: " + c.getScheduleId() + " for course " + c.getCourse().getCourseCode()));
            }

            // --- Test Enrollment (Reordered) ---
            log.info("---- TESTING ENROLLMENT ----");
            Student managedS1_enroll = studentRepository.findByEmail("alice.w@example.com").orElse(null);
            Schedule managedSch1_enroll = sch1;

            if (managedS1_enroll != null && managedSch1_enroll != null) {
                Enrollment e1 = new Enrollment();
                e1.setStudent(managedS1_enroll);
                e1.setSchedule(managedSch1_enroll);
                e1.setEnrollmentDate(LocalDate.now());
                e1.setGrade(new BigDecimal("95.50"));
                enrollmentRepository.save(e1);
                log.info("Saved Enrollment ID: " + e1.getEnrollmentId() + " for student " + e1.getStudent().getFirstName());

                // Query for Alice's enrollments BEFORE attempting the duplicate save
                List<Enrollment> aliceEnrollments = enrollmentRepository.findByStudent_StudentId(managedS1_enroll.getStudentId());
                log.info("Alice's enrollments (before duplicate attempt):");
                if (aliceEnrollments != null) {
                    aliceEnrollments.forEach(enrollment -> log.info("Enrolled in: " + enrollment.getSchedule().getCourse().getCourseName()));
                }


            } else {
                log.error("Could not find managed Student S1 or Schedule SCH1 for enrollment test.");
            }

        } else {
            String c1Err = managedC1_schedule == null ? "Classroom C101 not found. " : "";
            String p1Err = managedP1_schedule == null ? "Professor john.doe@example.com not found. " : "";
            String cs101Err = managedCourseCS101_schedule == null ? "Course CS101 not found. " : "";
            log.error("Could not find managed entities for schedule test: " + c1Err + p1Err + cs101Err);
        }


        log.info("---- DATABASE TESTS FINISHED ----");
    }
}