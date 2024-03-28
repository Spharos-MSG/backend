package spharos.msg.domain.users.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spharos.msg.domain.users.entity.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByUuid(String uuid);

    Optional<Users> findByLoginId(String loginId);

    Optional<Users> findByEmail(String email);

    Boolean existsByEmail(String email);

    Boolean existsByLoginId(String loginId);
}
