package mk.ukim.finki.wp.molbi.service.interfaces;

import mk.ukim.finki.wp.molbi.model.base.Professor;
import mk.ukim.finki.wp.molbi.model.enums.RequestType;
import mk.ukim.finki.wp.molbi.model.requests.LateCourseEnrollmentStudentRequest;
import org.springframework.data.domain.Page;

public interface LateCourseEnrollmentStudentRequestService extends StudentRequestService<LateCourseEnrollmentStudentRequest> {
    LateCourseEnrollmentStudentRequest create(Long sessionId, String studentId,
                                              String description,
                                              String joinedSubjectId, String professorId);

    LateCourseEnrollmentStudentRequest approveByProfessor(Long id);

    LateCourseEnrollmentStudentRequest rejectByProfessor(Long id);

    public Page<LateCourseEnrollmentStudentRequest> findAllByProfessor(
            Professor professor,
            int pageNum,
            int pageSize,
            RequestType type,
            Boolean isApproved,
            Boolean isProcessed,
            Long sessionId);

}