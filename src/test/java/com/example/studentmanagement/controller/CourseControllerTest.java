package com.example.studentmanagement.controller;

import com.example.studentmanagement.entity.Course;
import com.example.studentmanagement.repository.CourseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Use @WebMvcTest to test the controller layer without the full application context
@WebMvcTest(CourseController.class)
public class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc; // Used to simulate HTTP requests

    @Autowired
    private ObjectMapper objectMapper; // Used to convert objects to JSON

    // Mock the repository dependency
    @MockitoBean
    private CourseRepository courseRepository;

    @Test
    public void createCourse_shouldReturnCreatedCourseAndStatus201() throws Exception {
        // 1. Setup the input Course object (without ID as it's generated)
        Course inputCourse = new Course(
                null,
                "Introduction to Computer Science",
                "CS101",
                3,
                null // Enrollments list
        );

        // 2. Setup the expected output Course object (with the generated ID)
        Course savedCourse = new Course(
                1,
                "Introduction to Computer Science",
                "CS101",
                3,
                null
        );

        // 3. Mock the behavior of the CourseRepository.save() method
        // When save is called with *any* Course object, return the savedCourse.
        when(courseRepository.save(any(Course.class))).thenReturn(savedCourse);

        // 4. Perform the POST request and assert the results
        mockMvc.perform(post("/api/courses")
                        // Set the content type to application/json
                        .contentType(MediaType.APPLICATION_JSON)
                        // Set the body of the request by converting the input object to JSON
                        .content(objectMapper.writeValueAsString(inputCourse)))

                // 5. Assertions
                // Expect HTTP Status 201 (Created)
                .andExpect(status().isCreated())
                // Expect the response content type to be application/json
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Expect the 'courseId' field in the JSON response to be 1
                .andExpect(jsonPath("$.courseId").value(1))
                // Expect the 'courseName' field to match the input
                .andExpect(jsonPath("$.courseName").value("Introduction to Computer Science"))
                // Expect the 'courseCode' field to match the input
                .andExpect(jsonPath("$.courseCode").value("CS101"))
                // Expect the 'credits' field to match the input
                .andExpect(jsonPath("$.credits").value(3));
    }

    // TODO: add another test for validation or error scenarios here,
    // e.g., testing what happens if a required field like courseName is missing,
    // assuming validation annotations (@Valid) on the controller method.

}