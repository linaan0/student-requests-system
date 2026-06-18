package mk.ukim.finki.wp.molbi.service.interfaces;

import mk.ukim.finki.wp.molbi.model.base.Professor;
import mk.ukim.finki.wp.molbi.model.exceptions.ProfessorNotFoundException;

import java.util.List;

public interface ProfessorService {

    Professor getProfessorById(String id) throws ProfessorNotFoundException;

    List<Professor> findAll();
}
