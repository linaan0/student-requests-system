package mk.ukim.finki.wp.molbi.event;

import lombok.Getter;
import mk.ukim.finki.wp.molbi.model.requests.StudentRequest;

@Getter
public class RequestStatusChangedEvent {
    private final StudentRequest request;

    public RequestStatusChangedEvent(StudentRequest request) {
        this.request = request;
    }
}