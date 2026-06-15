package mk.ukim.finki.wp.molbi.service.interfaces;

import mk.ukim.finki.wp.molbi.model.requests.ChangeStudyProgramStudentRequest;

public interface ChangeStudyProgramStudentRequestService
        extends StudentRequestService<ChangeStudyProgramStudentRequest> {
    ChangeStudyProgramStudentRequest create(Long sessionId, String studentId,
                                            String description,
                                            String newProgramId, String oldProgramId);
}