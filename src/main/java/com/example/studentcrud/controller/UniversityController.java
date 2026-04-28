package com.example.studentcrud.controller;

import com.example.studentcrud.dto.UniversityRequest;
import com.example.studentcrud.dto.UniversityResponse;
import com.example.studentcrud.service.UniversityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/universities")
public class UniversityController {

    private final UniversityService universityService;

    public UniversityController(UniversityService universityService) {
        this.universityService = universityService;
    }

    @GetMapping
    public List<UniversityResponse> getAllUniversities() {
        return universityService.getAll();
    }

    @GetMapping("/{id}")
    public UniversityResponse getUniversityById(@PathVariable Long id) {
        return universityService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UniversityResponse createUniversity(@Valid @RequestBody UniversityRequest request) {
        return universityService.create(request);
    }

    @PutMapping("/{id}")
    public UniversityResponse updateUniversity(@PathVariable Long id, @Valid @RequestBody UniversityRequest request) {
        return universityService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUniversity(@PathVariable Long id) {
        universityService.delete(id);
    }
}
