package TeamCamp.demo.domain.model.wishlist.repository;

import TeamCamp.demo.domain.model.wishlist.ProductWishList;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductWishListRepository extends JpaRepository<ProductWishList ,Long> {
}
