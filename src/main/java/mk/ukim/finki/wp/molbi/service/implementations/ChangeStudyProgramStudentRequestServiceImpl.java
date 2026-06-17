package mk.ukim.finki.wp.molbi.service.implementations;

import jakarta.persistence.EntityNotFoundException;
import mk.ukim.finki.wp.molbi.model.base.Student;
import mk.ukim.finki.wp.molbi.model.base.StudyProgram;
import mk.ukim.finki.wp.molbi.model.requests.ChangeStudyProgramStudentRequest;
import mk.ukim.finki.wp.molbi.repository.RequestSessionRepository;
import mk.ukim.finki.wp.molbi.repository.StudentRepository;
import mk.ukim.finki.wp.molbi.repository.StudentRequestRepository;
import mk.ukim.finki.wp.molbi.repository.StudyProgramRepository;
import mk.ukim.finki.wp.molbi.service.interfaces.ChangeStudyProgramStudentRequestService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class ChangeStudyProgramStudentRequestServiceImpl
        extends StudentRequestServiceImpl<ChangeStudyProgramStudentRequest>
        implements ChangeStudyProgramStudentRequestService {

    private final StudyProgramRepository studyProgramRepository;

    protected ChangeStudyProgramStudentRequestServiceImpl(StudentRequestRepository<ChangeStudyProgramStudentRequest> repository, RequestSessionRepository requestSessionRepository, StudentRepository studentRepository, StudyProgramRepository studyProgramRepository, ApplicationEventPublisher eventPublisher) {
        super(repository, requestSessionRepository, studentRepository, eventPublisher);
        this.studyProgramRepository = studyProgramRepository;
    }

    @Override
    public ChangeStudyProgramStudentRequest create(Long sessionId, String studentId,
                                                   String description,
                                                   String newProgramId, String oldProgramId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));
        StudyProgram newProgram = studyProgramRepository.findById(newProgramId)
                .orElseThrow(() -> new EntityNotFoundException("Program not found"));
        StudyProgram oldProgram = studyProgramRepository.findById(oldProgramId)
                .orElseThrow(() -> new EntityNotFoundException("Program not found"));

        ChangeStudyProgramStudentRequest request = new ChangeStudyProgramStudentRequest();
        request.setDescription(description);
        request.setNewStudyProgram(newProgram);
        request.setOldStudyProgram(oldProgram);
        populateBaseFields(request, sessionId, student);
        return repository.save(request);
    }

    @Override
    public boolean existsBySessionId(Long sessionId) {
        return repository.existsByRequestSession_Id(sessionId);
    }

}