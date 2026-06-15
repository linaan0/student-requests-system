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
public class CourseGroupChangeStudentRequest extends StudentRequest {

    @ManyToOne
    private JoinedSubject joinedSubject;

    @ManyToOne
    private Professor currentProfessor;

    @ManyToOne
    private Professor newProfessor;
}
