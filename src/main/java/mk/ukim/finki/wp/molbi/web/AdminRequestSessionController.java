package mk.ukim.finki.wp.molbi.web;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.wp.molbi.config.FacultyUserDetails;
import mk.ukim.finki.wp.molbi.model.dto.RequestSessionDto;
import mk.ukim.finki.wp.molbi.model.enums.AppRole;
import mk.ukim.finki.wp.molbi.model.enums.RequestType;
import mk.ukim.finki.wp.molbi.model.requests.RequestSession;
import mk.ukim.finki.wp.molbi.service.interfaces.RequestSessionService;
import mk.ukim.finki.wp.molbi.service.interfaces.SemesterService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/sessions")
@RequiredArgsConstructor
public class AdminRequestSessionController {

    private final RequestSessionService requestSessionService;
    private final SemesterService semesterService;


    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(required = false) LocalDateTime from,
                       @RequestParam(required = false) LocalDateTime to,
                       @RequestParam(required = false) String semesterCode,
                       @RequestParam(required = false) RequestType type,
                       @AuthenticationPrincipal FacultyUserDetails userDetails, Model model) {
        AppRole role = userDetails.getUser().getRole().getApplicationRole();
        List<RequestType> allowedTypes = RequestType.forRole(role);
        Page<RequestSession> sessions = requestSessionService.filter(page, size, from, to, semesterCode, type);
        model.addAttribute("sessions", sessions);
        model.addAttribute("requestTypes", RequestType.values());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", sessions.getTotalPages());
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("semesters", semesterService.findAll());
        model.addAttribute("semesterCode", semesterCode);
        model.addAttribute("type", type);
        model.addAttribute("requestTypes", allowedTypes);
        return "admin/sessions/list";
    }

    @GetMapping("/new")
    public String newForm(@AuthenticationPrincipal FacultyUserDetails userDetails,
                          Model model) {
        AppRole role = userDetails.getUser().getRole().getApplicationRole();
        model.addAttribute("form", new RequestSessionDto());
        model.addAttribute("semesters", semesterService.findAll());
        model.addAttribute("requestTypes", RequestType.forRole(role));  // само неговите типови
        return "admin/sessions/form";
    }

    @PostMapping
    public String create(@ModelAttribute("form") @Valid RequestSessionDto form,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("form", form);  // експлицитно
            model.addAttribute("semesters", semesterService.findAll());
            model.addAttribute("requestTypes", RequestType.values());
            return "admin/sessions/form";
        }
        try {
            requestSessionService.create(form);
            return "redirect:/admin/sessions";
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("semesters", semesterService.findAll());
            model.addAttribute("requestTypes", RequestType.values());
            return "admin/sessions/form";
        }
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        RequestSession requestSession= requestSessionService.findById(id);
        model.addAttribute("s", requestSession);
        return "admin/sessions/details";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        RequestSession session = requestSessionService.findById(id);
        RequestSessionDto form = new RequestSessionDto(
                session.getTimeFrom(),
                session.getTimeTo(),
                session.getSemester().getCode(),
                session.getRequestType(),
                session.getDescription(),
                session.getApprovalNote()
        );
        model.addAttribute("form", form);
        model.addAttribute("sessionId", id);
        model.addAttribute("semesters", semesterService.findAll());
        model.addAttribute("requestTypes", RequestType.values());
        return "admin/sessions/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute("form") @Valid RequestSessionDto form,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("form", form);  // додај ова
            model.addAttribute("sessionId", id);
            model.addAttribute("semesters", semesterService.findAll());
            model.addAttribute("requestTypes", RequestType.values());
            return "admin/sessions/form";
        }
        try {
            requestSessionService.update(id, form);
            return "redirect:/admin/sessions";
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("sessionId", id);
            model.addAttribute("semesters", semesterService.findAll());
            model.addAttribute("requestTypes", RequestType.values());
            return "admin/sessions/form";
        }
    }

}