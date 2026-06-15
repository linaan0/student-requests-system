package mk.ukim.finki.wp.molbi.model.requests;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mk.ukim.finki.wp.molbi.model.base.StudyProgram;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class ChangeStudyProgramStudentRequest extends StudentRequest {

    @ManyToOne
    private StudyProgram newStudyProgram;

    @ManyToOne
    private StudyProgram oldStudyProgram;
}
