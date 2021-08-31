package TeamCamp.demo.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import TeamCamp.demo.domain.model.users.admin.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin,Long> {

}
