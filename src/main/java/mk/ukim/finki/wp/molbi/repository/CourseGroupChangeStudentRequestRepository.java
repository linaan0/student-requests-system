package mk.ukim.finki.wp.molbi.repository;

import mk.ukim.finki.wp.molbi.model.requests.CourseGroupChangeStudentRequest;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseGroupChangeStudentRequestRepository
        extends StudentRequestRepository<CourseGroupChangeStudentRequest>{
}
