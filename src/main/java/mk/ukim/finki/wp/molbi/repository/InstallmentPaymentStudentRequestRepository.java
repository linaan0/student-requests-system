package mk.ukim.finki.wp.molbi.repository;

import mk.ukim.finki.wp.molbi.model.requests.InstallmentPaymentStudentRequest;
import org.springframework.stereotype.Repository;

@Repository
public interface InstallmentPaymentStudentRequestRepository
        extends StudentRequestRepository<InstallmentPaymentStudentRequest> {
}
