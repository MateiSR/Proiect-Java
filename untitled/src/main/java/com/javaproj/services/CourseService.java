package com.javaproj.services;

import com.javaproj.db.Course;
import com.javaproj.db.CourseRepository;
import com.javaproj.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Transactional
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    @Transactional(readOnly = true)
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Course> getCourseById(Integer courseId) {
        return courseRepository.findById(courseId);
    }

    @Transactional(readOnly = true)
    public Optional<Course> getCourseByCourseCode(String courseCode) {
        return courseRepository.findByCourseCode(courseCode);
    }

    @Transactional(readOnly = true)
    public List<Course> getCoursesByDepartment(String department) {
        return courseRepository.findByDepartment(department);
    }

    @Transactional(readOnly = true)
    public List<Course> getCoursesByCredits(int credits) {
        return courseRepository.findByCredits(credits);
    }

    @Transactional(readOnly = true)
    public List<Course> findByCourseNameContainingIgnoreCase(String nameFragment) {
        return courseRepository.findByCourseNameContainingIgnoreCase(nameFragment);
    }

    @Transactional
    public Course updateCourse(Integer courseId, Course courseDetails) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        course.setCourseCode(courseDetails.getCourseCode());
        course.setCourseName(courseDetails.getCourseName());
        course.setCredits(courseDetails.getCredits());
        course.setDepartment(courseDetails.getDepartment());
        course.setDescription(courseDetails.getDescription());

        return courseRepository.save(course);
    }

    @Transactional
    public void deleteCourse(Integer courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        courseRepository.delete(course);
    }
}