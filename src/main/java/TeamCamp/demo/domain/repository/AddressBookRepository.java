package TeamCamp.demo.domain.repository;

import TeamCamp.demo.domain.model.users.user.address.AddressBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressBookRepository extends JpaRepository<AddressBook,Long> {
}
