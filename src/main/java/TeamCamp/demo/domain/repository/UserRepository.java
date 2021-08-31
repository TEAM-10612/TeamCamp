package TeamCamp.demo.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import TeamCamp.demo.domain.model.users.user.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> ,UserRepositoryCustom{
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean existsByEmailAndPassword(String email, String password);
    Optional<User>findByEmail(String email);
    void deleteByEmail(String email);
}
