package TeamCamp.demo.domain.repository;

import TeamCamp.demo.domain.model.trade.repository.SearchTradeRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import TeamCamp.demo.domain.model.product.Product;


@Repository
public interface ProductRepository extends JpaRepository<Product,Long> , SearchTradeRepository {

}
