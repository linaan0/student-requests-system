package mk.ukim.finki.wp.molbi.repository;

import mk.ukim.finki.wp.molbi.model.base.Semester;
import mk.ukim.finki.wp.molbi.model.base.Student;
import mk.ukim.finki.wp.molbi.model.enums.SemesterState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SemesterRepository extends JpaRepository<Semester, String> {
    List<Semester> findByStateIn(List<SemesterState> states);
}
