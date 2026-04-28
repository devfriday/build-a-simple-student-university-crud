package com.example.studentcrud.repository;

import com.example.studentcrud.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    long countByDepartmentUniversityId(Long universityId);

    List<Course> findByIdIn(Collection<Long> ids);
}
