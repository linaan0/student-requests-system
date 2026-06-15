package mk.ukim.finki.wp.molbi.model.base;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mk.ukim.finki.wp.molbi.model.enums.SemesterType;
import mk.ukim.finki.wp.molbi.model.enums.StudyCycle;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class JoinedSubject extends TrackedHistoryEntity {

    @Id
    private String abbreviation;

    @Column(length = 1000)
    private String name;

    private String codes;

    @Enumerated(EnumType.STRING)
    private SemesterType semesterType;

    @ManyToOne
    private Subject mainSubject;

    private String mandatoryForStudyPrograms;

    private Integer weeklyLecturesClasses;

    private Integer weeklyAuditoriumClasses;

    private Integer weeklyLabClasses;

    @Enumerated(EnumType.STRING)
    private StudyCycle cycle;

    @Column(length = 4_000)
    private String validationMessage;
}
