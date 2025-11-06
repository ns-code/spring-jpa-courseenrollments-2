package com.example.studentmanagement.controller;

import com.example.studentmanagement.dto.EnrollmentRequest;
import com.example.studentmanagement.entity.Course;
import com.example.studentmanagement.entity.Enrollment;
import com.example.studentmanagement.entity.EnrollmentId;
import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.repository.CourseRepository;
import com.example.studentmanagement.repository.EnrollmentRepository;
import com.example.studentmanagement.repository.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EnrollmentController.class)
class EnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EnrollmentRepository enrollmentRepository;

    @MockitoBean
    private StudentRepository studentRepository;

    @MockitoBean
    private CourseRepository courseRepository;

    private Student student;
    private Course course;
    private Enrollment enrollment;
    private EnrollmentId enrollmentId;

    @BeforeEach
    void setUp() {
        // Setup test student
        student = new Student();
        student.setStudentId(1);
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setEmail("john.doe@example.com");
        student.setEnrollmentDate(LocalDate.of(2024, 1, 15));

        // Setup test course
        course = new Course();
        course.setCourseId(1);
        course.setCourseName("Introduction to Computer Science");
        course.setCourseCode("CS101");
        course.setCredits(3);

        // Setup test enrollment
        enrollmentId = new EnrollmentId(1, 1);
        enrollment = new Enrollment();
        enrollment.setId(enrollmentId);
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(LocalDate.of(2024, 1, 20));
        enrollment.setGrade("A");
    }

    @Test
    void getAllEnrollments_ShouldReturnListOfEnrollments() throws Exception {
        // Given
        List<Enrollment> enrollments = Arrays.asList(enrollment);
        when(enrollmentRepository.findAll()).thenReturn(enrollments);

        // When & Then
        mockMvc.perform(get("/api/enrollments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].studentId", is(1)))
                .andExpect(jsonPath("$[0].courseId", is(1)))
                .andExpect(jsonPath("$[0].studentName", is("John Doe")))
                .andExpect(jsonPath("$[0].courseName", is("Introduction to Computer Science")))
                .andExpect(jsonPath("$[0].courseCode", is("CS101")))
                .andExpect(jsonPath("$[0].grade", is("A")));

        verify(enrollmentRepository, times(1)).findAll();
    }

    @Test
    void getEnrollmentById_WhenExists_ShouldReturnEnrollment() throws Exception {
        // Given
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));

        // When & Then
        mockMvc.perform(get("/api/enrollments/student/1/course/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId", is(1)))
                .andExpect(jsonPath("$.courseId", is(1)))
                .andExpect(jsonPath("$.studentName", is("John Doe")))
                .andExpect(jsonPath("$.courseName", is("Introduction to Computer Science")))
                .andExpect(jsonPath("$.grade", is("A")));

        verify(enrollmentRepository, times(1)).findById(enrollmentId);
    }

    @Test
    void getEnrollmentById_WhenNotExists_ShouldReturn404() throws Exception {
        // Given
        when(enrollmentRepository.findById(any(EnrollmentId.class))).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/enrollments/student/999/course/999"))
                .andExpect(status().isNotFound());

        verify(enrollmentRepository, times(1)).findById(any(EnrollmentId.class));
    }

    @Test
    void getEnrollmentsByStudent_ShouldReturnStudentEnrollments() throws Exception {
        // Given
        List<Enrollment> enrollments = Arrays.asList(enrollment);
        when(enrollmentRepository.findById_StudentId(1)).thenReturn(enrollments);

        // When & Then
        mockMvc.perform(get("/api/enrollments/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].studentId", is(1)))
                .andExpect(jsonPath("$[0].studentName", is("John Doe")));

        verify(enrollmentRepository, times(1)).findById_StudentId(1);
    }

    @Test
    void getEnrollmentsByCourse_ShouldReturnCourseEnrollments() throws Exception {
        // Given
        List<Enrollment> enrollments = Arrays.asList(enrollment);
        when(enrollmentRepository.findById_CourseId(1)).thenReturn(enrollments);

        // When & Then
        mockMvc.perform(get("/api/enrollments/course/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].courseId", is(1)))
                .andExpect(jsonPath("$[0].courseName", is("Introduction to Computer Science")));

        verify(enrollmentRepository, times(1)).findById_CourseId(1);
    }

    @Test
    void createEnrollment_WithValidData_ShouldReturnCreated() throws Exception {
        // Given
        EnrollmentRequest request = new EnrollmentRequest();
        request.setStudentId(1);
        request.setCourseId(1);
        request.setEnrollmentDate(LocalDate.of(2024, 1, 20));
        request.setGrade("A");

        when(studentRepository.findById(1)).thenReturn(Optional.of(student));
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);

        // When & Then
        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.studentId", is(1)))
                .andExpect(jsonPath("$.courseId", is(1)))
                .andExpect(jsonPath("$.grade", is("A")));

        verify(studentRepository, times(1)).findById(1);
        verify(courseRepository, times(1)).findById(1);
        verify(enrollmentRepository, times(1)).save(any(Enrollment.class));
    }

    @Test
    void createEnrollment_WithInvalidStudent_ShouldThrowException() throws Exception {
        // Given
        EnrollmentRequest request = new EnrollmentRequest();
        request.setStudentId(999);
        request.setCourseId(1);
        request.setEnrollmentDate(LocalDate.of(2024, 1, 20));
        request.setGrade("A");

        when(studentRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(Exception.class, () -> 
        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
        );

        // verify(studentRepository, times(1)).findById(999);
        // verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    void createEnrollment_WithInvalidCourse_ShouldThrowException() throws Exception {
        // Given
        EnrollmentRequest request = new EnrollmentRequest();
        request.setStudentId(1);
        request.setCourseId(999);
        request.setEnrollmentDate(LocalDate.of(2024, 1, 20));
        request.setGrade("A");

        when(studentRepository.findById(1)).thenReturn(Optional.of(student));
        when(courseRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(Exception.class, () -> 
        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
        );

        // verify(studentRepository, times(1)).findById(1);
        // verify(courseRepository, times(1)).findById(999);
        // verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    void updateEnrollment_WhenExists_ShouldReturnUpdatedEnrollment() throws Exception {
        // Given
        EnrollmentRequest request = new EnrollmentRequest();
        request.setEnrollmentDate(LocalDate.of(2024, 2, 1));
        request.setGrade("A+");

        Enrollment updatedEnrollment = new Enrollment();
        updatedEnrollment.setId(enrollmentId);
        updatedEnrollment.setStudent(student);
        updatedEnrollment.setCourse(course);
        updatedEnrollment.setEnrollmentDate(LocalDate.of(2024, 2, 1));
        updatedEnrollment.setGrade("A+");

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(updatedEnrollment);

        // When & Then
        mockMvc.perform(put("/api/enrollments/student/1/course/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grade", is("A+")))
                .andExpect(jsonPath("$.enrollmentDate", is("2024-02-01")));

        verify(enrollmentRepository, times(1)).findById(enrollmentId);
        verify(enrollmentRepository, times(1)).save(any(Enrollment.class));
    }

    @Test
    void updateEnrollment_WhenNotExists_ShouldReturn404() throws Exception {
        // Given
        EnrollmentRequest request = new EnrollmentRequest();
        request.setEnrollmentDate(LocalDate.of(2024, 2, 1));
        request.setGrade("A+");

        when(enrollmentRepository.findById(any(EnrollmentId.class))).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/enrollments/student/999/course/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(enrollmentRepository, times(1)).findById(any(EnrollmentId.class));
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    void deleteEnrollment_WhenExists_ShouldReturnNoContent() throws Exception {
        // Given
        when(enrollmentRepository.existsById(enrollmentId)).thenReturn(true);
        doNothing().when(enrollmentRepository).deleteById(enrollmentId);

        // When & Then
        mockMvc.perform(delete("/api/enrollments/student/1/course/1"))
                .andExpect(status().isNoContent());

        verify(enrollmentRepository, times(1)).existsById(enrollmentId);
        verify(enrollmentRepository, times(1)).deleteById(enrollmentId);
    }

    @Test
    void deleteEnrollment_WhenNotExists_ShouldReturn404() throws Exception {
        // Given
        when(enrollmentRepository.existsById(any(EnrollmentId.class))).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/enrollments/student/999/course/999"))
                .andExpect(status().isNotFound());

        verify(enrollmentRepository, times(1)).existsById(any(EnrollmentId.class));
        verify(enrollmentRepository, never()).deleteById(any(EnrollmentId.class));
    }

    @Test
    void getAllEnrollments_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        // Given
        when(enrollmentRepository.findAll()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/enrollments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(enrollmentRepository, times(1)).findAll();
    }

    @Test
    void getEnrollmentsByStudent_WhenNoEnrollments_ShouldReturnEmptyList() throws Exception {
        // Given
        when(enrollmentRepository.findById_StudentId(1)).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/enrollments/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(enrollmentRepository, times(1)).findById_StudentId(1);
    }

    @Test
    void getEnrollmentsByCourse_WhenNoEnrollments_ShouldReturnEmptyList() throws Exception {
        // Given
        when(enrollmentRepository.findById_CourseId(1)).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/enrollments/course/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(enrollmentRepository, times(1)).findById_CourseId(1);
    }
}