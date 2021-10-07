package TeamCamp.demo.controller;

import TeamCamp.demo.common.annotation.CurrentUser;
import TeamCamp.demo.common.annotation.LoginCheck;
import TeamCamp.demo.dto.TradeDto;
import TeamCamp.demo.service.TradeService;
import TeamCamp.demo.domain.model.users.UserLevel;
import com.amazonaws.Request;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import org.springframework.web.bind.annotation.*;

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

    @LoginCheck(authority = UserLevel.AUTH)
    @PatchMapping
    public void updateTradeInfo(@RequestBody TradeDto.ChangeRequest request){
        tradeService.updateTrade(request);
    }

    @LoginCheck(authority = UserLevel.AUTH)
    @DeleteMapping
    public void deleteTrade(@RequestBody TradeDto.ChangeRequest request){
        tradeService.deleteTrade(request);
    }

    @LoginCheck(authority = UserLevel.AUTH)
    @PatchMapping("{id}/receiving-tracking-number")
    public void updateReceivingTrackingNumber(@PathVariable Long id, @CurrentUser String email
        ,@RequestBody String trackingNumber){
        tradeService.updateReceivingTrackingNumber(id,email,trackingNumber);
    }
}
