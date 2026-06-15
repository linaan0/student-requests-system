package mk.ukim.finki.wp.molbi.service.implementations;

import lombok.AllArgsConstructor;
import mk.ukim.finki.wp.molbi.model.base.StudyProgram;
import mk.ukim.finki.wp.molbi.repository.StudyProgramRepository;
import mk.ukim.finki.wp.molbi.service.interfaces.StudyProgramService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class StudyProgramServiceImpl implements StudyProgramService {
    private final StudyProgramRepository repository;


    @Override
    public List<StudyProgram> findAll() {
        return repository.findAll();
    }
}
