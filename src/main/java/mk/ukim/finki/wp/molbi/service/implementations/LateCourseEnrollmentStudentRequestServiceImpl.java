package mk.ukim.finki.wp.molbi.service.implementations;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import mk.ukim.finki.wp.molbi.model.base.JoinedSubject;
import mk.ukim.finki.wp.molbi.model.base.Professor;
import mk.ukim.finki.wp.molbi.model.base.Student;
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

    protected LateCourseEnrollmentStudentRequestServiceImpl(StudentRequestRepository<LateCourseEnrollmentStudentRequest> repository, RequestSessionRepository requestSessionRepository, StudentRepository studentRepository, JoinedSubjectRepository joinedSubjectRepository, ProfessorRepository professorRepository, ApplicationEventPublisher eventPublisher) {
        super(repository, requestSessionRepository, studentRepository, eventPublisher);
        this.joinedSubjectRepository = joinedSubjectRepository;
        this.professorRepository = professorRepository;
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
        request.setIsApproved(true);
        return repository.save(request);
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
}
