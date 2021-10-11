package TeamCamp.demo.domain.model.product.repository;

import TeamCamp.demo.domain.model.trade.OrderStandard;
import TeamCamp.demo.dto.ProductDto;
import TeamCamp.demo.dto.ProductDto.ThumbnailResponse;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static TeamCamp.demo.domain.model.product.QProduct.*;
import static TeamCamp.demo.domain.model.trade.QTrade.trade;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
public class SearchProductRepositoryImpl implements SearchProductRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<ThumbnailResponse> findAllBySearchCondition(ProductDto.SearchCondition condition, Pageable pageable) {
        QueryResults<ThumbnailResponse> results = jpaQueryFactory
                .select(Projections.fields(ThumbnailResponse.class,
                        product.id,
                        product.thumbnailImagePath.as("productThumbnailImagePath"),
                        product.name,
                        trade.price.min().as("lowerPrice")
                ))
                .from(product)
                .leftJoin(product.trades,trade)
                .groupBy(product)
                .where(
                        containsKeyword(condition.getKeyword()),
                        trade.buyer.isNull()
                ).orderBy(
                        getOrderSpecifier(condition.getOrderStandard())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<ThumbnailResponse> products = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(products,pageable,total);
    }
    private BooleanExpression eqProductId(Long productId) {
        return (productId != null) ? product.id.eq(productId) : null;
    }
    private BooleanExpression containsKeyword(String keyword) {
        return (hasText(keyword)) ? product.name.containsIgnoreCase(keyword)
                .or(product.name.containsIgnoreCase(keyword))
                : null;
    }

    private OrderSpecifier getOrderSpecifier(OrderStandard orderStandard) {
        OrderSpecifier orderSpecifier = null;

        if (orderStandard == null) {
            orderSpecifier = new OrderSpecifier(Order.DESC, product.createDate);
        } else {
            switch (orderStandard) {
                case LOW_PRICE:
                    orderSpecifier = new OrderSpecifier(Order.ASC, trade.price);
                    break;
                case HIGH_PRICE:
                    orderSpecifier = new OrderSpecifier(Order.DESC, trade.price);
                    break;
                case RELEASE_DATE:
                    orderSpecifier = new OrderSpecifier(Order.DESC, product.createDate);
                    break;
                default:
                    orderSpecifier = new OrderSpecifier(Order.DESC, product.createDate);
            }
        }

        return orderSpecifier;
    }
}
