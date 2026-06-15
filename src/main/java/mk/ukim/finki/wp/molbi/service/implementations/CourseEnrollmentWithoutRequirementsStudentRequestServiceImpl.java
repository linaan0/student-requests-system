package mk.ukim.finki.wp.molbi.service.implementations;

import jakarta.persistence.EntityNotFoundException;
import mk.ukim.finki.wp.molbi.model.base.JoinedSubject;
import mk.ukim.finki.wp.molbi.model.base.Student;
import mk.ukim.finki.wp.molbi.model.requests.CourseEnrollmentWithoutRequirementsStudentRequest;
import mk.ukim.finki.wp.molbi.repository.JoinedSubjectRepository;
import mk.ukim.finki.wp.molbi.repository.RequestSessionRepository;
import mk.ukim.finki.wp.molbi.repository.StudentRepository;
import mk.ukim.finki.wp.molbi.repository.StudentRequestRepository;
import mk.ukim.finki.wp.molbi.service.interfaces.CourseEnrollmentWithoutRequirementsStudentRequestService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class CourseEnrollmentWithoutRequirementsStudentRequestServiceImpl
        extends StudentRequestServiceImpl<CourseEnrollmentWithoutRequirementsStudentRequest>
        implements CourseEnrollmentWithoutRequirementsStudentRequestService {

    private final JoinedSubjectRepository joinedSubjectRepository;


    protected CourseEnrollmentWithoutRequirementsStudentRequestServiceImpl(StudentRequestRepository<CourseEnrollmentWithoutRequirementsStudentRequest> repository, RequestSessionRepository requestSessionRepository, StudentRepository studentRepository, JoinedSubjectRepository joinedSubjectRepository, ApplicationEventPublisher eventPublisher) {
        super(repository, requestSessionRepository, studentRepository, eventPublisher);
        this.joinedSubjectRepository = joinedSubjectRepository;
    }


    @Override
    public CourseEnrollmentWithoutRequirementsStudentRequest create(Long sessionId, String studentId,
                                                                    String description,
                                                                    String joinedSubjectId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));
        JoinedSubject joinedSubject = joinedSubjectRepository.findById(joinedSubjectId)
                .orElseThrow(() -> new EntityNotFoundException("JoinedSubject not found"));

        CourseEnrollmentWithoutRequirementsStudentRequest request =
                new CourseEnrollmentWithoutRequirementsStudentRequest();
        request.setDescription(description);
        request.setJoinedSubject(joinedSubject);
        populateBaseFields(request, sessionId, student);
        return repository.save(request);
    }
}
