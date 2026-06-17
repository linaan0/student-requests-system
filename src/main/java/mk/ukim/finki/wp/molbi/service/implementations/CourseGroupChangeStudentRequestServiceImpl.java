package mk.ukim.finki.wp.molbi.service.implementations;

import jakarta.persistence.EntityNotFoundException;
import mk.ukim.finki.wp.molbi.model.base.JoinedSubject;
import mk.ukim.finki.wp.molbi.model.base.Professor;
import mk.ukim.finki.wp.molbi.model.base.Student;
import mk.ukim.finki.wp.molbi.model.requests.CourseGroupChangeStudentRequest;
import mk.ukim.finki.wp.molbi.repository.*;
import mk.ukim.finki.wp.molbi.service.interfaces.CourseGroupChangeStudentRequestService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class CourseGroupChangeStudentRequestServiceImpl
        extends StudentRequestServiceImpl<CourseGroupChangeStudentRequest>
        implements CourseGroupChangeStudentRequestService {

    private final JoinedSubjectRepository joinedSubjectRepository;
    private final ProfessorRepository professorRepository;

    protected CourseGroupChangeStudentRequestServiceImpl(StudentRequestRepository<CourseGroupChangeStudentRequest> repository, RequestSessionRepository requestSessionRepository, StudentRepository studentRepository, JoinedSubjectRepository joinedSubjectRepository, ProfessorRepository professorRepository, ApplicationEventPublisher eventPublisher) {
        super(repository, requestSessionRepository, studentRepository, eventPublisher);
        this.joinedSubjectRepository = joinedSubjectRepository;
        this.professorRepository = professorRepository;
    }


    @Override
    public CourseGroupChangeStudentRequest create(Long sessionId, String studentId,
                                                  String description, String joinedSubjectId,
                                                  String currentProfessorId, String newProfessorId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));
        JoinedSubject joinedSubject = joinedSubjectRepository.findById(joinedSubjectId)
                .orElseThrow(() -> new EntityNotFoundException("JoinedSubject not found"));
        Professor currentProfessor = professorRepository.findById(currentProfessorId)
                .orElseThrow(() -> new EntityNotFoundException("Current professor not found"));
        Professor newProfessor = professorRepository.findById(newProfessorId)
                .orElseThrow(() -> new EntityNotFoundException("New professor not found"));

        CourseGroupChangeStudentRequest request = new CourseGroupChangeStudentRequest();
        request.setDescription(description);
        request.setJoinedSubject(joinedSubject);
        request.setCurrentProfessor(currentProfessor);
        request.setNewProfessor(newProfessor);
        populateBaseFields(request, sessionId, student);
        return repository.save(request);
    }
    @Override
    public boolean existsBySessionId(Long sessionId) {
        return repository.existsByRequestSession_Id(sessionId);
    }
}