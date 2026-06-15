package mk.ukim.finki.wp.molbi.repository;

import mk.ukim.finki.wp.molbi.model.requests.RequestSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestSessionRepository extends JpaSpecificationRepository<RequestSession, Long> {
}