package mk.ukim.finki.wp.molbi.repository;

import mk.ukim.finki.wp.molbi.model.requests.StudentRequest;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface StudentRequestRepository<T extends StudentRequest>
        extends JpaSpecificationRepository<T, Long> {
    boolean existsByRequestSession_Id(Long sessionId);
}