package TeamCamp.demo.controller;

import TeamCamp.demo.common.annotation.CurrentUser;
import TeamCamp.demo.common.annotation.LoginCheck;
import TeamCamp.demo.domain.model.trade.TradeDto;
import TeamCamp.demo.domain.model.trade.TradeService;
import TeamCamp.demo.domain.model.users.UserLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("trade")
public class TradeApiController {

    private final TradeService tradeService;

    @LoginCheck(authority = UserLevel.AUTH)
    @PostMapping("/buy")
    public void purchase(@CurrentUser String email, @RequestBody TradeDto.TradeRequest request){
        tradeService.purchase(email,request);
    }

    @LoginCheck(authority = UserLevel.AUTH)
    @PostMapping("/sell")
    public void sell(@CurrentUser String email, @RequestBody TradeDto.TradeRequest request){
        tradeService.sale(email, request);
    }
}
