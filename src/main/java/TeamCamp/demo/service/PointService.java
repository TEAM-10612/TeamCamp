package TeamCamp.demo.service;

import TeamCamp.demo.domain.model.users.User;
import TeamCamp.demo.domain.repository.PointRepository;
import TeamCamp.demo.domain.repository.UserRepository;
import TeamCamp.demo.dto.PointDto;
import TeamCamp.demo.dto.TradeDto;
import TeamCamp.demo.encrypt.EncryptionService;
import TeamCamp.demo.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserRepository userRepository;
    private final PointRepository pointRepository;
    private final EncryptionService encryptionService;

    @Transactional
    public void charging(String email, PointDto.ChargeRequest request){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        pointRepository.save(request.toEntity(user));
        user.chargingPoint(request.getChargeAmount());
    }

    @Transactional
    public void withdrawal(String email, PointDto.WithdrawalRequest request){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        request.passwordEncryption(encryptionService);

        isMatchPassword(email,request);

        pointRepository.save(request.toEntity(user));

        user.deductionOfPoints(request.getWithdrawalAmount());
    }

    private void isMatchPassword(String email, PointDto.WithdrawalRequest request) {
        if(!userRepository.existsByEmailAndPassword(email, request.getPassword())){
            throw new UserNotFoundException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }
    }

    public List<PointDto.PointHistoryDto>getDeductionHistory(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        return user.getDeductionHistory();
    }
    public List<PointDto.PointHistoryDto>getChargingHistory(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        return user.getChargingHistory();
    }

    public Long getUserPoint(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        return user.getPoint();
    }
}
