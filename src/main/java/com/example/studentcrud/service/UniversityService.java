package com.example.studentcrud.service;

import com.example.studentcrud.dto.UniversityRequest;
import com.example.studentcrud.dto.UniversityResponse;
import com.example.studentcrud.entity.University;
import com.example.studentcrud.exception.ResourceNotFoundException;
import com.example.studentcrud.repository.CourseRepository;
import com.example.studentcrud.repository.DepartmentRepository;
import com.example.studentcrud.repository.StudentRepository;
import com.example.studentcrud.repository.UniversityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UniversityService {

    private final UniversityRepository universityRepository;
    private final DepartmentRepository departmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public UniversityService(
            UniversityRepository universityRepository,
            DepartmentRepository departmentRepository,
            StudentRepository studentRepository,
            CourseRepository courseRepository
    ) {
        this.universityRepository = universityRepository;
        this.departmentRepository = departmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    public List<UniversityResponse> getAll() {
        return universityRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UniversityResponse getById(Long id) {
        return toResponse(findEntity(id));
    }

    public UniversityResponse create(UniversityRequest request) {
        University university = new University();
        university.setName(request.name());
        university.setCity(request.city());
        return toResponse(universityRepository.save(university));
    }

    public UniversityResponse update(Long id, UniversityRequest request) {
        University university = findEntity(id);
        university.setName(request.name());
        university.setCity(request.city());
        return toResponse(universityRepository.save(university));
    }

    public void delete(Long id) {
        University university = findEntity(id);
        universityRepository.delete(university);
    }

    public University findEntity(Long id) {
        return universityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("University not found with id " + id));
    }

    private UniversityResponse toResponse(University university) {
        return new UniversityResponse(
                university.getId(),
                university.getName(),
                university.getCity(),
                departmentRepository.countByUniversityId(university.getId()),
                studentRepository.countByDepartmentUniversityId(university.getId()),
                courseRepository.countByDepartmentUniversityId(university.getId())
        );
    }
}
