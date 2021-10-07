package TeamCamp.demo.controller;

import TeamCamp.demo.common.annotation.CurrentUser;
import TeamCamp.demo.dto.PointDto;
import TeamCamp.demo.dto.PointDto.ChargeRequest;
import TeamCamp.demo.dto.PointDto.WithdrawalRequest;
import TeamCamp.demo.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("point")
public class PointApiController {

    private final PointService pointService;

    @GetMapping
    public Long getPoint(@CurrentUser String email){
        return pointService.getUserPoint(email);
    }

    @PostMapping("/payment")
    public void payment(@CurrentUser String email, @RequestBody ChargeRequest request){
        pointService.charging(email,request);
    }

    @PostMapping("/withdrawal")
    public void withdrawal(@CurrentUser String email, @RequestBody WithdrawalRequest request){
        pointService.withdrawal(email,request);
    }

    @GetMapping("/deduction-details")
    @ResponseBody
    public List<PointDto.PointHistoryDto> deductionHistory(@CurrentUser String email){
        return pointService.getDeductionHistory(email);
    }

    @GetMapping("/charging-details")
    @ResponseBody
    public List<PointDto.PointHistoryDto> chargingHistory(@CurrentUser String email){
        return pointService.getChargingHistory(email);
    }
}
