package com.example.studentcrud.dto;

import jakarta.validation.constraints.NotBlank;

public record UniversityRequest(
        @NotBlank(message = "University name is required")
        String name,
        @NotBlank(message = "City is required")
        String city
) {
}
