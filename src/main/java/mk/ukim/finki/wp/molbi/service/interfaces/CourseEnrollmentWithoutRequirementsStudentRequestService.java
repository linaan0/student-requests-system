package mk.ukim.finki.wp.molbi.service.interfaces;

import mk.ukim.finki.wp.molbi.model.requests.CourseEnrollmentWithoutRequirementsStudentRequest;

public interface CourseEnrollmentWithoutRequirementsStudentRequestService extends StudentRequestService<CourseEnrollmentWithoutRequirementsStudentRequest> {
    CourseEnrollmentWithoutRequirementsStudentRequest create(Long sessionId, String studentId,
                                                             String description,
                                                             String joinedSubjectId);
     boolean existsBySessionId(Long sessionId);
}