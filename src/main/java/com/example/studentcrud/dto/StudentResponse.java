package com.example.studentcrud.dto;

import java.util.List;

public record StudentResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        Long departmentId,
        String departmentName,
        Long universityId,
        String universityName,
        List<CourseSummaryResponse> courses
) {
}
