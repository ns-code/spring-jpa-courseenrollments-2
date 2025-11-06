package com.example.studentmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentRequest {
    private Integer studentId;
    private Integer courseId;
    private LocalDate enrollmentDate;
    private String grade;
}