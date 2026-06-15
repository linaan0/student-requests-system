package mk.ukim.finki.wp.molbi.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mk.ukim.finki.wp.molbi.config.FacultyUserDetails;
import mk.ukim.finki.wp.molbi.model.base.Student;
import mk.ukim.finki.wp.molbi.model.dto.GeneralRequestForm;
import mk.ukim.finki.wp.molbi.model.dto.InstallmentRequestForm;
import mk.ukim.finki.wp.molbi.model.enums.RequestType;
import mk.ukim.finki.wp.molbi.model.requests.RequestSession;
import mk.ukim.finki.wp.molbi.service.interfaces.InstallmentPaymentStudentRequestService;
import mk.ukim.finki.wp.molbi.service.interfaces.RequestSessionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/requests/installment")
@RequiredArgsConstructor
public class InstallmentRequestController extends BaseStudentRequestController {

    private final InstallmentPaymentStudentRequestService service;
    private final RequestSessionService requestSessionService;

    @GetMapping("/new")
    public String form(Model model) {
        Optional<RequestSession> session = requestSessionService.getActiveByType(RequestType.INSTALLMENT_PAYMENT);
        if (session.isEmpty())
            return "redirect:/requests/new?error=no-active-session";
        InstallmentRequestForm form = new InstallmentRequestForm();
        form.setSessionId(session.get().getId());
        model.addAttribute("session", session.get());
        model.addAttribute("form", form);
        return "requests/installment/form";
    }

    @PostMapping
    public String create(@ModelAttribute @Valid InstallmentRequestForm form,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal FacultyUserDetails userDetails,
                         Model model) {
        if (bindingResult.hasErrors()) return "requests/installment/form";
        try {
            Student student = getCurrentStudent(userDetails);
            service.create(form.getSessionId(), student.getIndex(),
                    form.getDescription(), form.getInstallmentsNum());
            return "redirect:/requests";
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "requests/installment/form";
        }
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        model.addAttribute("request", service.findById(id));
        return "requests/installment/details";
    }
}
