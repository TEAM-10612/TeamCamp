package TeamCamp.demo.domain.model.trade;

import TeamCamp.demo.domain.model.product.QProduct;
import TeamCamp.demo.domain.model.users.User;
import TeamCamp.demo.dto.ProductDto;
import TeamCamp.demo.dto.ProductDto.ThumbnailResponse;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
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
public class SearchTradeRepositoryImpl implements SearchTradeRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<ThumbnailResponse> findAllBySearchCondition(ProductDto.SearchCondition condition, Pageable pageable) {
        QueryResults<ThumbnailResponse> results = jpaQueryFactory
                .select(Projections.fields(ThumbnailResponse.class,
                        product.id,
                        product.thumbnailImagePath.as("productThumbnailImagePath"),
                        product.name)) //trade 구현 완료후 최저가 추가하기 
                .from(product)
                .where(
                        eqProductId(condition.getProductId()),
                        containKeyword(condition.getKeyword())
                ).orderBy(
                        getOrderSpecifier(condition.getOrderStandard())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<ThumbnailResponse>products = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(products,pageable,total);
    }

    private OrderSpecifier getOrderSpecifier(OrderStandard orderStandard) {
        OrderSpecifier orderSpecifier =  null;

        if(orderStandard == null ){
            orderSpecifier = new OrderSpecifier(Order.DESC, product.createDate);
        }else{
            switch (orderStandard){
                case LOW_PRICE:
                    orderSpecifier = new OrderSpecifier(Order.ASC, product.createDate);
                    break;
                case HIGH_PRICE:
                    orderSpecifier = new OrderSpecifier(Order.DESC, product.createDate);
                    break;
                case RELEASE_DATE:
                    orderSpecifier = new OrderSpecifier(Order.DESC, product.createDate);
                    break;
                default:
                    orderSpecifier = new OrderSpecifier(Order.DESC, product.createDate);
            }
        }

        return  orderSpecifier;
    }

    private BooleanExpression containKeyword(String keyword) {
        return (hasText(keyword)) ? product.name.containsIgnoreCase(keyword) : null;
    }

    private BooleanExpression eqProductId(Long productId) {
        return (productId != null) ? product.id.eq(productId):null;
    }
}
