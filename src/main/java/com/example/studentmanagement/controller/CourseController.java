package com.example.studentmanagement.controller;

import com.example.studentmanagement.entity.Course;
import com.example.studentmanagement.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    
    @Autowired
    private CourseRepository courseRepository;
    
    @GetMapping
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Integer id) {
        return courseRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        Course savedCourse = courseRepository.save(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCourse);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable Integer id, @RequestBody Course course) {
        return courseRepository.findById(id)
                .map(existingCourse -> {
                    existingCourse.setCourseName(course.getCourseName());
                    existingCourse.setCourseCode(course.getCourseCode());
                    existingCourse.setCredits(course.getCredits());
                    return ResponseEntity.ok(courseRepository.save(existingCourse));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Integer id) {
        return courseRepository.findById(id)
                .map(course -> {
                    // Clear the enrollments collection before deleting
                    course.getEnrollments().clear();
                    courseRepository.delete(course);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}