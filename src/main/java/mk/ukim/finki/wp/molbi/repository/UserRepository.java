package mk.ukim.finki.wp.molbi.repository;

import mk.ukim.finki.wp.molbi.model.base.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaSpecificationRepository<User, String> {

}
