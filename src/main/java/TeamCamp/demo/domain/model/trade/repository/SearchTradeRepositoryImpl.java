package TeamCamp.demo.domain.model.trade.repository;

import TeamCamp.demo.domain.model.trade.QTrade;
import TeamCamp.demo.domain.model.trade.TradeStatus;
import TeamCamp.demo.domain.model.users.User;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static TeamCamp.demo.domain.model.trade.QTrade.trade;

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
}
