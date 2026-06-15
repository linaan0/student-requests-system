package mk.ukim.finki.wp.molbi.service.implementations;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.wp.molbi.model.enums.RequestType;
import mk.ukim.finki.wp.molbi.model.requests.StudentRequest;
import mk.ukim.finki.wp.molbi.service.interfaces.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminStudentRequestServiceImpl implements AdminStudentRequestService {

    private final GeneralStudentRequestService generalService;
    private final InstallmentPaymentStudentRequestService installmentService;
    private final ChangeStudyProgramStudentRequestService changeStudyProgramService;
    private final CourseGroupChangeStudentRequestService courseGroupChangeService;
    private final LateCourseEnrollmentStudentRequestService lateCourseEnrollmentService;
    private final CourseEnrollmentWithoutRequirementsStudentRequestService courseEnrollmentWithoutRequirementsService;

    @Override
    public Page<? extends StudentRequest> findAll(int page, int size,
                                                  RequestType type,
                                                  Boolean isApproved,
                                                  Boolean isProcessed,
                                                  Long sessionId,
                                                  List<RequestType> allowedTypes) {
        if (type != null) {
            return switch (type) {
                case GENERAL, PAYMENT_DISCOUNT_SINGLE_PARENT -> generalService.findAll(page, size, isApproved, isProcessed, sessionId);
                case INSTALLMENT_PAYMENT -> installmentService.findAll(page, size, isApproved, isProcessed, sessionId);
                case STUDY_PROGRAM_CHANGE -> changeStudyProgramService.findAll(page, size, isApproved, isProcessed, sessionId);
                case COURSE_GROUP_CHANGE -> courseGroupChangeService.findAll(page, size, isApproved, isProcessed, sessionId);
                case LATE_COURSE_ENROLLMENT -> lateCourseEnrollmentService.findAll(page, size, isApproved, isProcessed, sessionId);
                case COURSE_ENROLLMENT_WITHOUT_REQUIREMENTS -> courseEnrollmentWithoutRequirementsService.findAll(page, size, isApproved, isProcessed, sessionId);
            };
        }

        return findAllAcrossTypes(page, size, isApproved, isProcessed, sessionId, allowedTypes);
    }

    private Page<? extends StudentRequest> findAllAcrossTypes(int page, int size,
                                                              Boolean isApproved,
                                                              Boolean isProcessed,
                                                              Long sessionId,
                                                              List<RequestType> allowedTypes) {
        List<StudentRequest> all = new ArrayList<>();

        for (RequestType type : allowedTypes) {
            List<? extends StudentRequest> forType = switch (type) {
                case GENERAL, PAYMENT_DISCOUNT_SINGLE_PARENT ->
                        generalService.findAll(0, Integer.MAX_VALUE, isApproved, isProcessed, sessionId).getContent();
                case INSTALLMENT_PAYMENT ->
                        installmentService.findAll(0, Integer.MAX_VALUE, isApproved, isProcessed, sessionId).getContent();
                case STUDY_PROGRAM_CHANGE ->
                        changeStudyProgramService.findAll(0, Integer.MAX_VALUE, isApproved, isProcessed, sessionId).getContent();
                case COURSE_GROUP_CHANGE ->
                        courseGroupChangeService.findAll(0, Integer.MAX_VALUE, isApproved, isProcessed, sessionId).getContent();
                case LATE_COURSE_ENROLLMENT ->
                        lateCourseEnrollmentService.findAll(0, Integer.MAX_VALUE, isApproved, isProcessed, sessionId).getContent();
                case COURSE_ENROLLMENT_WITHOUT_REQUIREMENTS ->
                        courseEnrollmentWithoutRequirementsService.findAll(0, Integer.MAX_VALUE, isApproved, isProcessed, sessionId).getContent();
            };
            all.addAll(forType);
        }

        all.sort(Comparator.comparing(StudentRequest::getDateCreated,
                Comparator.nullsLast(Comparator.reverseOrder())));

        int start = Math.min(page * size, all.size());
        int end = Math.min(start + size, all.size());

        return new PageImpl<>(all.subList(start, end), PageRequest.of(page, size), all.size());
    }

    @Override
    public StudentRequest findById(Long id, RequestType type) {
        return switch (type) {
            case GENERAL, PAYMENT_DISCOUNT_SINGLE_PARENT -> generalService.findById(id);
            case INSTALLMENT_PAYMENT -> installmentService.findById(id);
            case STUDY_PROGRAM_CHANGE -> changeStudyProgramService.findById(id);
            case COURSE_GROUP_CHANGE -> courseGroupChangeService.findById(id);
            case LATE_COURSE_ENROLLMENT -> lateCourseEnrollmentService.findById(id);
            case COURSE_ENROLLMENT_WITHOUT_REQUIREMENTS -> courseEnrollmentWithoutRequirementsService.findById(id);
        };
    }

    @Override
    public StudentRequest approve(Long id, RequestType type) {
        return switch (type) {
            case GENERAL, PAYMENT_DISCOUNT_SINGLE_PARENT -> generalService.approve(id);
            case INSTALLMENT_PAYMENT -> installmentService.approve(id);
            case STUDY_PROGRAM_CHANGE -> changeStudyProgramService.approve(id);
            case COURSE_GROUP_CHANGE -> courseGroupChangeService.approve(id);
            case LATE_COURSE_ENROLLMENT -> lateCourseEnrollmentService.approve(id);
            case COURSE_ENROLLMENT_WITHOUT_REQUIREMENTS -> courseEnrollmentWithoutRequirementsService.approve(id);
        };
    }

    @Override
    public StudentRequest reject(Long id, RequestType type, String reason) {
        return switch (type) {
            case GENERAL, PAYMENT_DISCOUNT_SINGLE_PARENT -> generalService.reject(id, reason);
            case INSTALLMENT_PAYMENT -> installmentService.reject(id, reason);
            case STUDY_PROGRAM_CHANGE -> changeStudyProgramService.reject(id, reason);
            case COURSE_GROUP_CHANGE -> courseGroupChangeService.reject(id, reason);
            case LATE_COURSE_ENROLLMENT -> lateCourseEnrollmentService.reject(id, reason);
            case COURSE_ENROLLMENT_WITHOUT_REQUIREMENTS ->
                    courseEnrollmentWithoutRequirementsService.reject(id, reason);
        };
    }

    @Override
    public StudentRequest markAsProcessed(Long id, RequestType type) {
        return switch (type) {
            case GENERAL, PAYMENT_DISCOUNT_SINGLE_PARENT -> generalService.markAsProcessed(id);
            case INSTALLMENT_PAYMENT -> installmentService.markAsProcessed(id);
            case STUDY_PROGRAM_CHANGE -> changeStudyProgramService.markAsProcessed(id);
            case COURSE_GROUP_CHANGE -> courseGroupChangeService.markAsProcessed(id);
            case LATE_COURSE_ENROLLMENT -> lateCourseEnrollmentService.markAsProcessed(id);
            case COURSE_ENROLLMENT_WITHOUT_REQUIREMENTS ->
                    courseEnrollmentWithoutRequirementsService.markAsProcessed(id);
        };
    }
}
