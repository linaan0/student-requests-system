package mk.ukim.finki.wp.molbi.repository;

import mk.ukim.finki.wp.molbi.model.requests.GeneralStudentRequest;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneralStudentRequestRepository extends
        StudentRequestRepository<GeneralStudentRequest>{
}
