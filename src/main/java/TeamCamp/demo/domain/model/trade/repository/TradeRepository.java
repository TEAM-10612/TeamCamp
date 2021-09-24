package TeamCamp.demo.domain.model.trade.repository;

import TeamCamp.demo.domain.model.trade.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<Trade,Long> {
}
