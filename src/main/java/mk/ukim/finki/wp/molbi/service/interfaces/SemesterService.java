package mk.ukim.finki.wp.molbi.service.interfaces;

import mk.ukim.finki.wp.molbi.model.base.Semester;

import java.util.List;

public interface SemesterService {
    List<Semester> findAll();

    List<Semester> findActive();

}
