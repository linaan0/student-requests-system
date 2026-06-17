package mk.ukim.finki.wp.molbi.service.implementations;

import lombok.AllArgsConstructor;
import mk.ukim.finki.wp.molbi.model.base.Semester;
import mk.ukim.finki.wp.molbi.model.enums.SemesterState;
import mk.ukim.finki.wp.molbi.repository.SemesterRepository;
import mk.ukim.finki.wp.molbi.service.interfaces.SemesterService;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@AllArgsConstructor
public class SemesterServiceImpl implements SemesterService {
    private final SemesterRepository repository;

    @Override
    public List<Semester> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Semester> findActive() {
        List<SemesterState> activeStates = List.of(
                SemesterState.STARTED,
                SemesterState.SCHEDULE_PREPARATION,
                SemesterState.DATA_COLLECTION
        );

        return  repository.findByStateIn(activeStates);

    }
}
