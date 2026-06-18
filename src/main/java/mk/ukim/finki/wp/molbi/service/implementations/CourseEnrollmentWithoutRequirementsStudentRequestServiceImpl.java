package mk.ukim.finki.wp.molbi.service.implementations;

import jakarta.persistence.EntityNotFoundException;
import mk.ukim.finki.wp.molbi.event.RequestStatusChangedEvent;
import mk.ukim.finki.wp.molbi.model.base.*;
import mk.ukim.finki.wp.molbi.model.requests.CourseEnrollmentWithoutRequirementsStudentRequest;
import mk.ukim.finki.wp.molbi.repository.*;
import mk.ukim.finki.wp.molbi.service.interfaces.CourseEnrollmentWithoutRequirementsStudentRequestService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class CourseEnrollmentWithoutRequirementsStudentRequestServiceImpl
        extends StudentRequestServiceImpl<CourseEnrollmentWithoutRequirementsStudentRequest>
        implements CourseEnrollmentWithoutRequirementsStudentRequestService {

    private final JoinedSubjectRepository joinedSubjectRepository;
    private final StudentSubjectEnrollmentRepository studentSubjectEnrollmentRepository;


    protected CourseEnrollmentWithoutRequirementsStudentRequestServiceImpl(StudentRequestRepository<CourseEnrollmentWithoutRequirementsStudentRequest> repository, RequestSessionRepository requestSessionRepository, StudentRepository studentRepository, JoinedSubjectRepository joinedSubjectRepository, ApplicationEventPublisher eventPublisher, StudentSubjectEnrollmentRepository studentSubjectEnrollmentRepository) {
        super(repository, requestSessionRepository, studentRepository, eventPublisher);
        this.joinedSubjectRepository = joinedSubjectRepository;
        this.studentSubjectEnrollmentRepository = studentSubjectEnrollmentRepository;
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
    @Override
    public boolean existsBySessionId(Long sessionId) {
        return repository.existsByRequestSession_Id(sessionId);
    }

    @Override
    public CourseEnrollmentWithoutRequirementsStudentRequest approve(Long id) {
        CourseEnrollmentWithoutRequirementsStudentRequest request = findById(id);
        if (!request.canBeApproved())
            throw new IllegalStateException("Request cannot be approved");

        JoinedSubject joinedSubject = request.getJoinedSubject();
        Student student = request.getStudent();
        Semester semester = request.getRequestSession().getSemester();
        Subject subject = joinedSubject.getMainSubject();

        String enrollmentId = String.format("%s-%s-%s",
                semester.getCode(), student.getIndex(), subject.getId());

        StudentSubjectEnrollment enrollment = studentSubjectEnrollmentRepository.findById(enrollmentId)
                .orElse(null);

        if (enrollment == null) {
            // new entry
            enrollment = new StudentSubjectEnrollment(semester, student, subject);
            enrollment.setJoinedSubject(joinedSubject);
            enrollment.setValid(true);
        } else {
            //an invalid enrollment already exists
            if (Boolean.TRUE.equals(enrollment.getValid())) {
                throw new IllegalStateException(
                        "Student already has a valid enrollment for this subject");
            }
            enrollment.setValid(true);
            enrollment.setInvalidNote(null);
            enrollment.setJoinedSubject(joinedSubject);
        }

        studentSubjectEnrollmentRepository.save(enrollment);
        request.setIsApproved(true);
        request.setIsProcessed(true);
        request.setDateProcessed(LocalDate.now());

        CourseEnrollmentWithoutRequirementsStudentRequest saved = repository.save(request);
        eventPublisher.publishEvent(new RequestStatusChangedEvent(saved));
        return saved;
    }
}
