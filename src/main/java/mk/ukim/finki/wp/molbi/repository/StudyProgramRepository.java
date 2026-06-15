package mk.ukim.finki.wp.molbi.repository;

import mk.ukim.finki.wp.molbi.model.base.Student;
import mk.ukim.finki.wp.molbi.model.base.StudyProgram;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyProgramRepository extends JpaSpecificationRepository<StudyProgram, String> {
}