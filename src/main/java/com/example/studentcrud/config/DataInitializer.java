package com.example.studentcrud.config;

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
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class DataInitializer {

    @Bean
    CommandLineRunner loadSampleData(
            UniversityRepository universityRepository,
            DepartmentRepository departmentRepository,
            CourseRepository courseRepository,
            StudentRepository studentRepository,
            EnrollmentRepository enrollmentRepository
    ) {
        return args -> {
            if (universityRepository.count() > 0 || departmentRepository.count() > 0
                    || courseRepository.count() > 0 || studentRepository.count() > 0
                    || enrollmentRepository.count() > 0) {
                return;
            }

            University mit = new University();
            mit.setName("MIT");
            mit.setCity("Cambridge");

            University stanford = new University();
            stanford.setName("Stanford University");
            stanford.setCity("Stanford");

            mit = universityRepository.save(mit);
            stanford = universityRepository.save(stanford);

            Department mitComputerScience = new Department();
            mitComputerScience.setName("Computer Science");
            mitComputerScience.setUniversity(mit);

            Department stanfordBusiness = new Department();
            stanfordBusiness.setName("Business");
            stanfordBusiness.setUniversity(stanford);

            mitComputerScience = departmentRepository.save(mitComputerScience);
            stanfordBusiness = departmentRepository.save(stanfordBusiness);

            Course algorithms = new Course();
            algorithms.setCourseCode("CS601");
            algorithms.setTitle("Algorithms");
            algorithms.setCreditHours(4);
            algorithms.setDepartment(mitComputerScience);

            Course databases = new Course();
            databases.setCourseCode("CS615");
            databases.setTitle("Advanced Databases");
            databases.setCreditHours(3);
            databases.setDepartment(mitComputerScience);

            Course finance = new Course();
            finance.setCourseCode("BUS510");
            finance.setTitle("Corporate Finance");
            finance.setCreditHours(3);
            finance.setDepartment(stanfordBusiness);

            algorithms = courseRepository.save(algorithms);
            databases = courseRepository.save(databases);
            finance = courseRepository.save(finance);

            Student studentOne = new Student();
            studentOne.setFirstName("Alice");
            studentOne.setLastName("Johnson");
            studentOne.setEmail("alice.johnson@example.com");
            studentOne.setDepartment(mitComputerScience);

            Student studentTwo = new Student();
            studentTwo.setFirstName("Bob");
            studentTwo.setLastName("Smith");
            studentTwo.setEmail("bob.smith@example.com");
            studentTwo.setDepartment(stanfordBusiness);

            studentOne = studentRepository.save(studentOne);
            studentTwo = studentRepository.save(studentTwo);

            Enrollment enrollmentOne = new Enrollment();
            enrollmentOne.setStudent(studentOne);
            enrollmentOne.setCourse(algorithms);
            enrollmentOne.setTermName("SPRING-2026");

            Enrollment enrollmentTwo = new Enrollment();
            enrollmentTwo.setStudent(studentOne);
            enrollmentTwo.setCourse(databases);
            enrollmentTwo.setTermName("SPRING-2026");

            Enrollment enrollmentThree = new Enrollment();
            enrollmentThree.setStudent(studentTwo);
            enrollmentThree.setCourse(finance);
            enrollmentThree.setTermName("SPRING-2026");

            enrollmentRepository.save(enrollmentOne);
            enrollmentRepository.save(enrollmentTwo);
            enrollmentRepository.save(enrollmentThree);
        };
    }
}
