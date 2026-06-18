package mk.ukim.finki.wp.molbi.web;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.wp.molbi.config.FacultyUserDetails;
import mk.ukim.finki.wp.molbi.model.base.Semester;
import mk.ukim.finki.wp.molbi.model.base.Student;
import mk.ukim.finki.wp.molbi.model.dto.RequestSummaryDto;
import mk.ukim.finki.wp.molbi.model.enums.RequestType;
import mk.ukim.finki.wp.molbi.model.requests.*;
import mk.ukim.finki.wp.molbi.service.interfaces.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
@RequestMapping("/requests")
@RequiredArgsConstructor
public class StudentRequestController extends BaseStudentRequestController {

    private final GeneralStudentRequestService generalService;
    private final InstallmentPaymentStudentRequestService installmentService;
    private final ChangeStudyProgramStudentRequestService changeStudyProgramService;
    private final CourseGroupChangeStudentRequestService courseGroupChangeService;
    private final LateCourseEnrollmentStudentRequestService lateCourseEnrollmentService;
    private final CourseEnrollmentWithoutRequirementsStudentRequestService courseEnrollmentService;
    private final RequestSessionService requestSessionService;
    private final SemesterService semesterService;

    @GetMapping
    public String myRequests(@AuthenticationPrincipal FacultyUserDetails userDetails,
                             Model model) {
        Student student = getCurrentStudent(userDetails);
        List<RequestSummaryDto> all = new ArrayList<>();

        generalService.findByStudent(student).forEach(r ->
                all.add(new RequestSummaryDto(r.getId(), RequestType.GENERAL,
                        r.getDescription(), r.getDateCreated(),
                        r.getIsApproved(), r.getIsProcessed(),
                        "/requests/general/" + r.getId())));

        installmentService.findByStudent(student).forEach(r ->
                all.add(new RequestSummaryDto(r.getId(), RequestType.INSTALLMENT_PAYMENT,
                        r.getDescription(), r.getDateCreated(),
                        r.getIsApproved(), r.getIsProcessed(),
                        "/requests/installment/" + r.getId())));

        changeStudyProgramService.findByStudent(student).forEach(r ->
                all.add(new RequestSummaryDto(r.getId(), RequestType.STUDY_PROGRAM_CHANGE,
                        r.getDescription(), r.getDateCreated(),
                        r.getIsApproved(), r.getIsProcessed(),
                        "/requests/change-program/" + r.getId())));

        courseGroupChangeService.findByStudent(student).forEach(r ->
                all.add(new RequestSummaryDto(r.getId(), RequestType.COURSE_GROUP_CHANGE,
                        r.getDescription(), r.getDateCreated(),
                        r.getIsApproved(), r.getIsProcessed(),
                        "/requests/course-group-change/" + r.getId())));

        lateCourseEnrollmentService.findByStudent(student).forEach(r ->
                all.add(new RequestSummaryDto(r.getId(), RequestType.LATE_COURSE_ENROLLMENT,
                        r.getDescription(), r.getDateCreated(),
                        r.getIsApproved(), r.getIsProcessed(),
                        "/requests/late-enrollment/" + r.getId())));

        courseEnrollmentService.findByStudent(student).forEach(r ->
                all.add(new RequestSummaryDto(r.getId(), RequestType.COURSE_ENROLLMENT_WITHOUT_REQUIREMENTS,
                        r.getDescription(), r.getDateCreated(),
                        r.getIsApproved(), r.getIsProcessed(),
                        "/requests/enrollment-requirements/" + r.getId())));

        all.sort(Comparator.comparing(RequestSummaryDto::getDateCreated).reversed());

        model.addAttribute("requests", all);
        return "requests/list";
    }

    @GetMapping("/new")
    public String chooseType(@AuthenticationPrincipal FacultyUserDetails userDetails,Model model) {
        Student student = getCurrentStudent(userDetails);
        Semester semester=semesterService.findActive().get(0);
        Map<RequestType, RequestSession> activeSessions =requestSessionService.getActiveSessions(student, semester);
        model.addAttribute("activeSessions", activeSessions);
        return "requests/choose-type";
    }
}