package TeamCamp.demo.controller;

import TeamCamp.demo.common.annotation.CurrentUser;
import TeamCamp.demo.common.annotation.LoginCheck;
import TeamCamp.demo.domain.model.trade.repository.TradeRepository;
import TeamCamp.demo.dto.TradeDto;
import TeamCamp.demo.dto.TradeDto.TradeResource;
import TeamCamp.demo.service.TradeService;
import TeamCamp.demo.domain.model.users.UserLevel;
import com.amazonaws.Request;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Path;

@RequiredArgsConstructor
@RestController
@RequestMapping("trade")
public class TradeApiController {

    private final TradeService tradeService;
    private final TradeRepository tradeRepository;
    @LoginCheck(authority = UserLevel.AUTH)
    @GetMapping("/{productId}")
    public TradeResource returnTradeResource(@CurrentUser String email, @PathVariable Long productId){
        TradeResource resource = tradeService.getResource(email, productId);
        return  resource;
    }

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

    @LoginCheck(authority = UserLevel.ADMIN)
    @PatchMapping("{id}/purchase-confirmation")
    public void confirmPurchase(@PathVariable Long id,@CurrentUser String email){
        tradeService.confirmPurchase(id, email);
    }


    @LoginCheck(authority = UserLevel.ADMIN)
    @PatchMapping("/{id}/forwarding-tracking-number")
    public void updateForwardingTrackingNumber(@PathVariable Long id,
                                               @RequestBody TradeDto.TrackingNumberRequest requestDto) {
        tradeService.updateTrackingNumber(id, requestDto.getTrackingNumber());
    }
//
//    @Transactional(readOnly = true)
//    public Page<TradeDto.TradeInfoResponse> getTradeInfos(TradeDto.TradeSearchCondition tradeSearchCondition,
//                                                          Pageable pageable) {
//        return tradeRepository.(tradeSearchCondition, pageable);
//    }


}
