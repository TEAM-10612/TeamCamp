package TeamCamp.demo.domain.model.trade.repository;


import TeamCamp.demo.domain.model.users.User;

public interface SearchTradeRepository {
    //진행중인지 여부 체크
    boolean existProgressingByUser(User user);

}
