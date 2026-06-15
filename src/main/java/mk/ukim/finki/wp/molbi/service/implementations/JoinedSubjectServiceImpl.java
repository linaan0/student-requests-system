package mk.ukim.finki.wp.molbi.service.implementations;

import lombok.AllArgsConstructor;
import mk.ukim.finki.wp.molbi.model.base.JoinedSubject;
import mk.ukim.finki.wp.molbi.model.base.Student;
import mk.ukim.finki.wp.molbi.repository.JoinedSubjectRepository;
import mk.ukim.finki.wp.molbi.service.interfaces.JoinedSubjectService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class JoinedSubjectServiceImpl implements JoinedSubjectService {
    private final JoinedSubjectRepository repository;


    @Override
    public List<JoinedSubject> findAll() {
        return repository.findAll();
    }

    @Override
    public List<JoinedSubject> findSubjectsWithUnmetRequirements(Student student) {
        //return List.of();
        return repository.findAll();
    }
}
