package mk.ukim.finki.wp.molbi.service.interfaces;

import mk.ukim.finki.wp.molbi.model.dto.RequestSessionDto;
import mk.ukim.finki.wp.molbi.model.requests.RequestSession;
import mk.ukim.finki.wp.molbi.model.enums.RequestType;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RequestSessionService {

    List<RequestSession> findAll();

    Page<RequestSession> filter(
            int page,
            int size,
            LocalDateTime from,
            LocalDateTime to,
            String semesterCode,
            RequestType type
    );

    RequestSession findById(Long id);

    RequestSession create(RequestSessionDto dto);

    RequestSession update(Long id, RequestSessionDto dto);

    RequestSession getActiveSessions();

    Optional<RequestSession> getActiveByType(RequestType type);

    List<RequestSession> getEndedSessions();

    List<RequestSession> findAllByTypes(List<RequestType> types);



}