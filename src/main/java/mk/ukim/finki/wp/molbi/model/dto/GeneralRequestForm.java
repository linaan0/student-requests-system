package mk.ukim.finki.wp.molbi.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeneralRequestForm {

    @NotNull
    private Long sessionId;

    @NotBlank
    private String description;
}