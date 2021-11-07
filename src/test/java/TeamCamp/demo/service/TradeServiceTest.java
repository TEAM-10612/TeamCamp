package TeamCamp.demo.service;

import TeamCamp.demo.domain.model.product.Product;
import TeamCamp.demo.domain.model.product.ProductState;
import TeamCamp.demo.domain.model.trade.Trade;
import TeamCamp.demo.domain.model.trade.TradeStatus;
import TeamCamp.demo.domain.model.trade.TransactionMethod;
import TeamCamp.demo.domain.model.trade.repository.TradeRepository;
import TeamCamp.demo.domain.model.users.User;
import TeamCamp.demo.domain.model.users.UserLevel;
import TeamCamp.demo.domain.model.users.UserStatus;
import TeamCamp.demo.domain.model.address.Address;
import TeamCamp.demo.domain.model.address.repository.AddressRepository;
import TeamCamp.demo.domain.model.product.repository.ProductRepository;
import TeamCamp.demo.domain.model.users.repository.UserRepository;
import TeamCamp.demo.dto.ProductDto;
import TeamCamp.demo.dto.TradeDto;
import TeamCamp.demo.dto.TradeDto.TradeResource;
import TeamCamp.demo.dto.UserDto;
import TeamCamp.demo.exception.user.NotAuthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradeServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;
    @Mock
    private AddressRepository addressRepository;

    @Mock
    private PointService pointService;
    @Mock
    private TradeRepository tradeRepository;

    @InjectMocks
    private TradeService tradeService;


    private User user0(){
        return User.builder()
                .id(10L)
                .email("rdj1014@naver.com")
                .password("ehdwo991014")
                .phone("01033234455")
                .nickname("ryu")
                .nicknameModifiedDate(LocalDateTime.now())
                .userLevel(UserLevel.AUTH)
                .userStatus(UserStatus.NORMAL)
                .point(0L)
                .build();
    }

    private User user1(){
        return User.builder()
                .id(2L)
                .email("rdj10149@naver.com")
                .password("test1234")
                .phone("01037734455")
                .nickname("ryudd")
                .nicknameModifiedDate(LocalDateTime.now())
                .userLevel(UserLevel.AUTH)
                .userStatus(UserStatus.NORMAL)
                .point(0L)
                .build();
    }

    private UserDto.UserInfo userInfo = UserDto.UserInfo.builder()
            .email("rdj10149@naver.com")
            .password("test1234")
            .phone("01037734455")
            .nickname("ryudd")
            .nicknameModifiedDate(LocalDateTime.now())
            .userLevel(UserLevel.AUTH)
            .userStatus(UserStatus.NORMAL)
            .point(0L)
            .build();


    private String ProductOriginImagePath = "https://cusproduct.s3.ap-northeast-2.amazonaws.com/9b9a6239-fbae-4ce3-bba3-731ddfc1fea8.jpg";
    private String ProductThumbnailImagePath = "https://cusproduct.s3.ap-northeast-2.amazonaws.com/9b9a6239-fbae-4ce3-bba3-731ddfc1fea8.jpg";
    private String ProductChangedOriginImagePath =  "https://cusproduct.s3.ap-northeast-2.amazonaws.com/9b9a6239-fbae-4ce3-bba3-731ddfc1fea8.jpg";
    private String ProductChangedThumbnailImagePath =  "https://cusproduct.s3.ap-northeast-2.amazonaws.com/9b9a6239-fbae-4ce3-bba3-731ddfc1fea8.jpg";

    private UserDto.SaveRequest createUserDto() {
        UserDto.SaveRequest saveRequest = UserDto.SaveRequest.builder()
                .email("test123@test.com")
                .password("test1234")
                .phone("01011112222")
                .nickname("123123123")
                .build();
        return saveRequest;
    }
    private ProductDto.SaveRequest createProductDto(){
        return ProductDto.SaveRequest.builder()
                .name("텐트")
                .userInfo(userInfo)
                .productDescription("good")
                .productState(ProductState.BEST)
                .originImagePath(ProductOriginImagePath)
                .thumbnailImagePath(ProductThumbnailImagePath)
                .build();
    }
    private Product createProduct(){
        return Product.builder()
                .id(1L)
                .name("텐트")
                .user(user0())
                .productDescription("good")
                .productState(ProductState.BEST)
                .originImagePath(ProductOriginImagePath)
                .thumbnailImagePath(ProductThumbnailImagePath)
                .trades(createTrades())
                .build();
    }

    private List<Trade>createTrades(){
        User user0 = user0();
        Address address = new Address(1L,"내집","대정로1","101동1004호","56432");
        Product product = createProductDto().toEntity();
        List<Trade> list = new ArrayList<>();

        Trade sale = Trade.builder()
                .seller(user0)
                .buyer(null)
                .product(product)
                .tradeStatus(TradeStatus.BID)
                .returnAddress(address)
                .transactionMethod(TransactionMethod.ALL)
                .shippingAddress(null)
                .build();
        list.add(sale);

        Trade purchase = Trade.builder()
                .seller(null)
                .buyer(user0)
                .product(product)
                .tradeStatus(TradeStatus.BID)
                .returnAddress(address)
                .transactionMethod(TransactionMethod.ALL)
                .shippingAddress(null)
                .build();
        list.add(purchase);
        return list;
    }

    private Trade createTrade(){
        User user = user0();
        Product product = createProduct();
        Address address = new Address(1L,"내집","대정로1","101동1004호","56432");
         return Trade.builder()
                 .id(11L)
                 .seller(null)
                 .buyer(user)
                 .product(product)
                 .tradeStatus(TradeStatus.BID)
                 .price(230000L)
                 .returnAddress(address)
                 .transactionMethod(TransactionMethod.ALL)
                 .shippingAddress(null)
                 .build();
    }
    private Trade concludeBuyerTrade(){
        User seller = user0();
        User buyer = user1();
        Product product = createProduct();
        Address address = new Address(2L,"내집","대정로1","101동1004호","56432");
        return Trade.builder()
                .id(112L)
                .seller(seller)
                .buyer(buyer)
                .product(product)
                .tradeStatus(TradeStatus.BID)
                .price(230000L)
                .returnAddress(address)
                .transactionMethod(TransactionMethod.ALL)
                .shippingAddress(null)
                .build();
    }

