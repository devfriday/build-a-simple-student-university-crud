package com.example.studentcrud.dto;

public record CourseSummaryResponse(
        Long id,
        String courseCode,
        String title,
        Integer creditHours
) {
}
