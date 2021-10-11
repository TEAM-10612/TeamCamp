package TeamCamp.demo.service;

import TeamCamp.demo.domain.model.point.Point;
import TeamCamp.demo.domain.model.point.PointDivision;
import TeamCamp.demo.domain.model.users.User;
import TeamCamp.demo.domain.model.users.UserLevel;
import TeamCamp.demo.domain.model.users.UserStatus;
import TeamCamp.demo.domain.model.users.user.address.AddressBook;
import TeamCamp.demo.domain.repository.PointRepository;
import TeamCamp.demo.domain.repository.UserRepository;
import TeamCamp.demo.dto.PointDto;
import TeamCamp.demo.dto.PointDto.PointHistoryDto;
import TeamCamp.demo.encrypt.EncryptionService;
import TeamCamp.demo.exception.user.UserNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PointRepository pointRepository;

    @Mock
    EncryptionService encryptionService;

    @InjectMocks
    private PointService pointService;

    public User user0() {
        return User.builder()
                .id(1L)
                .email("rddd@naver.com")
                .password("11111111")
                .nicknameModifiedDate(LocalDateTime.now())
                .nickname("ryu")
                .phone("01011111111")
                .userLevel(UserLevel.UNAUTH)
                .userStatus(UserStatus.NORMAL)
                .addressBooks(new AddressBook())
                .point(50000L)
                .pointBreakdown(createPointBreakDownMockData())
                .build();
    }

    private List<Point> createPointBreakDownMockData() {
        User tempUser = User.builder()
                .id(1L)
                .email("rddd@naver.com")
                .password("11111111")
                .nicknameModifiedDate(LocalDateTime.now())
                .nickname("ryu")
                .phone("01011111111")
                .userLevel(UserLevel.UNAUTH)
                .userStatus(UserStatus.NORMAL)
                .addressBooks(new AddressBook())
                .point(50000L)
                .build();
        List<Point> pointBreakDownMockDataList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Point point = Point.builder()
                    .amount(100000L)
                    .division(PointDivision.CHARGE)
                    .user(tempUser)
                    .build();

            pointBreakDownMockDataList.add(point);
        }

        for (int i = 0; i < 5; i++) {
            Point point = Point.builder()
                    .amount(100000L)
                    .division(PointDivision.SALES_REVENUE)
                    .user(tempUser)
                    .build();

            pointBreakDownMockDataList.add(point);
        }

        for (int i = 0; i < 3; i++) {
            Point point = Point.builder()
                    .amount(100000L)
                    .division(PointDivision.WITHDRAW)
                    .user(tempUser)
                    .build();

            pointBreakDownMockDataList.add(point);
        }

        for (int i = 0; i < 3; i++) {
            Point point = Point.builder()
                    .amount(100000L)
                    .division(PointDivision.PURCHASE_DEDUCTION)
                    .user(tempUser)
                    .build();

            pointBreakDownMockDataList.add(point);
        }

        return pointBreakDownMockDataList;
    }

    @Test
    @DisplayName("포인트 충전")
    public void charging() {
        User user = user0();
        Long nowPoint = user.getPoint();
        String email = "rddd@naver.com";
        PointDto.ChargeRequest request = PointDto.ChargeRequest.builder()
                .chargeAmount(100000L)
                .build();
        System.out.println(user.getPassword());

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        pointService.charging(email, request);

        assertThat(user.getPoint()).isEqualTo(nowPoint + request.getChargeAmount());

    }

    @Test
    @DisplayName("포인트 출금  - 패스위드 불일치로 포인트 출금에 실패한다")
    void pointWithdrawal() throws Exception {
        //given
        User user = user0();
        String email = "rddd@naver.com";
        PointDto.WithdrawalRequest request = PointDto.WithdrawalRequest.builder()
                .withdrawalAmount(50000L)
                .password("11111111")
                .build();

        //when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndPassword(any(), any())).thenReturn(false);


        //then
        assertThrows(UserNotFoundException.class,
                () -> pointService.withdrawal(email, request));

    }

    @Test
    @DisplayName("포인트 출금 성공")
    void pointWithdrawal_success() throws Exception {
        //given
        User user = user0();
        Long nowPoint = user.getPoint();
        String email = "rddd@naver.com";

        PointDto.WithdrawalRequest request = PointDto.WithdrawalRequest.builder()
                .withdrawalAmount(50000L)
                .build();

        //when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndPassword(any(), any())).thenReturn(true);

        pointService.withdrawal(email, request);
        //then
        assertThat(user.getPoint()).isEqualTo(nowPoint - request.getWithdrawalAmount());

    }

    @Test
    @DisplayName("사용자 포인트 조회")
    void getPoint() throws Exception {
        //given
        User user = user0();
        String email = "rddd@naver.com";
        //when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        Long userPoint = pointService.getUserPoint(email);
        //then
        assertThat(user.getPoint()).isEqualTo(userPoint);
        System.out.println(userPoint);
    }

    @Test
    @DisplayName("포인트 차감내역 조회")
    void getDeductionHistory() throws Exception {
        //given
        User user = user0();
        String email = "rddd@naver.com";

        //when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        List<PointHistoryDto> deductionHistory = pointService.getDeductionHistory(email);

        long count = createPointBreakDownMockData().stream()
                .filter(p -> p.getDivision().equals(PointDivision.WITHDRAW) ||
                        p.getDivision().equals(PointDivision.PURCHASE_DEDUCTION))
                .count();
        //then
        assertThat(deductionHistory.size()).isEqualTo(count);

    }

    @Test
    @DisplayName("충전 내역조회")
    void getChargingHistory() throws Exception {
        //given
        User user = user0();
        String email = "rddd@naver.com";

        //when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        List<PointHistoryDto> history = pointService.getChargingHistory(email);
        long count = createPointBreakDownMockData().stream()
                .filter(p -> p.getDivision().equals(PointDivision.CHARGE) ||
                        p.getDivision().equals(PointDivision.SALES_REVENUE))
                .count();
        //then
        assertThat(history.size()).isEqualTo(count);

    }

    @Test
    @DisplayName("구매자의 포인트 지출 기록이 생성된다.")
    void purchasePointPayment() throws Exception {
        //given
        User buyer = user0();
        Long price = 198000L;
        //when
        pointService.purchasePointPayment(buyer, price);
        //then
        verify(pointRepository, times(1)).save(any());

    }

    @Test
    @DisplayName("구매자의 지출되었던 포인트 반환 기록이 생성된다.")
    void purchasePointReturn() throws Exception {
        //given
        User buyer = user0();
        Long price = 198000L;

        //when
        pointService.purchasePointPayment(buyer, price);
        //then
        verify(pointRepository, times(1)).save(any());

    }

    @Test
    @DisplayName("판매자에게 포인트 지급 기록이 생성된다.")
    void salesPointReceive()throws Exception{
        //given
        User seller = user0();
        Long price = 198000L;

        //when
        pointService.purchasePointPayment(seller,price);

        //then
        verify(pointRepository,times(1)).save(any());

    }

}
