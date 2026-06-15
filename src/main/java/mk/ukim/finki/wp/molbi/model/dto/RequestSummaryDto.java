package mk.ukim.finki.wp.molbi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import mk.ukim.finki.wp.molbi.model.enums.RequestType;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class RequestSummaryDto {
    private Long id;
    private RequestType type;
    private String description;
    private LocalDate dateCreated;
    private Boolean isApproved;
    private Boolean isProcessed;
    private String detailsUrl;
}