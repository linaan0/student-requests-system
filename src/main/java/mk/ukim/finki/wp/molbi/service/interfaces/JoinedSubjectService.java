package mk.ukim.finki.wp.molbi.service.interfaces;

import mk.ukim.finki.wp.molbi.model.base.JoinedSubject;
import mk.ukim.finki.wp.molbi.model.base.Student;

import java.util.List;

public interface JoinedSubjectService {
    List<JoinedSubject> findAll();
    List<JoinedSubject> findSubjectsWithUnmetRequirements(Student student);
}
