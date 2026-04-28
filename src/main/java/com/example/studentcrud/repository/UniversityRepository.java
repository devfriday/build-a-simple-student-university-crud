package com.example.studentcrud.repository;

import com.example.studentcrud.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UniversityRepository extends JpaRepository<University, Long> {
}
