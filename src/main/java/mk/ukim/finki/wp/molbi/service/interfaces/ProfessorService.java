package mk.ukim.finki.wp.molbi.service.interfaces;

import mk.ukim.finki.wp.molbi.model.base.Professor;
import mk.ukim.finki.wp.molbi.model.enums.ProfessorTitle;
import mk.ukim.finki.wp.molbi.model.exceptions.ProfessorNotFoundException;

import java.util.List;

public interface ProfessorService {


    List<Professor> getAllProfessors();

    Professor getProfessorById(String id) throws ProfessorNotFoundException;

    List<Professor> findProfessorsByTitle(ProfessorTitle title);

    List<Professor> findAll();
}
