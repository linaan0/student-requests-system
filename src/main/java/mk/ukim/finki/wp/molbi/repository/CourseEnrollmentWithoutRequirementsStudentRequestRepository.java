package mk.ukim.finki.wp.molbi.repository;

import mk.ukim.finki.wp.molbi.model.requests.CourseEnrollmentWithoutRequirementsStudentRequest;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseEnrollmentWithoutRequirementsStudentRequestRepository
        extends StudentRequestRepository<CourseEnrollmentWithoutRequirementsStudentRequest> {
}
