package mk.ukim.finki.wp.molbi.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeStudyProgramForm {

    @NotNull
    private Long sessionId;

    @NotNull
    private String newProgramId;

    private String oldProgramId;

    private String description;
}