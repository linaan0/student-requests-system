package mk.ukim.finki.wp.molbi.service.implementations;

import jakarta.persistence.EntityNotFoundException;
import mk.ukim.finki.wp.molbi.model.base.Student;
import mk.ukim.finki.wp.molbi.model.requests.GeneralStudentRequest;
import mk.ukim.finki.wp.molbi.repository.RequestSessionRepository;
import mk.ukim.finki.wp.molbi.repository.StudentRepository;
import mk.ukim.finki.wp.molbi.repository.StudentRequestRepository;
import mk.ukim.finki.wp.molbi.service.interfaces.GeneralStudentRequestService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class GeneralStudentRequestServiceImpl
        extends StudentRequestServiceImpl<GeneralStudentRequest>
        implements GeneralStudentRequestService {

    protected GeneralStudentRequestServiceImpl(StudentRequestRepository<GeneralStudentRequest> repository, RequestSessionRepository requestSessionRepository, StudentRepository studentRepository, ApplicationEventPublisher eventPublisher) {
        super(repository, requestSessionRepository, studentRepository, eventPublisher);
    }

    @Override
    public GeneralStudentRequest create(Long sessionId, String studentId, String description) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));
        GeneralStudentRequest request = new GeneralStudentRequest();
        request.setDescription(description);
        populateBaseFields(request, sessionId, student);
        return repository.save(request);
    }
}
