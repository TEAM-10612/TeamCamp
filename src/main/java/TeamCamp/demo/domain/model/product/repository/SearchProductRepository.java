package TeamCamp.demo.domain.model.product.repository;

import TeamCamp.demo.domain.model.users.User;
import TeamCamp.demo.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SearchProductRepository {

    Page<ProductDto.ThumbnailResponse> findAllBySearchCondition(ProductDto.SearchCondition condition,
                                                                Pageable pageable);
}
