package mk.ukim.finki.wp.molbi.service.implementations;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import mk.ukim.finki.wp.molbi.event.RequestStatusChangedEvent;
import mk.ukim.finki.wp.molbi.model.base.Student;
import mk.ukim.finki.wp.molbi.model.requests.RequestSession;
import mk.ukim.finki.wp.molbi.model.requests.StudentRequest;
import mk.ukim.finki.wp.molbi.repository.*;
import mk.ukim.finki.wp.molbi.service.interfaces.StudentRequestService;
import mk.ukim.finki.wp.molbi.service.specifications.FieldFilterSpecification;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Transactional
public abstract class StudentRequestServiceImpl<T extends StudentRequest>
        implements StudentRequestService<T> {

    protected final StudentRequestRepository<T> repository;
    protected final RequestSessionRepository requestSessionRepository;
    protected final StudentRepository studentRepository;protected final ApplicationEventPublisher eventPublisher;
    protected StudentRequestServiceImpl(StudentRequestRepository<T> repository,
                                        RequestSessionRepository requestSessionRepository,
                                        StudentRepository studentRepository,
                                        ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.requestSessionRepository = requestSessionRepository;
        this.studentRepository = studentRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Page<T> findAll(int page, int size, Boolean isApproved, Boolean isProcessed, Long sessionId) {
        Specification<T> spec = Specification
                .<T>where(FieldFilterSpecification.filterEqualsV(null, "isApproved", isApproved))
                .and(FieldFilterSpecification.filterEqualsV(null, "isProcessed", isProcessed))
                .and(FieldFilterSpecification.filterEqualsV(null, "requestSession.id", sessionId));
        return repository.findAll(spec, PageRequest.of(page, size));
    }

    @Override
    public T findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found"));
    }

    @Override
    public List<T> findByStudent(Student student) {
        Specification<T> spec = FieldFilterSpecification.filterEqualsV(null, "student", student);
        return repository.findAll(spec);
    }

    @Override
    public Page<T> findBySession(RequestSession session, int page, int size) {
        Specification<T> spec = FieldFilterSpecification
                .filterEqualsV(null, "requestSession", session);
        return repository.findAll(spec, PageRequest.of(page, size));
    }

    @Override
    public T approve(Long id) {
        T request = findById(id);
        if (!request.canBeApproved())
            throw new IllegalStateException("Request cannot be approved");
        request.setIsApproved(true);
        T saved = repository.save(request);
        eventPublisher.publishEvent(new RequestStatusChangedEvent(saved));

        return saved;
    }

    @Override
    public T reject(Long id, String reason) {
        T request = findById(id);
        if ( request.getIsApproved()!=null&& request.getIsApproved() &&!request.canBeRejected())
            throw new IllegalStateException("Request cannot be rejected");
        request.setIsApproved(false);
        request.setRejectionReason(reason);
        request.setDateProcessed(LocalDate.now());
        T saved = repository.save(request);
        eventPublisher.publishEvent(new RequestStatusChangedEvent(saved));
        return saved;
    }

    @Override
    public T markAsProcessed(Long id) {
        T request = findById(id);
        if (!request.canBeMarkedAsProcessed())
            throw new IllegalStateException("Request cannot be marked as processed");
        request.setIsProcessed(true);
        request.setDateProcessed(LocalDate.now());
        T saved = repository.save(request);
        eventPublisher.publishEvent(new RequestStatusChangedEvent(saved));
        return saved;
    }

    protected void populateBaseFields(T request, Long sessionId, Student student) {
        RequestSession session = requestSessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));
        if (!session.getTimeFrom().isBefore(LocalDateTime.now()) ||
                !session.getTimeTo().isAfter(LocalDateTime.now()))
            throw new IllegalStateException("Session is not active");
        request.setRequestSession(session);
        request.setStudent(student);
        request.setDateCreated(LocalDate.now());
    }
}