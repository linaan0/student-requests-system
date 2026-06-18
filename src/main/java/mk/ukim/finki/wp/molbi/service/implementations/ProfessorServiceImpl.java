package mk.ukim.finki.wp.molbi.service.implementations;

import lombok.AllArgsConstructor;
import mk.ukim.finki.wp.molbi.model.base.Professor;
import mk.ukim.finki.wp.molbi.model.enums.ProfessorTitle;
import mk.ukim.finki.wp.molbi.model.exceptions.ProfessorNotFoundException;
import mk.ukim.finki.wp.molbi.repository.ProfessorRepository;
import mk.ukim.finki.wp.molbi.service.interfaces.ProfessorService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProfessorServiceImpl implements ProfessorService {
    private final ProfessorRepository professorRepository;

    @Override
    public Professor getProfessorById(String id) throws ProfessorNotFoundException {
        return professorRepository.findById(id)
                .orElseThrow(() -> new ProfessorNotFoundException("Professor with id " + id + " doesn't exist"));
    }

    @Override
    public List<Professor> findAll() {
        return professorRepository.findAllByTitleNot(ProfessorTitle.RETIRED);
    }
}