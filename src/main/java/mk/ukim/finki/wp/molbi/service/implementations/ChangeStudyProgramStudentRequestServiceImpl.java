package mk.ukim.finki.wp.molbi.service.implementations;

import jakarta.persistence.EntityNotFoundException;
import mk.ukim.finki.wp.molbi.event.RequestStatusChangedEvent;
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
import java.time.LocalDate;

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
        if (newProgram.equals(oldProgram)) {
            throw new IllegalStateException(
                    "Новата програма мора да биде различна од тековната.");
        }

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

    @Override
    public ChangeStudyProgramStudentRequest approve(Long id) {
        ChangeStudyProgramStudentRequest request = findById(id);
        Student student=request.getStudent();
        student.setStudyProgram(request.getNewStudyProgram());
        studentRepository.save(student);
        request.setIsApproved(true);
        markAsProcessed(id);
        request.setDateProcessed(LocalDate.now());
        ChangeStudyProgramStudentRequest saved = repository.save(request);
        eventPublisher.publishEvent(new RequestStatusChangedEvent(saved));
        return saved;
    }

}