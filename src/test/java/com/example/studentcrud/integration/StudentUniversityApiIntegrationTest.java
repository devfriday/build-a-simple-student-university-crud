package com.example.studentcrud.integration;

import com.example.studentcrud.dto.CourseSummaryResponse;
import com.example.studentcrud.dto.StudentRequest;
import com.example.studentcrud.dto.StudentResponse;
import com.example.studentcrud.dto.UniversityRequest;
import com.example.studentcrud.dto.UniversityResponse;
import com.example.studentcrud.entity.Course;
import com.example.studentcrud.entity.Department;
import com.example.studentcrud.entity.Enrollment;
import com.example.studentcrud.entity.Student;
import com.example.studentcrud.entity.University;
import com.example.studentcrud.repository.CourseRepository;
import com.example.studentcrud.repository.DepartmentRepository;
import com.example.studentcrud.repository.EnrollmentRepository;
import com.example.studentcrud.repository.StudentRepository;
import com.example.studentcrud.repository.UniversityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class StudentUniversityApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UniversityRepository universityRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void setUp() {
        enrollmentRepository.deleteAll();
        studentRepository.deleteAll();
        courseRepository.deleteAll();
        departmentRepository.deleteAll();
        universityRepository.deleteAll();
    }

    @Test
    void shouldCreateUniversityAndPersistItInH2() {
        UniversityRequest request = new UniversityRequest("Test University", "Chicago");

        ResponseEntity<UniversityResponse> response = restTemplate.postForEntity(
                url("/api/universities"),
                request,
                UniversityResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("Test University");
        assertThat(response.getBody().city()).isEqualTo("Chicago");
        assertThat(response.getBody().departmentCount()).isZero();
        assertThat(response.getBody().studentCount()).isZero();
        assertThat(response.getBody().courseCount()).isZero();

        List<University> universities = universityRepository.findAll();
        assertThat(universities).hasSize(1);
        assertThat(universities.get(0).getName()).isEqualTo("Test University");
        assertThat(universities.get(0).getCity()).isEqualTo("Chicago");
    }

    @Test
    void shouldCreateStudentThroughApiAndPersistItInH2() {
        University university = new University();
        university.setName("Integration University");
        university.setCity("Austin");
        university = universityRepository.save(university);

        Department department = new Department();
        department.setName("Computer Science");
        department.setUniversity(university);
        department = departmentRepository.save(department);

        Course course = new Course();
        course.setCourseCode("CS701");
        course.setTitle("Distributed Systems");
        course.setCreditHours(4);
        course.setDepartment(department);
        course = courseRepository.save(course);

        StudentRequest request = new StudentRequest(
                "Jane",
                "Doe",
                "jane.doe@example.com",
                department.getId(),
                List.of(course.getId())
        );

        ResponseEntity<StudentResponse> response = restTemplate.postForEntity(
                url("/api/students"),
                request,
                StudentResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().firstName()).isEqualTo("Jane");
        assertThat(response.getBody().departmentId()).isEqualTo(department.getId());
        assertThat(response.getBody().departmentName()).isEqualTo("Computer Science");
        assertThat(response.getBody().universityId()).isEqualTo(university.getId());
        assertThat(response.getBody().courses()).extracting(CourseSummaryResponse::courseCode)
                .containsExactly("CS701");

        List<Student> students = studentRepository.findAll();
        assertThat(students).hasSize(1);
        assertThat(students.get(0).getEmail()).isEqualTo("jane.doe@example.com");
        assertThat(students.get(0).getDepartment().getId()).isEqualTo(department.getId());
        assertThat(enrollmentRepository.findAll()).hasSize(1);
    }

    @Test
    void shouldFetchStudentsFromRealHttpRequestAgainstH2Database() {
        University university = new University();
        university.setName("Read University");
        university.setCity("Dallas");
        university = universityRepository.save(university);

        Department department = new Department();
        department.setName("Analytics");
        department.setUniversity(university);
        department = departmentRepository.save(department);

        Course course = new Course();
        course.setCourseCode("AN501");
        course.setTitle("Data Analytics");
        course.setCreditHours(3);
        course.setDepartment(department);
        course = courseRepository.save(course);

        Student student = new Student();
        student.setFirstName("Mark");
        student.setLastName("Lee");
        student.setEmail("mark.lee@example.com");
        student.setDepartment(department);
        student = studentRepository.save(student);

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setTermName("SPRING-2026");
        enrollmentRepository.save(enrollment);

        ResponseEntity<List<StudentResponse>> response = restTemplate.exchange(
                url("/api/students"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).email()).isEqualTo("mark.lee@example.com");
        assertThat(response.getBody().get(0).departmentName()).isEqualTo("Analytics");
        assertThat(response.getBody().get(0).universityName()).isEqualTo("Read University");
        assertThat(response.getBody().get(0).courses()).extracting(CourseSummaryResponse::courseCode)
                .containsExactly("AN501");
    }

    @Test
    void shouldUpdateStudentThroughApiAndReflectChangesInH2() {
        University university = new University();
        university.setName("Original University");
        university.setCity("Houston");
        university = universityRepository.save(university);

        Department originalDepartment = new Department();
        originalDepartment.setName("Engineering");
        originalDepartment.setUniversity(university);
        originalDepartment = departmentRepository.save(originalDepartment);

        Course originalCourse = new Course();
        originalCourse.setCourseCode("EN600");
        originalCourse.setTitle("Engineering Design");
        originalCourse.setCreditHours(3);
        originalCourse.setDepartment(originalDepartment);
        originalCourse = courseRepository.save(originalCourse);

        Department newDepartment = new Department();
        newDepartment.setName("Computer Science");
        newDepartment.setUniversity(university);
        newDepartment = departmentRepository.save(newDepartment);

        Course newCourse = new Course();
        newCourse.setCourseCode("CS800");
        newCourse.setTitle("Machine Learning Systems");
        newCourse.setCreditHours(4);
        newCourse.setDepartment(newDepartment);
        newCourse = courseRepository.save(newCourse);

        Student student = new Student();
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setEmail("john.doe@example.com");
        student.setDepartment(originalDepartment);
        student = studentRepository.save(student);

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(originalCourse);
        enrollment.setTermName("SPRING-2026");
        enrollmentRepository.save(enrollment); //TODO 2

        StudentRequest updateRequest = new StudentRequest(
                "John",
                "Doe Updated",
                "john.updated@example.com",
                newDepartment.getId(),
                List.of(newCourse.getId())
        );

        ResponseEntity<StudentResponse> response = restTemplate.exchange(
                url("/api/students/" + student.getId()),
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                StudentResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().lastName()).isEqualTo("Doe Updated");
        assertThat(response.getBody().email()).isEqualTo("john.updated@example.com");
        assertThat(response.getBody().departmentName()).isEqualTo("Computer Science");
        assertThat(response.getBody().courses()).extracting(CourseSummaryResponse::courseCode)
                .containsExactly("CS800");

        Student updatedStudent = studentRepository.findById(student.getId()).orElseThrow();
        assertThat(updatedStudent.getLastName()).isEqualTo("Doe Updated");
        assertThat(updatedStudent.getEmail()).isEqualTo("john.updated@example.com");
        assertThat(updatedStudent.getDepartment().getId()).isEqualTo(newDepartment.getId());
        assertThat(enrollmentRepository.findAll()).hasSize(1);
        assertThat(enrollmentRepository.findAll().get(0).getCourse().getId()).isEqualTo(newCourse.getId());
    }

    @Test
    void shouldDeleteUniversityThroughApiAndRemoveItFromH2() {
        University university = new University();
        university.setName("Delete University");
        university.setCity("Phoenix");
        university = universityRepository.save(university);

        Department department = new Department();
        department.setName("Accounting");
        department.setUniversity(university);
        department = departmentRepository.save(department);

        Course course = new Course();
        course.setCourseCode("AC300");
        course.setTitle("Managerial Accounting");
        course.setCreditHours(3);
        course.setDepartment(department);
        course = courseRepository.save(course);

        Student student = new Student();
        student.setFirstName("Delete");
        student.setLastName("Me");
        student.setEmail("delete.me@example.com");
        student.setDepartment(department);
        student = studentRepository.save(student);

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setTermName("SPRING-2026");
        enrollmentRepository.save(enrollment);

        ResponseEntity<Void> response = restTemplate.exchange(
                url("/api/universities/" + university.getId()),
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(universityRepository.findById(university.getId())).isEmpty();
        assertThat(departmentRepository.findAll()).isEmpty();
        assertThat(courseRepository.findAll()).isEmpty();
        assertThat(studentRepository.findAll()).isEmpty();
        assertThat(enrollmentRepository.findAll()).isEmpty();
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }
}
