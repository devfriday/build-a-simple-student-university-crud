package com.example.studentcrud.repository;

import com.example.studentcrud.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    long countByUniversityId(Long universityId);
}
