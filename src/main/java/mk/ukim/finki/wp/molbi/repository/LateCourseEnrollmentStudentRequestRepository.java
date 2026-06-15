package mk.ukim.finki.wp.molbi.repository;

import mk.ukim.finki.wp.molbi.model.base.Professor;
import mk.ukim.finki.wp.molbi.model.requests.LateCourseEnrollmentStudentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface LateCourseEnrollmentStudentRequestRepository extends StudentRequestRepository<LateCourseEnrollmentStudentRequest> {
    Page<LateCourseEnrollmentStudentRequest> findAllByProfessor(
            Professor professor,
            Pageable pageable
    );
}
