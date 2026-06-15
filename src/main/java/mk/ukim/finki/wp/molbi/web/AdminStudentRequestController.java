package mk.ukim.finki.wp.molbi.web;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.wp.molbi.config.FacultyUserDetails;
import mk.ukim.finki.wp.molbi.model.enums.AppRole;
import mk.ukim.finki.wp.molbi.model.enums.RequestType;
import mk.ukim.finki.wp.molbi.model.requests.StudentRequest;
import mk.ukim.finki.wp.molbi.service.interfaces.AdminStudentRequestService;
import mk.ukim.finki.wp.molbi.service.interfaces.RequestSessionService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/requests")
@RequiredArgsConstructor
public class AdminStudentRequestController {

    private final AdminStudentRequestService adminService;
    private final RequestSessionService requestSessionService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "5") int size,
                       @RequestParam(required = false) RequestType type,
                       @RequestParam(required = false) Boolean isApproved,
                       @RequestParam(required = false) Boolean isProcessed,
                       @RequestParam(required = false) Long sessionId,
                       @AuthenticationPrincipal FacultyUserDetails userDetails,
                       Model model) {

        AppRole role = userDetails.getUser().getRole().getApplicationRole();
        List<RequestType> allowedTypes = RequestType.forRole(role);

        if (type != null && !allowedTypes.contains(type)) {
            type = null;
        }

        Page<? extends StudentRequest> requests =
                adminService.findAll(page, size, type, isApproved, isProcessed, sessionId, allowedTypes);

        model.addAttribute("requests", requests);
        model.addAttribute("requestTypes", allowedTypes);
        model.addAttribute("sessions", requestSessionService.findAllByTypes(allowedTypes));
        model.addAttribute("selectedType", type);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", requests.getTotalPages());

        return "admin/requests/list";
    }

    @GetMapping("/{type}/{id}")
    public String details(@PathVariable RequestType type,
                          @PathVariable Long id,
                          Model model) {

        model.addAttribute("request", adminService.findById(id, type));
        model.addAttribute("type", type);

        return "admin/requests/details";
    }

    @PostMapping("/{type}/{id}/approve")
    public String approve(@PathVariable RequestType type,
                          @PathVariable Long id,
                          @RequestParam(defaultValue = "") String redirectUrl) {

        adminService.approve(id, type);

        return redirect(type, id, redirectUrl);
    }

    @PostMapping("/{type}/{id}/reject")
    public String reject(@PathVariable RequestType type,
                         @PathVariable Long id,
                         @RequestParam String reason,
                         @RequestParam(defaultValue = "") String redirectUrl) {

        adminService.reject(id, type, reason);

        return redirect(type, id, redirectUrl);
    }

    @PostMapping("/{type}/{id}/mark-as-processed")
    public String markAsProcessed(@PathVariable RequestType type,
                                  @PathVariable Long id,
                                  @RequestParam(defaultValue = "") String redirectUrl) {

        adminService.markAsProcessed(id, type);

        return redirect(type, id, redirectUrl);
    }

    private String redirect(RequestType type,
                            Long id,
                            String redirectUrl) {

        if (redirectUrl == null || redirectUrl.isBlank()) {
            return "redirect:/admin/requests/" + type + "/" + id;
        }

        return "redirect:" + redirectUrl;
    }
}