package TeamCamp.demo.domain.repository;

import TeamCamp.demo.domain.model.users.user.address.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address,Long> {
}
