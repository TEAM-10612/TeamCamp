package TeamCamp.demo.domain.model.trade.repository;


import TeamCamp.demo.domain.model.users.User;
import TeamCamp.demo.dto.TradeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchTradeRepository {
    //진행중인지 여부 체크
    boolean existProgressingByUser(User user);

    Page<TradeDto.TradeInfoResponse> searchByTradeStatusAndTradeId(TradeDto.TradeSearchCondition searchCondition,
                                                                   Pageable pageable);
}
