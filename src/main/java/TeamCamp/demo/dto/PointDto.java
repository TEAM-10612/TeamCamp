package TeamCamp.demo.dto;

import TeamCamp.demo.domain.model.point.Point;
import TeamCamp.demo.domain.model.point.PointDivision;
import TeamCamp.demo.domain.model.users.User;
import TeamCamp.demo.encrypt.EncryptionService;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class PointDto {

    @Getter
    @NoArgsConstructor
    public static class ChargeRequest{
        private Long chargeAmount;

        @Builder
        public ChargeRequest(Long chargeAmount) {
            this.chargeAmount = chargeAmount;
        }

        public Point toEntity(User user){
            return Point.builder()
                    .amount(chargeAmount)
                    .user(user)
                    .division(PointDivision.CHARGE)
                    .build();
        }
    }
    @Getter
    @NoArgsConstructor
    public static class WithdrawalRequest{
        private Long withdrawalAmount;
        private String password;

        public void passwordEncryption(EncryptionService encryptionService){
            this.password = encryptionService.encrypt(password);
        }

        @Builder
        public WithdrawalRequest(Long withdrawalAmount, String password) {
            this.withdrawalAmount = withdrawalAmount;
            this.password = password;
        }

        public Point toEntity(User user){
            return Point.builder()
                    .amount(withdrawalAmount)
                    .user(user)
                    .division(PointDivision.WITHDRAW)
                    .build();
        }
    }
    @Getter
    @NoArgsConstructor
    public static class PointHistoryDto{
        private LocalDateTime time;
        private Long amount;
        private PointDivision division;

        @Builder
        public PointHistoryDto(LocalDateTime time, Long amount, PointDivision division) {
            this.time = time;
            this.amount = amount;
            this.division = division;
        }
    }
}
