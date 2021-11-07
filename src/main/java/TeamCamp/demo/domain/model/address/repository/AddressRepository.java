package TeamCamp.demo.domain.model.address.repository;

import TeamCamp.demo.domain.model.address.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address,Long> {
}
