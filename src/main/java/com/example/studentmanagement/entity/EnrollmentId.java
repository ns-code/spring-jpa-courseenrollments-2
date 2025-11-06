package com.example.studentmanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentId implements Serializable {
    
    @Column(name = "student_id")
    private Integer studentId;
    
    @Column(name = "course_id")
    private Integer courseId;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnrollmentId that = (EnrollmentId) o;
        return Objects.equals(studentId, that.studentId) && 
               Objects.equals(courseId, that.courseId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(studentId, courseId);
    }
}