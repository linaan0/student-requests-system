package mk.ukim.finki.wp.molbi.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import mk.ukim.finki.wp.molbi.model.base.*;
import mk.ukim.finki.wp.molbi.model.requests.*;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Getter
public enum RequestType {

    GENERAL("Општа молба",
            GeneralStudentRequest.class,
            AppRole.ADMIN),

    STUDY_PROGRAM_CHANGE("Молба за промена на студиска програма",
            ChangeStudyProgramStudentRequest.class,
            AppRole.ADMIN),

    COURSE_GROUP_CHANGE("Молба за промена на професор",
            CourseGroupChangeStudentRequest.class,
            AppRole.ADMIN),

    LATE_COURSE_ENROLLMENT("Молба за задоцнето запишување на предмет",
            LateCourseEnrollmentStudentRequest.class,
            AppRole.ADMIN),

    COURSE_ENROLLMENT_WITHOUT_REQUIREMENTS("Молба за запишување на предмет без исполнет предуслов",
            CourseEnrollmentWithoutRequirementsStudentRequest.class,
            AppRole.ADMIN),

    PAYMENT_DISCOUNT_SINGLE_PARENT("Молба за намалена партиципација за еднородителски семејства",
            GeneralStudentRequest.class,
            AppRole.FINANCE_ADMIN),

    INSTALLMENT_PAYMENT("Молба за плаќање на рати",
            InstallmentPaymentStudentRequest.class,
            AppRole.FINANCE_ADMIN);

    private final String displayName;
    private final Class<? extends StudentRequest> studentRequestClass;
    private final AppRole managedBy;

    public static List<RequestType> forRole(AppRole role) {
        return Arrays.stream(values())
                .filter(t -> t.managedBy == role)
                .toList();
    }
}