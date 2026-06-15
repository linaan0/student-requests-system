package mk.ukim.finki.wp.molbi.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mk.ukim.finki.wp.molbi.config.FacultyUserDetails;
import mk.ukim.finki.wp.molbi.model.base.Student;
import mk.ukim.finki.wp.molbi.model.dto.CourseGroupChangeForm;
import mk.ukim.finki.wp.molbi.model.enums.RequestType;
import mk.ukim.finki.wp.molbi.model.requests.CourseGroupChangeStudentRequest;
import mk.ukim.finki.wp.molbi.model.requests.RequestSession;
import mk.ukim.finki.wp.molbi.service.interfaces.CourseGroupChangeStudentRequestService;
import mk.ukim.finki.wp.molbi.service.interfaces.JoinedSubjectService;
import mk.ukim.finki.wp.molbi.service.interfaces.ProfessorService;
import mk.ukim.finki.wp.molbi.service.interfaces.RequestSessionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/requests/course-group-change")
@RequiredArgsConstructor
public class CourseGroupChangeController extends BaseStudentRequestController {

    private final CourseGroupChangeStudentRequestService service;
    private final RequestSessionService requestSessionService;
    private final JoinedSubjectService joinedSubjectService;
    private final ProfessorService professorService;

    @GetMapping("/new")
    public String form(@AuthenticationPrincipal FacultyUserDetails userDetails, Model model) {
        Optional<RequestSession> session = requestSessionService.getActiveByType(RequestType.COURSE_GROUP_CHANGE);
        if (session.isEmpty())
            return "redirect:/requests/new?error=no-active-session";

        CourseGroupChangeForm form=new CourseGroupChangeForm();
        form.setSessionId(session.get().getId());

        model.addAttribute("session", session.get());
        model.addAttribute("subjects", joinedSubjectService.findAll());
        model.addAttribute("professors", professorService.findAll());
        model.addAttribute("form", form);
        return "requests/course-group-change/form";
    }

    @PostMapping
    public String create(@ModelAttribute @Valid CourseGroupChangeForm form,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal FacultyUserDetails userDetails,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("subjects", joinedSubjectService.findAll());
            model.addAttribute("professors", professorService.findAll());
            return "requests/course-group-change/form";
        }
        try {
            Student student = getCurrentStudent(userDetails);
            service.create(
                    form.getSessionId(),
                    student.getIndex(),
                    form.getDescription(),
                    form.getJoinedSubjectId(),
                    form.getCurrentProfessorId(),
                    form.getNewProfessorId()
            );
            return "redirect:/requests";
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("subjects", joinedSubjectService.findAll());
            model.addAttribute("professors", professorService.findAll());
            return "requests/course-group-change/form";
        }
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id,
                          @AuthenticationPrincipal FacultyUserDetails userDetails,
                          Model model) {
        CourseGroupChangeStudentRequest request = service.findById(id);
        validateOwnership(request, userDetails);
        model.addAttribute("request", request);
        return "requests/course-group-change/details";
    }


}