//    private List<TradeDto.TradeInfoResponse>  createTradeInfoList(){
//        List<TradeDto.TradeInfoResponse> list = new ArrayList<>();
//        Long id =1L;
//
//        for(int i = 0; i< 5; i++){
//
//        }
//    }


    @Test
    @DisplayName("리소스 반환")
    void getResourceForTrade()throws Exception{
        //given
        String email = "rdj1014@naver.com";
        Long productId = 1L;
        User user = user0();
        Product product = createProduct();

        //when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        TradeResource tradeResource =  tradeService.getResource(email,productId);
        //then
        assertThat(tradeResource.getProductInfoByTrade().getBuyPrice()).isNull();
        assertThat(tradeResource.getProductInfoByTrade().getSellPrice()).isNull();

    }
    @Test
    @DisplayName("물품 구매")
    void purchase()throws Exception{
        //given
        Address address = new Address(2L,"내집","대정로1","101동1004호","56432");
        String email = "rdj1014@naver.com";
        User user = user0();
        user.chargingPoint(1000000L);
        User anotherUser = user1();
        Product product = createProduct();

        Trade purchaseTrade = Trade.builder()
                .id(5L)
                .seller(anotherUser)
                .buyer(null)
                .product(product)
                .tradeStatus(TradeStatus.BID)
                .price(230000L)
                .returnAddress(address)
                .transactionMethod(TransactionMethod.ALL)
                .shippingAddress(null)
                .build();

        TradeDto.TradeRequest request = TradeDto.TradeRequest.builder()
                .tradeId(5L)
                .addressId(2L)
                .productId(1L)
                .build();
        //when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(addressRepository.findById(request.getAddressId())).thenReturn(Optional.of(address));
        when(tradeRepository.findById(request.getTradeId())).thenReturn(Optional.of(purchaseTrade));
        tradeService.purchase(email,request);

        //then
        assertThat(purchaseTrade.getTradeStatus()).isEqualTo(TradeStatus.PROGRESS);
        assertThat(purchaseTrade.getBuyer().getId()).isEqualTo(user.getId());
        assertThat(purchaseTrade.getShippingAddress().getId()).isEqualTo(address.getId());

    }
    @Test
    @DisplayName("물품 판매")
    void sales()throws Exception{
        //given
        Address address = new Address(2L,"내집","대정로1","101동1004호","56432");
        String email = "rdj1014@naver.com";
        User user = user0();
        User anotherUser = user1();
        Product product = createProduct();

        Trade saleTrade = Trade.builder()
                .seller(null)
                .buyer(anotherUser)
                .product(product)
                .tradeStatus(TradeStatus.BID)
                .price(230000L)
                .returnAddress(address)
                .transactionMethod(TransactionMethod.ALL)
                .shippingAddress(null)
                .build();

        TradeDto.TradeRequest request = TradeDto.TradeRequest.builder()
                .tradeId(5L)
                .addressId(2L)
                .productId(1L)
                .build();
        //when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(addressRepository.findById(request.getAddressId())).thenReturn(Optional.of(address));
        when(tradeRepository.findById(request.getTradeId())).thenReturn(Optional.of(saleTrade));
        tradeService.sale(email,request);

        //then

        assertThat(saleTrade.getTradeStatus()).isEqualTo(TradeStatus.PROGRESS);
        assertThat(saleTrade.getSeller().getId()).isEqualTo(user.getId());
        assertThat(saleTrade.getShippingAddress().getId()).isEqualTo(address.getId());
        System.out.println(user.getPoint());
    }


    @Test
    @DisplayName("거래 수정")
    void updateTrade()throws Exception{
        //given
        Trade trade = createTrade();
        TradeDto.ChangeRequest request = TradeDto.ChangeRequest.builder()
                .tradeId(11L)
                .price(250000L)
                .build();
        //when
        when(tradeRepository.findById(request.getTradeId())).thenReturn(Optional.of(trade));
        tradeService.updateTrade(request);
        //then
        assertThat(trade.getPrice()).isEqualTo(request.getPrice());

    }

    @Test
    @DisplayName("셀러가 아닌 바이어가 입고운송장 번호 입력을 시도할 경우 실패한다.")
    void failToUpdateReceivingTrackingNumber()throws Exception{
        //given
        Trade trade = concludeBuyerTrade();
        User buyer = user1();
        Product product = createProduct();
        String email = buyer.getEmail();
        Long tradeId = trade.getId();
        String trackingNumber = "12345678";

        //when
        given(tradeRepository.findById(tradeId)).willReturn(Optional.of(trade));
        given(userRepository.findByEmail(email)).willReturn(Optional.of(buyer));

        //then
        assertThrows(NotAuthorizedException.class,
                ()-> tradeService.updateReceivingTrackingNumber(tradeId,email,trackingNumber));
        assertThat(trade.getReceivingTrackingNumber()).isNull();


    }

    @Test
    @DisplayName("판매자가 상품 발송 후 입고 운송장 번호를 입력한다.")
    void updateReceivingTrackingNumber()throws Exception{
        //given
        Trade trade = concludeBuyerTrade();
        User seller = trade.getSeller();
        Long tradeId = trade.getId();
        String email = trade.getSeller().getEmail();
        String trackingNumber  = "12345678";

        //when
        given(tradeRepository.findById(tradeId)).willReturn(Optional.of(trade));
        given(userRepository.findByEmail(email)).willReturn(Optional.of(seller));

        tradeService.updateReceivingTrackingNumber(tradeId,email,trackingNumber);
        //then
        assertThat(trade.getReceivingTrackingNumber()).isEqualTo(trackingNumber);

    }


    @Test
    @DisplayName("구매 확정 요청 성공")
    void confirmPurchase()throws Exception{
        //given
        Trade trade = concludeBuyerTrade();
        Long tradeId = trade.getId();
        Long tradePrice =  trade.getPrice();
        String email = trade.getBuyer().getEmail();
        Long preSellerPoint = trade.getSeller().getPoint();

        //when
        given(tradeRepository.findById(tradeId)).willReturn(Optional.of(trade));
        tradeService.confirmPurchase(tradeId,email);

        //then
        assertThat(trade.getTradeStatus()).isEqualTo(TradeStatus.END);
        assertThat(trade.getSeller().getPoint()).isEqualTo(preSellerPoint+tradePrice);


    }
}