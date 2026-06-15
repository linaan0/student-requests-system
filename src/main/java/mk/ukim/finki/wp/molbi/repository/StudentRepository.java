package mk.ukim.finki.wp.molbi.repository;

import mk.ukim.finki.wp.molbi.model.base.Student;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaSpecificationRepository<Student, String> {
}
