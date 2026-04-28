package com.example.studentcrud.service;

import com.example.studentcrud.dto.CourseSummaryResponse;
import com.example.studentcrud.dto.StudentRequest;
import com.example.studentcrud.dto.StudentResponse;
import com.example.studentcrud.entity.Course;
import com.example.studentcrud.entity.Department;
import com.example.studentcrud.entity.Enrollment;
import com.example.studentcrud.entity.Student;
import com.example.studentcrud.exception.ResourceNotFoundException;
import com.example.studentcrud.repository.CourseRepository;
import com.example.studentcrud.repository.DepartmentRepository;
import com.example.studentcrud.repository.EnrollmentRepository;
import com.example.studentcrud.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public StudentService(
            StudentRepository studentRepository,
            DepartmentRepository departmentRepository,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository
    ) {
        this.studentRepository = studentRepository;
        this.departmentRepository = departmentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public List<StudentResponse> getAll() {
        return studentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public StudentResponse getById(Long id) {
        return toResponse(findEntity(id));
    }

    public StudentResponse create(StudentRequest request) {
        Department department = findDepartment(request.departmentId());
        List<Course> courses = findCourses(request.courseIds());

        Student student = new Student();
        student.setFirstName(request.firstName());
        student.setLastName(request.lastName());
        student.setEmail(request.email());
        student.setDepartment(department);

        Student savedStudent = studentRepository.save(student);
        saveEnrollments(savedStudent, courses);
        return toResponse(savedStudent);
    }

    public StudentResponse update(Long id, StudentRequest request) {
        Student student = findEntity(id);
        Department department = findDepartment(request.departmentId());
        List<Course> courses = findCourses(request.courseIds());

        student.setFirstName(request.firstName());
        student.setLastName(request.lastName());
        student.setEmail(request.email());
        student.setDepartment(department);
        student.getEnrollments().clear();

        Student savedStudent = studentRepository.save(student);
        saveEnrollments(savedStudent, courses);
        return toResponse(savedStudent);
    }

    public void delete(Long id) {
        Student student = findEntity(id);
        studentRepository.delete(student);
    }

    private Student findEntity(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + id));
    }

    private Department findDepartment(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id " + id));
    }

    private List<Course> findCourses(List<Long> courseIds) {
        Set<Long> distinctIds = courseIds.stream().collect(Collectors.toSet());
        List<Course> courses = courseRepository.findByIdIn(distinctIds);
        if (courses.size() != distinctIds.size()) {
            throw new ResourceNotFoundException("One or more courses were not found");
        }
        return courses;
    }

    private void saveEnrollments(Student student, List<Course> courses) {
        for (Course course : courses) {
            if (!course.getDepartment().getUniversity().getId()
                    .equals(student.getDepartment().getUniversity().getId())) {
                throw new ResourceNotFoundException("Course " + course.getId() + " does not belong to the student's university");
            }

            Enrollment enrollment = new Enrollment();
            enrollment.setStudent(student);
            enrollment.setCourse(course);
            enrollment.setTermName("SPRING-2026");
            enrollmentRepository.save(enrollment);
            student.getEnrollments().add(enrollment);
        }
    }

    private StudentResponse toResponse(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.getDepartment().getId(),
                student.getDepartment().getName(),
                student.getDepartment().getUniversity().getId(),
                student.getDepartment().getUniversity().getName(),
                student.getEnrollments().stream()
                        .map(Enrollment::getCourse)
                        .sorted(Comparator.comparing(Course::getCourseCode))
                        .map(course -> new CourseSummaryResponse(
                                course.getId(),
                                course.getCourseCode(),
                                course.getTitle(),
                                course.getCreditHours()
                        ))
                        .toList()
        );
    }
}
