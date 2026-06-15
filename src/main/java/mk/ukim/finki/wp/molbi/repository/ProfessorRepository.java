package mk.ukim.finki.wp.molbi.repository;

import mk.ukim.finki.wp.molbi.model.base.Professor;
import mk.ukim.finki.wp.molbi.model.enums.ProfessorTitle;

import java.util.List;

public interface ProfessorRepository extends JpaSpecificationRepository<Professor, String> {
    List<Professor> findByTitle(ProfessorTitle title);
}
