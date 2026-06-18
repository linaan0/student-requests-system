package mk.ukim.finki.wp.molbi.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mk.ukim.finki.wp.molbi.config.FacultyUserDetails;
import mk.ukim.finki.wp.molbi.model.base.Student;
import mk.ukim.finki.wp.molbi.model.dto.ChangeStudyProgramForm;
import mk.ukim.finki.wp.molbi.model.enums.RequestType;
import mk.ukim.finki.wp.molbi.model.requests.ChangeStudyProgramStudentRequest;
import mk.ukim.finki.wp.molbi.model.requests.RequestSession;
import mk.ukim.finki.wp.molbi.service.interfaces.ChangeStudyProgramStudentRequestService;
import mk.ukim.finki.wp.molbi.service.interfaces.RequestSessionService;
import mk.ukim.finki.wp.molbi.service.interfaces.StudyProgramService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/requests/change-program")
@RequiredArgsConstructor
public class ChangeStudyProgramController extends BaseStudentRequestController {

    private final ChangeStudyProgramStudentRequestService service;
    private final RequestSessionService requestSessionService;
    private final StudyProgramService studyProgramService;

    @GetMapping("/new")
    public String form(@AuthenticationPrincipal FacultyUserDetails userDetails,
                       Model model) {
        Optional<RequestSession> session = requestSessionService.getActiveByType(RequestType.STUDY_PROGRAM_CHANGE);
        if (session.isEmpty())
            return "redirect:/requests/new?error=no-active-session";
        ChangeStudyProgramForm form = new ChangeStudyProgramForm();
        form.setSessionId(session.get().getId());
        form.setOldProgramId(getCurrentStudent(userDetails).getStudyProgram().getCode());

        model.addAttribute("session", session.get());
        model.addAttribute("studyPrograms",studyProgramService.findAll(getCurrentStudent(userDetails).getStudyProgram().getAccreditationYear(), getCurrentStudent(userDetails).getStudyProgram().getStudyCycle()));
        model.addAttribute("currentProgram", getCurrentStudent(userDetails).getStudyProgram());
        model.addAttribute("form", form);

        return "requests/change-program/form";
    }

    @PostMapping
    public String create(@ModelAttribute @Valid ChangeStudyProgramForm form,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal FacultyUserDetails userDetails,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("session", requestSessionService.findById(form.getSessionId()));
            model.addAttribute("studyPrograms",studyProgramService.findAll(getCurrentStudent(userDetails).getStudyProgram().getAccreditationYear(), getCurrentStudent(userDetails).getStudyProgram().getStudyCycle()));
            model.addAttribute("currentProgram", getCurrentStudent(userDetails).getStudyProgram());
            model.addAttribute("form", form);
            return "requests/change-program/form";
        }
        try {
            Student student = getCurrentStudent(userDetails);
            service.create(
                    form.getSessionId(),
                    student.getIndex(),
                    form.getDescription(),
                    form.getNewProgramId(),
                    form.getOldProgramId()
            );
            return "redirect:/requests";
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("session", requestSessionService.findById(form.getSessionId()));
            model.addAttribute("studyPrograms",studyProgramService.findAll(getCurrentStudent(userDetails).getStudyProgram().getAccreditationYear(), getCurrentStudent(userDetails).getStudyProgram().getStudyCycle()));
            model.addAttribute("currentProgram", getCurrentStudent(userDetails).getStudyProgram());
            model.addAttribute("form", form);
            return "requests/change-program/form";
        }
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id,
                          @AuthenticationPrincipal FacultyUserDetails userDetails,
                          Model model) {
        ChangeStudyProgramStudentRequest request = service.findById(id);
        validateOwnership(request, userDetails);
        model.addAttribute("request", request);
        return "requests/change-program/details";
    }

}