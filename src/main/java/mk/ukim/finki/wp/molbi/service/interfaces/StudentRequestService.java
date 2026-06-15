package mk.ukim.finki.wp.molbi.service.interfaces;

import mk.ukim.finki.wp.molbi.model.base.Student;
import mk.ukim.finki.wp.molbi.model.requests.RequestSession;
import mk.ukim.finki.wp.molbi.model.requests.StudentRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StudentRequestService<T extends StudentRequest> {
    Page<T> findAll(int page, int size, Boolean isApproved, Boolean isProcessed, Long sessionId);
    T findById(Long id);
    List<T> findByStudent(Student student);

    Page<T> findBySession(RequestSession session, int page, int size);
    T approve(Long id);
    T reject(Long id, String reason);
    T markAsProcessed(Long id);
}