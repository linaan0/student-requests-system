package mk.ukim.finki.wp.molbi.service.implementations;

import jakarta.persistence.EntityNotFoundException;
import mk.ukim.finki.wp.molbi.event.RequestStatusChangedEvent;
import mk.ukim.finki.wp.molbi.model.base.*;
import mk.ukim.finki.wp.molbi.model.enums.RequestType;
import mk.ukim.finki.wp.molbi.model.requests.LateCourseEnrollmentStudentRequest;
import mk.ukim.finki.wp.molbi.repository.*;
import mk.ukim.finki.wp.molbi.service.interfaces.LateCourseEnrollmentStudentRequestService;
import mk.ukim.finki.wp.molbi.service.specifications.FieldFilterSpecification;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class LateCourseEnrollmentStudentRequestServiceImpl
        extends StudentRequestServiceImpl<LateCourseEnrollmentStudentRequest>
        implements LateCourseEnrollmentStudentRequestService {

    private final JoinedSubjectRepository joinedSubjectRepository;
    private final ProfessorRepository professorRepository;
    private final StudentSubjectEnrollmentRepository studentSubjectEnrollmentRepository;

    protected LateCourseEnrollmentStudentRequestServiceImpl(StudentRequestRepository<LateCourseEnrollmentStudentRequest> repository, RequestSessionRepository requestSessionRepository, StudentRepository studentRepository, JoinedSubjectRepository joinedSubjectRepository, ProfessorRepository professorRepository, ApplicationEventPublisher eventPublisher, StudentSubjectEnrollmentRepository studentSubjectEnrollmentRepository) {
        super(repository, requestSessionRepository, studentRepository, eventPublisher);
        this.joinedSubjectRepository = joinedSubjectRepository;
        this.professorRepository = professorRepository;
        this.studentSubjectEnrollmentRepository = studentSubjectEnrollmentRepository;
    }

    @Override
    public LateCourseEnrollmentStudentRequest create(Long sessionId, String studentId,
                                                     String description,
                                                     String joinedSubjectId, String professorId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));
        JoinedSubject joinedSubject = joinedSubjectRepository.findById(joinedSubjectId)
                .orElseThrow(() -> new EntityNotFoundException("JoinedSubject not found"));
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new EntityNotFoundException("Professor not found"));

        LateCourseEnrollmentStudentRequest request = new LateCourseEnrollmentStudentRequest();
        request.setDescription(description);
        request.setJoinedSubject(joinedSubject);
        request.setProfessor(professor);
        populateBaseFields(request, sessionId, student);
        return repository.save(request);
    }

    @Override
    public LateCourseEnrollmentStudentRequest approveByProfessor(Long id) {
        LateCourseEnrollmentStudentRequest request = findById(id);
        request.setProfessorApproved(true);
        return repository.save(request);
    }

    @Override
    public LateCourseEnrollmentStudentRequest rejectByProfessor(Long id) {
        LateCourseEnrollmentStudentRequest request = findById(id);
        request.setProfessorApproved(false);
        request.setDateProcessed(LocalDate.now());
        return repository.save(request);
    }

    @Override
    public LateCourseEnrollmentStudentRequest approve(Long id) {
        LateCourseEnrollmentStudentRequest request = findById(id);
        JoinedSubject joinedSubject = request.getJoinedSubject();
        Student student = request.getStudent();
        Semester semester = request.getRequestSession().getSemester();
        Subject subject = joinedSubject.getMainSubject();

        String enrollmentId = String.format("%s-%s-%s",
                semester.getCode(), student.getIndex(), subject.getId());

        StudentSubjectEnrollment enrollment = studentSubjectEnrollmentRepository.findById(enrollmentId)
                .orElse(null);

        if (enrollment == null) {
            enrollment = new StudentSubjectEnrollment(semester, student, subject);
            enrollment.setJoinedSubject(joinedSubject);
            enrollment.setValid(true);
        } else {
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
        markAsProcessed(id);
        request.setDateProcessed(LocalDate.now());

        LateCourseEnrollmentStudentRequest saved = repository.save(request);
        eventPublisher.publishEvent(new RequestStatusChangedEvent(saved));

        return saved;
    }

    @Override
    public Page<LateCourseEnrollmentStudentRequest> findAllByProfessor(
            Professor professor,
            int pageNum,
            int pageSize,
            RequestType type,
            Boolean isApproved,
            Boolean isProcessed,
            Long sessionId) {

        Pageable pageable = PageRequest.of(pageNum, pageSize);

        Specification<LateCourseEnrollmentStudentRequest> spec =
                Specification.where(
                        FieldFilterSpecification.filterEqualsV(
                                LateCourseEnrollmentStudentRequest.class,
                                "professor",
                                professor
                        )
                );

        if (type != null) {
            spec = spec.and(FieldFilterSpecification.filterEqualsV(
                    LateCourseEnrollmentStudentRequest.class,
                    "requestSession.requestType",
                    type
            ));
        }

        if (sessionId != null) {
            spec = spec.and(FieldFilterSpecification.filterEqualsV(
                    LateCourseEnrollmentStudentRequest.class,
                    "requestSession.id",
                    sessionId
            ));
        }

        if (isApproved != null) {
            spec = spec.and(FieldFilterSpecification.filterEqualsV(
                    LateCourseEnrollmentStudentRequest.class,
                    "isApproved",
                    isApproved
            ));
        }

        if (isProcessed != null) {
            spec = spec.and(FieldFilterSpecification.filterEqualsV(
                    LateCourseEnrollmentStudentRequest.class,
                    "isProcessed",
                    isProcessed
            ));
        }

        return repository.findAll(spec, pageable);
    }
    @Override
    public boolean existsBySessionId(Long sessionId) {
        return repository.existsByRequestSession_Id(sessionId);
    }
}