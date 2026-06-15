package mk.ukim.finki.wp.molbi.model.requests;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mk.ukim.finki.wp.molbi.model.base.JoinedSubject;
import mk.ukim.finki.wp.molbi.model.base.Professor;


@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class LateCourseEnrollmentStudentRequest extends StudentRequest {

    @ManyToOne
    private JoinedSubject joinedSubject;

    @ManyToOne
    private Professor professor;

    private Boolean professorApproved;

    @Override
    public boolean canBeApproved() {
        //return isApproved == null && Boolean.TRUE.equals(professorApproved);
        return professorApproved!=null;
    }

    @Override
    public boolean canBeRejected() {
        return isApproved == null && Boolean.TRUE.equals(professorApproved);
    }

    public boolean canBeApprovedByProfessor() {
        return professorApproved == null;
    }

    public boolean canBeRejectedByProfessor() {
        return professorApproved == null|| professorApproved;
    }
}
