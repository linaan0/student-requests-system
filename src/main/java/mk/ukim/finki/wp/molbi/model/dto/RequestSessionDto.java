package mk.ukim.finki.wp.molbi.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mk.ukim.finki.wp.molbi.model.enums.RequestType;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestSessionDto {

    @NotNull(message = "*Мора да изберете датум и време за почеток")
    private LocalDateTime timeFrom;

    @NotNull(message = "*Мора да изберете датум и време за крај")
    private LocalDateTime timeTo;

    @NotNull
    private String semesterId;

    @NotNull
    private RequestType requestType;

    @Size(max = 5000)
    private String description;

    @Size(max = 5000)
    private String approvalNote;
}
