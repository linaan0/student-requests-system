package mk.ukim.finki.wp.molbi.service.implementations;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import mk.ukim.finki.wp.molbi.model.base.Semester;
import mk.ukim.finki.wp.molbi.model.dto.RequestSessionDto;
import mk.ukim.finki.wp.molbi.model.enums.RequestType;
import mk.ukim.finki.wp.molbi.model.requests.RequestSession;
import mk.ukim.finki.wp.molbi.repository.RequestSessionRepository;
import mk.ukim.finki.wp.molbi.repository.SemesterRepository;
import mk.ukim.finki.wp.molbi.service.interfaces.AdminStudentRequestService;
import mk.ukim.finki.wp.molbi.service.interfaces.RequestSessionService;
import mk.ukim.finki.wp.molbi.service.specifications.FieldFilterSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestSessionServiceImpl implements RequestSessionService {

    private final RequestSessionRepository repository;
    private final SemesterRepository semesterRepository;
    private final AdminStudentRequestService adminStudentRequestService;

    @Override
    public List<RequestSession> findAll(){
        return repository.findAll();
    }

    @Override
    public Page<RequestSession> filter(int page, int size,
                                       LocalDateTime from,
                                       LocalDateTime to,
                                       String semesterCode,
                                       RequestType type) {

        Specification<RequestSession> spec = Specification.where(null);

        if (from != null) {
            spec = spec.and(FieldFilterSpecification.greaterThan(
                    RequestSession.class, "timeFrom", from));
        }

        if (to != null) {
            spec = spec.and(FieldFilterSpecification.lessThan(
                    RequestSession.class, "timeTo", to));
        }

        if (type != null) {
            spec = spec.and(FieldFilterSpecification.filterEqualsV(
                    RequestSession.class, "requestType", type));
        }

        if (semesterCode != null) {
            spec = spec.and(FieldFilterSpecification.filterEquals(
                    RequestSession.class, "semester.code", semesterCode));
        }

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("timeFrom").descending());

        return repository.findAll(spec, pageable);
    }

    @Override
    public RequestSession findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "RequestSession not found with id: " + id));
    }

    @Override
    public RequestSession create(RequestSessionDto dto) {

        if (dto.getTimeFrom().isAfter(dto.getTimeTo())) {
            throw new IllegalStateException(
                    "Датумот 'Активна од' мора да биде пред 'Активна до'");
        }

        validateNoOverlap(dto.getRequestType(),
                dto.getTimeFrom(),
                dto.getTimeTo(),
                null);

        Semester semester = semesterRepository.findById(dto.getSemesterId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Semester not found"));

        RequestSession session = new RequestSession();
        session.setTimeFrom(dto.getTimeFrom());
        session.setTimeTo(dto.getTimeTo());
        session.setSemester(semester);
        session.setRequestType(dto.getRequestType());
        session.setDescription(dto.getDescription());
        session.setApprovalNote(dto.getApprovalNote());

        return repository.save(session);
    }

    @Override
    public RequestSession update(Long id, RequestSessionDto dto) {
        RequestSession session = findById(id);

        validateNoOverlap(dto.getRequestType(),
                dto.getTimeFrom(),
                dto.getTimeTo(),
                id);

        Semester semester = semesterRepository.findById(dto.getSemesterId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Semester not found"));

        session.setTimeFrom(dto.getTimeFrom());
        session.setTimeTo(dto.getTimeTo());
        session.setSemester(semester);
        session.setRequestType(dto.getRequestType());
        session.setDescription(dto.getDescription());
        session.setApprovalNote(dto.getApprovalNote());

        return repository.save(session);
    }

    @Override
    public Optional<RequestSession> getActiveByType(RequestType type) {
        LocalDateTime now = LocalDateTime.now();

        Specification<RequestSession> spec = Specification
                .where(FieldFilterSpecification.filterEqualsV(
                        RequestSession.class, "requestType", type))
                .and((root, query, cb) ->
                        cb.lessThanOrEqualTo(root.get("timeFrom"), now))
                .and((root, query, cb) ->
                        cb.greaterThanOrEqualTo(root.get("timeTo"), now));

        return repository.findAll(spec)
                .stream()
                .findFirst();
    }

    @Override
    public RequestSession getActiveSessions() {
        LocalDateTime now = LocalDateTime.now();

        Specification<RequestSession> spec = Specification
                .<RequestSession>where((root, query, cb) ->
                        cb.lessThanOrEqualTo(root.get("timeFrom"), now))
                .and((root, query, cb) ->
                        cb.greaterThanOrEqualTo(root.get("timeTo"), now));

        return repository.findAll(spec)
                .stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "No active sessions"));
    }

    @Override
    public List<RequestSession> getEndedSessions() {
        LocalDateTime now = LocalDateTime.now();

        Specification<RequestSession> spec =
                (root, query, cb) -> cb.lessThan(root.get("timeTo"), now);

        return repository.findAll(spec);
    }

    private void validateNoOverlap(RequestType type,
                                   LocalDateTime from,
                                   LocalDateTime to,
                                   Long excludeId) {

        Specification<RequestSession> spec = Specification
                .where(FieldFilterSpecification.filterEqualsV(
                        RequestSession.class, "requestType", type))
                .and((root, query, cb) ->
                        cb.lessThan(root.get("timeFrom"), to))
                .and((root, query, cb) ->
                        cb.greaterThan(root.get("timeTo"), from));

        repository.findAll(spec)
                .stream()
                .filter(s -> excludeId == null || !s.getId().equals(excludeId))
                .findFirst()
                .ifPresent(s -> {
                    throw new IllegalStateException(
                            "Веќе постои сесија од истиот тип во овој временски период (ID: " + s.getId() + ")");
                });
    }

    @Override
    public List<RequestSession> findAllByTypes(List<RequestType> types) {
        if (types == null || types.isEmpty()) {
            return List.of();
        }
        return repository.findAll(
                (root, query, cb) -> root.get("requestType").in(types)
        );
    }
    @Override
    public void delete(Long id) {
        if (adminStudentRequestService.existsBySession(id)) {
            throw new IllegalStateException(
                    "Сесијата не може да се избрише бидејќи има поднесени молби.");
        }

        repository.deleteById(id);
    }
}