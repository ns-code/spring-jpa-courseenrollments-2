package com.example.studentmanagement.repository;

import com.example.studentmanagement.entity.Enrollment;
import com.example.studentmanagement.entity.EnrollmentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, EnrollmentId> {
    List<Enrollment> findById_StudentId(Integer studentId);
    List<Enrollment> findById_CourseId(Integer courseId);
}