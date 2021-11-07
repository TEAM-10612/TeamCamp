package TeamCamp.demo.domain.model.address.repository;

import TeamCamp.demo.domain.model.address.AddressBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressBookRepository extends JpaRepository<AddressBook,Long> {
}
