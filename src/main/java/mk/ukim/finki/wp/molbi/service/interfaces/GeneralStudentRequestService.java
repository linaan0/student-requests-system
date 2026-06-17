package mk.ukim.finki.wp.molbi.service.interfaces;

import mk.ukim.finki.wp.molbi.model.requests.GeneralStudentRequest;

public interface GeneralStudentRequestService
        extends StudentRequestService<GeneralStudentRequest> {
    GeneralStudentRequest create(Long sessionId, String studentId, String description);
     boolean existsBySessionId(Long sessionId);
}