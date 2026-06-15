package mk.ukim.finki.wp.molbi.service.interfaces;

import mk.ukim.finki.wp.molbi.model.enums.RequestType;
import mk.ukim.finki.wp.molbi.model.requests.StudentRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AdminStudentRequestService {

    Page<? extends StudentRequest> findAll(int page, int size,
                                           RequestType type,
                                           Boolean isApproved,
                                           Boolean isProcessed,
                                           Long sessionId, List<RequestType> allowedTypes);

    StudentRequest findById(Long id, RequestType type);

    StudentRequest approve(Long id, RequestType type);

    StudentRequest reject(Long id, RequestType type, String reason);

    StudentRequest markAsProcessed(Long id, RequestType type);
}