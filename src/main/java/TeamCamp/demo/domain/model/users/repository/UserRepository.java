package TeamCamp.demo.domain.model.users.repository;

import TeamCamp.demo.domain.model.admin.repository.AdminRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import TeamCamp.demo.domain.model.users.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> , AdminRepository {
    @EntityGraph(attributePaths = {"addressBook"})
    Optional<User>findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean existsByEmailAndPassword(String email, String password);
    void deleteByEmail(String email);
}
