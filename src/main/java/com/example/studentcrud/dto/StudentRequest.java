package com.example.studentcrud.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record StudentRequest(
        @NotBlank(message = "First name is required")
        String firstName,
        @NotBlank(message = "Last name is required")
        String lastName,
        @Email(message = "Email must be valid")
        @NotBlank(message = "Email is required")
        String email,
        @NotNull(message = "Department id is required")
        Long departmentId,
        @NotEmpty(message = "At least one course is required")
        List<Long> courseIds
) {
}
