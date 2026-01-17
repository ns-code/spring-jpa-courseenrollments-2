package com.example.studentmanagement.controller;

import com.example.studentmanagement.dto.EnrollmentDTO;
import com.example.studentmanagement.dto.EnrollmentRequest;
import com.example.studentmanagement.entity.Enrollment;
import com.example.studentmanagement.entity.EnrollmentId;
import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.entity.Course;
import com.example.studentmanagement.repository.EnrollmentRepository;
import com.example.studentmanagement.repository.StudentRepository;

import lombok.RequiredArgsConstructor;

import com.example.studentmanagement.repository.CourseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Transactional
@RequestMapping("/api/enrollments")
public class EnrollmentController {
    
    private final EnrollmentRepository enrollmentRepository;
    
    private final StudentRepository studentRepository;
    
    private final CourseRepository courseRepository;
    
    private EnrollmentDTO convertToDTO(Enrollment enrollment) {
        EnrollmentDTO dto = new EnrollmentDTO();
        dto.setStudentId(enrollment.getId().getStudentId());
        dto.setCourseId(enrollment.getId().getCourseId());
        dto.setStudentName(enrollment.getStudent().getFirstName() + " " + enrollment.getStudent().getLastName());
        dto.setCourseName(enrollment.getCourse().getCourseName());
        dto.setCourseCode(enrollment.getCourse().getCourseCode());
        dto.setEnrollmentDate(enrollment.getEnrollmentDate());
        dto.setGrade(enrollment.getGrade());
        return dto;
    }
    
    @GetMapping
    public List<EnrollmentDTO> getAllEnrollments() {
        return enrollmentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @GetMapping("/student/{studentId}/course/{courseId}")
    public ResponseEntity<EnrollmentDTO> getEnrollmentById(
            @PathVariable Integer studentId, 
            @PathVariable Integer courseId) {
        EnrollmentId id = new EnrollmentId(studentId, courseId);
        return enrollmentRepository.findById(id)
                .map(enrollment -> ResponseEntity.ok(convertToDTO(enrollment)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/student/{studentId}")
    public List<EnrollmentDTO> getEnrollmentsByStudent(@PathVariable Integer studentId) {
        return enrollmentRepository.findById_StudentId(studentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @GetMapping("/course/{courseId}")
    public List<EnrollmentDTO> getEnrollmentsByCourse(@PathVariable Integer courseId) {
        return enrollmentRepository.findById_CourseId(courseId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @PostMapping
    public ResponseEntity<?> createEnrollment(@RequestBody EnrollmentRequest request) {
        // Verify student exists
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        
        // Verify course exists
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        
        // Create composite key
        EnrollmentId enrollmentId = new EnrollmentId(request.getStudentId(), request.getCourseId());
        
        // Create enrollment
        Enrollment enrollment = new Enrollment();
        enrollment.setId(enrollmentId);
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(request.getEnrollmentDate());
        enrollment.setGrade(request.getGrade());
        
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedEnrollment));
    }
    
    @PutMapping("/student/{studentId}/course/{courseId}")
    public ResponseEntity<EnrollmentDTO> updateEnrollment(
            @PathVariable Integer studentId,
            @PathVariable Integer courseId,
            @RequestBody EnrollmentRequest request) {
        EnrollmentId id = new EnrollmentId(studentId, courseId);
        return enrollmentRepository.findById(id)
                .map(existingEnrollment -> {
                    existingEnrollment.setEnrollmentDate(request.getEnrollmentDate());
                    existingEnrollment.setGrade(request.getGrade());
                    Enrollment updated = enrollmentRepository.save(existingEnrollment);
                    return ResponseEntity.ok(convertToDTO(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/student/{studentId}/course/{courseId}")
    public ResponseEntity<Void> deleteEnrollment(
            @PathVariable Integer studentId,
            @PathVariable Integer courseId) {
        EnrollmentId id = new EnrollmentId(studentId, courseId);
        if (enrollmentRepository.existsById(id)) {
            enrollmentRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}