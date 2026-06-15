package mk.ukim.finki.wp.molbi.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mk.ukim.finki.wp.molbi.config.FacultyUserDetails;
import mk.ukim.finki.wp.molbi.model.base.Student;
import mk.ukim.finki.wp.molbi.model.dto.GeneralRequestForm;
import mk.ukim.finki.wp.molbi.model.enums.RequestType;
import mk.ukim.finki.wp.molbi.model.requests.GeneralStudentRequest;
import mk.ukim.finki.wp.molbi.model.requests.RequestSession;
import mk.ukim.finki.wp.molbi.service.interfaces.GeneralStudentRequestService;
import mk.ukim.finki.wp.molbi.service.interfaces.RequestSessionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/requests/general")
@RequiredArgsConstructor
public class GeneralRequestController extends BaseStudentRequestController {

    private final GeneralStudentRequestService service;
    private final RequestSessionService requestSessionService;

    @GetMapping("/new")
    public String form(Model model) {
        Optional<RequestSession> session = requestSessionService.getActiveByType(RequestType.GENERAL);
        if (session.isEmpty())
            return "redirect:/requests/new?error=no-active-session";

        GeneralRequestForm form = new GeneralRequestForm();
        form.setSessionId(session.get().getId());

        model.addAttribute("session", session.get());
        model.addAttribute("form", form);

        return "requests/general/form";
    }

    @PostMapping
    public String create(@ModelAttribute @Valid GeneralRequestForm form,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal FacultyUserDetails userDetails,
                         Model model) {
        if (bindingResult.hasErrors()) {
            return "requests/general/form";
        }
        try {
            Student student = getCurrentStudent(userDetails);
            service.create(form.getSessionId(), student.getIndex(), form.getDescription());
            return "redirect:/requests";
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "requests/general/form";
        }
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id,
                          @AuthenticationPrincipal FacultyUserDetails userDetails,
                          Model model) {
        GeneralStudentRequest request = service.findById(id);
        validateOwnership(request, userDetails);
        model.addAttribute("request", request);
        return "requests/general/details";
    }
}