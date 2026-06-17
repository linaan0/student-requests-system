package mk.ukim.finki.wp.molbi.service.interfaces;

import mk.ukim.finki.wp.molbi.model.requests.CourseGroupChangeStudentRequest;

public interface CourseGroupChangeStudentRequestService extends StudentRequestService<CourseGroupChangeStudentRequest> {
    CourseGroupChangeStudentRequest create(Long sessionId, String studentId,
                                           String description, String joinedSubjectId,
                                           String currentProfessorId, String newProfessorId);
    boolean existsBySessionId(Long sessionId);
}

