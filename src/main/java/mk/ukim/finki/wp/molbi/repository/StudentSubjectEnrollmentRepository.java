package mk.ukim.finki.wp.molbi.repository;

import mk.ukim.finki.wp.molbi.model.base.Semester;
import mk.ukim.finki.wp.molbi.model.base.Student;
import mk.ukim.finki.wp.molbi.model.base.StudentSubjectEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentSubjectEnrollmentRepository extends JpaRepository<StudentSubjectEnrollment, String> {
    List<StudentSubjectEnrollment> findByStudentAndSemester(Student student, Semester semester);
}
