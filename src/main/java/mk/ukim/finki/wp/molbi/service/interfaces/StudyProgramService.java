package mk.ukim.finki.wp.molbi.service.interfaces;

import mk.ukim.finki.wp.molbi.model.base.StudyProgram;
import java.util.List;

public interface StudyProgramService {
    List<StudyProgram> findAll();

    List<StudyProgram> findAll(String year);

    List<StudyProgram> findAll(String year, String studyCycle);
}