package mk.ukim.finki.wp.molbi.web;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.wp.molbi.config.FacultyUserDetails;
import mk.ukim.finki.wp.molbi.model.enums.RequestType;
import mk.ukim.finki.wp.molbi.model.requests.LateCourseEnrollmentStudentRequest;
import mk.ukim.finki.wp.molbi.service.interfaces.LateCourseEnrollmentStudentRequestService;
import mk.ukim.finki.wp.molbi.service.interfaces.RequestSessionService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/professor/requests")
@RequiredArgsConstructor
public class ProfessorRequestController {

    private final LateCourseEnrollmentStudentRequestService service;
    private final RequestSessionService requestSessionService;

    @GetMapping("/late-enrollment")
    public String list(@AuthenticationPrincipal FacultyUserDetails userDetails,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "5") int size,
                       @RequestParam(required = false) RequestType type,
                       @RequestParam(required = false) Boolean isApproved,
                       @RequestParam(required = false) Boolean isProcessed,
                       @RequestParam(required = false) Long sessionId,
                       Model model) {

        Page<LateCourseEnrollmentStudentRequest> requests =
                service.findAllByProfessor(
                        userDetails.getProfessor(),
                        page,
                        size,
                        type,
                        isApproved,
                        isProcessed,
                        sessionId
                );

        model.addAttribute("requestTypes", RequestType.values());
        model.addAttribute("sessions", requestSessionService.findAll());

        model.addAttribute("selectedType", type);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", requests.getTotalPages());
        model.addAttribute("requests", requests);

        return "professor/requests/list";
    }

    @GetMapping("/late-enrollment/{id}")
    public String details(@PathVariable Long id,
                          @AuthenticationPrincipal FacultyUserDetails userDetails,
                          Model model) {
        LateCourseEnrollmentStudentRequest request = service.findById(id);
        validateOwnership(request, userDetails);
        model.addAttribute("request", request);
        return "professor/requests/details";
    }

    @PostMapping("/late-enrollment/{id}/approve")
    public String approve(@PathVariable Long id,
                          @AuthenticationPrincipal FacultyUserDetails userDetails,
                          @RequestParam(defaultValue = "") String redirectUrl) {
        LateCourseEnrollmentStudentRequest request = service.findById(id);
        validateOwnership(request, userDetails);
        try {
            service.approveByProfessor(id);
        } catch (IllegalStateException e) {
            return redirect(id, redirectUrl) + "?error=" + e.getMessage();
        }
        return redirect(id, redirectUrl);
    }

    @PostMapping("/late-enrollment/{id}/reject")
    public String reject(@PathVariable Long id,
                         @AuthenticationPrincipal FacultyUserDetails userDetails,
                         @RequestParam(defaultValue = "") String redirectUrl) {
        LateCourseEnrollmentStudentRequest request = service.findById(id);
        validateOwnership(request, userDetails);
        try {
            service.rejectByProfessor(id);
        } catch (IllegalStateException e) {
            return redirect(id, redirectUrl) + "?error=" + e.getMessage();
        }
        return redirect(id, redirectUrl);
    }

    private void validateOwnership(LateCourseEnrollmentStudentRequest request,
                                   FacultyUserDetails userDetails) {
        if (!request.getProfessor().getId().equals(userDetails.getProfessor().getId()))
            throw new IllegalStateException("Access denied");
    }

    private String redirect(Long id, String redirectUrl) {
        if (redirectUrl == null || redirectUrl.isBlank())
            return "redirect:/professor/requests/late-enrollment/" + id;
        return "redirect:" + redirectUrl;
    }
}