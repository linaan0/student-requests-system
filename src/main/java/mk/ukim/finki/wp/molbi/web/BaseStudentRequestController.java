package mk.ukim.finki.wp.molbi.web;

import mk.ukim.finki.wp.molbi.config.FacultyUserDetails;
import mk.ukim.finki.wp.molbi.model.base.Student;
import mk.ukim.finki.wp.molbi.model.requests.StudentRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

public abstract class BaseStudentRequestController {

    protected Student getCurrentStudent(
            @AuthenticationPrincipal FacultyUserDetails userDetails) {
        Student student = userDetails.getStudent();
        if (student == null)
            throw new IllegalStateException("No student associated with current user");
        return student;
    }


    protected void validateOwnership(StudentRequest request,
                                     FacultyUserDetails userDetails) {
        if (!request.getStudent().getIndex().equals(userDetails.getUsername()))
            throw new IllegalStateException("Access denied");
    }
}
