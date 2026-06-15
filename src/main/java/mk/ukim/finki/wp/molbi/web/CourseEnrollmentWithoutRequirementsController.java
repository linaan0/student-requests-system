package mk.ukim.finki.wp.molbi.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mk.ukim.finki.wp.molbi.config.FacultyUserDetails;
import mk.ukim.finki.wp.molbi.model.base.Student;
import mk.ukim.finki.wp.molbi.model.dto.ChangeStudyProgramForm;
import mk.ukim.finki.wp.molbi.model.dto.CourseEnrollmentWithoutRequirementsForm;
import mk.ukim.finki.wp.molbi.model.enums.RequestType;
import mk.ukim.finki.wp.molbi.model.requests.CourseEnrollmentWithoutRequirementsStudentRequest;
import mk.ukim.finki.wp.molbi.model.requests.RequestSession;
import mk.ukim.finki.wp.molbi.service.interfaces.CourseEnrollmentWithoutRequirementsStudentRequestService;
import mk.ukim.finki.wp.molbi.service.interfaces.JoinedSubjectService;
import mk.ukim.finki.wp.molbi.service.interfaces.RequestSessionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/requests/enrollment-requirements")
@RequiredArgsConstructor
public class CourseEnrollmentWithoutRequirementsController extends BaseStudentRequestController {

    private final CourseEnrollmentWithoutRequirementsStudentRequestService service;
    private final RequestSessionService requestSessionService;
    private final JoinedSubjectService joinedSubjectService;

    @GetMapping("/new")
    public String form(@AuthenticationPrincipal FacultyUserDetails userDetails,
                       Model model) {
        Optional<RequestSession> session = requestSessionService.getActiveByType(RequestType.COURSE_ENROLLMENT_WITHOUT_REQUIREMENTS);
        if (session.isEmpty())
            return "redirect:/requests/new?error=no-active-session";

        CourseEnrollmentWithoutRequirementsForm form=new CourseEnrollmentWithoutRequirementsForm();
        form.setSessionId(session.get().getId());


        model.addAttribute("session", session.get());
        model.addAttribute("subjects",
                joinedSubjectService.findSubjectsWithUnmetRequirements(
                        getCurrentStudent(userDetails)));
        model.addAttribute("form", form);
        return "requests/enrollment-without-requirements/form";
    }

    @PostMapping
    public String create(@ModelAttribute @Valid CourseEnrollmentWithoutRequirementsForm form,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal FacultyUserDetails userDetails,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("subjects",
                    joinedSubjectService.findSubjectsWithUnmetRequirements(
                            getCurrentStudent(userDetails)));
            return "requests/enrollment-without-requirements/form";
        }
        try {
            Student student = getCurrentStudent(userDetails);
            service.create(
                    form.getSessionId(),
                    student.getIndex(),
                    form.getDescription(),
                    form.getJoinedSubjectId()
            );
            return "redirect:/requests";
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("subjects",
                    joinedSubjectService.findSubjectsWithUnmetRequirements(
                            getCurrentStudent(userDetails)));
            return "requests/enrollment-without-requirements/form";
        }
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id,
                          @AuthenticationPrincipal FacultyUserDetails userDetails,
                          Model model) {
        CourseEnrollmentWithoutRequirementsStudentRequest request = service.findById(id);
        validateOwnership(request, userDetails);
        model.addAttribute("request", request);
        return "requests/enrollment-without-requirements/details";
    }

}