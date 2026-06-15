package mk.ukim.finki.wp.molbi.service.implementations;

import jakarta.persistence.EntityNotFoundException;
import mk.ukim.finki.wp.molbi.model.base.Student;
import mk.ukim.finki.wp.molbi.model.requests.InstallmentPaymentStudentRequest;
import mk.ukim.finki.wp.molbi.repository.RequestSessionRepository;
import mk.ukim.finki.wp.molbi.repository.StudentRepository;
import mk.ukim.finki.wp.molbi.repository.StudentRequestRepository;
import mk.ukim.finki.wp.molbi.service.interfaces.InstallmentPaymentStudentRequestService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;


@Service
public class InstallmentPaymentStudentRequestServiceImpl
        extends StudentRequestServiceImpl<InstallmentPaymentStudentRequest>
        implements InstallmentPaymentStudentRequestService {

    protected InstallmentPaymentStudentRequestServiceImpl(StudentRequestRepository<InstallmentPaymentStudentRequest> repository, RequestSessionRepository requestSessionRepository, StudentRepository studentRepository, ApplicationEventPublisher eventPublisher) {
        super(repository, requestSessionRepository, studentRepository, eventPublisher);
    }

    @Override
    public InstallmentPaymentStudentRequest create(Long sessionId, String studentId,
                                                   String description, Integer installmentsNum) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));
        InstallmentPaymentStudentRequest request = new InstallmentPaymentStudentRequest();
        request.setDescription(description);
        request.setInstallmentsNum(installmentsNum);
        populateBaseFields(request, sessionId, student);
        return repository.save(request);
    }
}
