package mk.ukim.finki.wp.molbi.repository;

import mk.ukim.finki.wp.molbi.model.base.StudyProgram;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyProgramRepository extends JpaSpecificationRepository<StudyProgram, String> {
    List<StudyProgram> findAllByAccreditationYear(String year);

    List<StudyProgram> findAllByAccreditationYearAndStudyCycle(String year, String studyCycle);
}