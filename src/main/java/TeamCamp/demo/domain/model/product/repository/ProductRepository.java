package TeamCamp.demo.domain.model.product.repository;

import TeamCamp.demo.domain.model.product.repository.SearchProductRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import TeamCamp.demo.domain.model.product.Product;

import java.util.Optional;


@Repository
public interface ProductRepository extends JpaRepository<Product,Long> , SearchProductRepository {

    @Override
    @EntityGraph(attributePaths = {"trades","user"})
    Optional<Product> findById(Long id);
}
