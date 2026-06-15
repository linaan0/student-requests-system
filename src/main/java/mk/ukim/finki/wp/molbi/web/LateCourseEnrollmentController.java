package mk.ukim.finki.wp.molbi.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mk.ukim.finki.wp.molbi.config.FacultyUserDetails;
import mk.ukim.finki.wp.molbi.model.base.Student;
import mk.ukim.finki.wp.molbi.model.dto.GeneralRequestForm;
import mk.ukim.finki.wp.molbi.model.dto.LateCourseEnrollmentForm;
import mk.ukim.finki.wp.molbi.model.enums.RequestType;
import mk.ukim.finki.wp.molbi.model.requests.RequestSession;
import mk.ukim.finki.wp.molbi.service.interfaces.JoinedSubjectService;
import mk.ukim.finki.wp.molbi.service.interfaces.LateCourseEnrollmentStudentRequestService;
import mk.ukim.finki.wp.molbi.service.interfaces.ProfessorService;
import mk.ukim.finki.wp.molbi.service.interfaces.RequestSessionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/requests/late-enrollment")
@RequiredArgsConstructor
public class LateCourseEnrollmentController extends BaseStudentRequestController {

    private final LateCourseEnrollmentStudentRequestService service;
    private final RequestSessionService requestSessionService;
    private final JoinedSubjectService joinedSubjectService;
    private final ProfessorService professorService;

    @GetMapping("/new")
    public String form(Model model) {
        Optional<RequestSession> session = requestSessionService.getActiveByType(RequestType.LATE_COURSE_ENROLLMENT);
        if (session.isEmpty())
            return "redirect:/requests/new?error=no-active-session";

        LateCourseEnrollmentForm form = new LateCourseEnrollmentForm();
        form.setSessionId(session.get().getId());

        model.addAttribute("session", session.get());
        model.addAttribute("subjects", joinedSubjectService.findAll());
        model.addAttribute("professors", professorService.findAll());
        model.addAttribute("form", form);
        return "requests/late-enrollment/form";
    }

    @PostMapping
    public String create(@ModelAttribute @Valid LateCourseEnrollmentForm form,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal FacultyUserDetails userDetails,
                         Model model) {
        if (bindingResult.hasErrors()) return "requests/late-enrollment/form";
        try {
            Student student = getCurrentStudent(userDetails);
            service.create(form.getSessionId(), student.getIndex(),
                    form.getDescription(), form.getJoinedSubjectId(),
                    form.getProfessorId());
            return "redirect:/requests";
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "requests/late-enrollment/form";
        }
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        model.addAttribute("request", service.findById(id));
        return "requests/late-enrollment/details";
    }

    @PostMapping("/{id}/professor-approve")
    public String professorApprove(@PathVariable Long id) {
        try {
            service.approveByProfessor(id);
        } catch (IllegalStateException e) {
            return "redirect:/requests/late-enrollment/" + id + "?error=" + e.getMessage();
        }
        return "redirect:/requests/late-enrollment/" + id;
    }
}