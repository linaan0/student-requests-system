package mk.ukim.finki.wp.molbi.service.interfaces;

import mk.ukim.finki.wp.molbi.model.requests.InstallmentPaymentStudentRequest;

public interface InstallmentPaymentStudentRequestService
        extends StudentRequestService<InstallmentPaymentStudentRequest> {
    InstallmentPaymentStudentRequest create(Long sessionId, String studentId,
                                            String description, Integer installmentsNum);
}
