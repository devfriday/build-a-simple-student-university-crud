package com.example.studentcrud.dto;

public record UniversityResponse(
        Long id,
        String name,
        String city,
        long departmentCount,
        long studentCount,
        long courseCount
) {
}
