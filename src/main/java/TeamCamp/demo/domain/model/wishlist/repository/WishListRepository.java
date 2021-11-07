package TeamCamp.demo.domain.model.wishlist.repository;

import TeamCamp.demo.domain.model.wishlist.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;


public interface WishListRepository extends JpaRepository<Wishlist,Long> {
}
