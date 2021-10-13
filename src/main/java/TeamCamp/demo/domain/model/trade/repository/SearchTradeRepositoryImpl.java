package TeamCamp.demo.domain.model.trade.repository;

import TeamCamp.demo.domain.model.trade.QTrade;
import TeamCamp.demo.domain.model.trade.TradeStatus;
import TeamCamp.demo.domain.model.users.User;
import TeamCamp.demo.dto.TradeDto;
import TeamCamp.demo.dto.TradeDto.TradeSearchCondition;
import com.querydsl.core.Query;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.ml.neuralnet.twod.util.QuantizationError;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static TeamCamp.demo.domain.model.trade.QTrade.trade;
import static TeamCamp.demo.dto.TradeDto.*;

@RequiredArgsConstructor
public class SearchTradeRepositoryImpl implements SearchTradeRepository{

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public boolean existProgressingByUser(User user) {
        Integer result = jpaQueryFactory
                .selectOne()
                .from(trade)
                .where(isUserTrade(user),isProgressing())
                .fetchFirst();
        return result != null;
    }

    private BooleanExpression isUserTrade(User user) {
        return trade.buyer.eq(user)
                .or(trade.seller.eq(user));
    }

    private BooleanExpression isProgressing() {
        return trade.tradeStatus.ne(TradeStatus.CANCEL)
                .and(trade.tradeStatus.ne(TradeStatus.END));
    }

    @Override
    public Page<TradeInfoResponse> searchByTradeStatusAndTradeId(
            TradeSearchCondition searchRequest, Pageable pageable) {
        if(searchRequest.isSearchByBuyer()){
            return searchByBuyerEmail(searchRequest.getBuyerEmail(),pageable);
        }
        if(searchRequest.isSearchBySeller()){
            return searchBySellerEmail(searchRequest.getSellerEmail(),pageable);
        }
        return searchByTradeId(searchRequest.getTradeId(),pageable);
    }

    private Page<TradeInfoResponse> searchByTradeId(Long tradeId, Pageable pageable) {
        QueryResults<TradeInfoResponse> results =  jpaQueryFactory
                .select(Projections.fields(TradeInfoResponse.class,
                        trade.id,
                        trade.tradeStatus))
                .from(trade)
                .where(
                        tradeIdEq(tradeId)
                ).offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<TradeInfoResponse> infoResponseList = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(infoResponseList,pageable,total);
    }

    private Page<TradeInfoResponse> searchBySellerEmail(String sellerEmail, Pageable pageable) {
        QueryResults<TradeInfoResponse> results = jpaQueryFactory
                .select(Projections.fields(TradeInfoResponse.class,
                        trade.id,
                        trade.tradeStatus))
                .from(trade)
                .innerJoin(trade.seller)
                .where(
                        tradeSellerEq(sellerEmail)
                ).offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<TradeInfoResponse> infoResponseList = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(infoResponseList,pageable,total);
    }

    private Page<TradeInfoResponse> searchByBuyerEmail(String buyerEmail, Pageable pageable) {
        QueryResults<TradeInfoResponse> results = jpaQueryFactory
                .select(Projections.fields(TradeInfoResponse.class,
                        trade.id,
                        trade.tradeStatus))
                .from(trade)
                .innerJoin(trade.seller)
                .where(
                        tradeBuyerEq(buyerEmail)
                ).offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<TradeInfoResponse> infoResponseList = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(infoResponseList,pageable,total);
    }

    private BooleanExpression tradeIdEq(Long tradeId) {
        return tradeId != null ? trade.id.eq(tradeId) : null;
    }

    private BooleanExpression tradeBuyerEq(String email) {
        return email != null ? trade.buyer.email.eq(email):null;
    }
    private BooleanExpression tradeSellerEq(String email) {
        return email != null ? trade.seller.email.eq(email):null;
    }
}
