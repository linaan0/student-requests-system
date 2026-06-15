package mk.ukim.finki.wp.molbi.event;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.wp.molbi.model.base.User;
import mk.ukim.finki.wp.molbi.model.requests.StudentRequest;
import mk.ukim.finki.wp.molbi.repository.UserRepository;
import mk.ukim.finki.wp.molbi.service.email.EmailService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

@Component
@RequiredArgsConstructor
public class RequestStatusChangedListener {

    private final UserRepository userRepository;
    private final EmailService emailService;

    @Async
    @EventListener
    public void onStatusChanged(RequestStatusChangedEvent event) {
        StudentRequest request = event.getRequest();
        System.out.println("EVENT FIRED!");

        User user = userRepository.findById(request.getStudent().getIndex()).orElse(null);
        if (user == null || user.getEmail() == null || user.getEmail().isBlank()) {
            return;
        }

        Context context = new Context();
        context.setVariable("studentName", user.getName());
        context.setVariable("requestType", request.getRequestSession().getRequestType().getDisplayName());
        context.setVariable("status", statusText(request));
        context.setVariable("rejectionReason", request.getRejectionReason());

        emailService.send(request.getStudent().getEmail(), "Ажуриран статус на молба", "email/status-update", context);
    }

    private String statusText(StudentRequest request) {
        if (Boolean.TRUE.equals(request.getIsProcessed())) return "Спроведена";
        if (Boolean.TRUE.equals(request.getIsApproved())) return "Одобрена";
        if (Boolean.FALSE.equals(request.getIsApproved())) return "Одбиена";
        return "Во обработка";
    }
}