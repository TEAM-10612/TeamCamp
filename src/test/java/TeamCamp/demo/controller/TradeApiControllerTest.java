package TeamCamp.demo.controller;

import TeamCamp.demo.domain.model.product.Product;
import TeamCamp.demo.domain.model.product.ProductState;
import TeamCamp.demo.domain.model.trade.Trade;
import TeamCamp.demo.domain.model.trade.TradeStatus;
import TeamCamp.demo.domain.model.trade.TransactionMethod;
import TeamCamp.demo.domain.model.users.User;
import TeamCamp.demo.domain.model.users.UserLevel;
import TeamCamp.demo.domain.model.users.UserStatus;
import TeamCamp.demo.domain.model.users.user.address.Address;
import TeamCamp.demo.domain.model.users.user.address.AddressBook;
import TeamCamp.demo.dto.ProductDto;
import TeamCamp.demo.dto.TradeDto;
import TeamCamp.demo.service.TradeService;
import TeamCamp.demo.service.loginservice.SessionLoginService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(TradeApiController.class)
@ActiveProfiles("test")
@MockBean(JpaMetamodelMappingContext.class)
class TradeApiControllerTest {

    @MockBean
    private TradeService tradeService;

    @MockBean
    private SessionLoginService sessionLoginService;

    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    public void setup(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentationContextProvider){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentationContextProvider))
                .apply(sharedHttpSession())
                .build();
    }

    public User user1() {
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
                .build();
    }
    public User user2() {
        return User.builder()
                .id(2L)
                .email("rdj1014@naver.com")
                .password("22222222")
                .nicknameModifiedDate(LocalDateTime.now())
                .nickname("ryu")
                .phone("01022222222")
                .userLevel(UserLevel.UNAUTH)
                .userStatus(UserStatus.NORMAL)
                .addressBooks(new AddressBook())
                .build();
    }
    private Product createProduct0(){
        return Product.builder()
                .id(1L)
                .name("텐트")
                .user(user1())
                .productDescription("good")
                .productState(ProductState.BEST)
                .originImagePath(ProductOriginImagePath)
                .thumbnailImagePath(ProductThumbnailImagePath)
                .build();
    }

    private String ProductOriginImagePath = "https://cusproduct.s3.ap-northeast-2.amazonaws.com/9b9a6239-fbae-4ce3-bba3-731ddfc1fea8.jpg";
    private String ProductThumbnailImagePath = "https://cusproduct.s3.ap-northeast-2.amazonaws.com/9b9a6239-fbae-4ce3-bba3-731ddfc1fea8.jpg";
    private String ProductChangedOriginImagePath =  "https://cusproduct.s3.ap-northeast-2.amazonaws.com/9b9a6239-fbae-4ce3-bba3-731ddfc1fea8.jpg";
    private String ProductChangedThumbnailImagePath =  "https://cusproduct.s3.ap-northeast-2.amazonaws.com/9b9a6239-fbae-4ce3-bba3-731ddfc1fea8.jpg";

    private ProductDto.SaveRequest  createProduct(){
        return ProductDto.SaveRequest.builder()
                .name("텐트")
                .userInfo(user1().toUserInfo())
                .productDescription("good")
                .productState(ProductState.BEST)
                .originImagePath(ProductOriginImagePath)
                .thumbnailImagePath(ProductThumbnailImagePath)
                .build();
    }

    private List<Trade> createTrades(){
        User user0 = user1();
        User user1 = user2();
        Address address = new Address(1L,"내집","대정로1","101동1004호","56432");
        Product product = createProduct().toEntity();
        List<Trade> list = new ArrayList<>();

        Trade sale1 = Trade.builder()
                .id(2L)
                .seller(user0)
                .buyer(null)
                .product(product)
                .tradeStatus(TradeStatus.BID)
                .returnAddress(address)
                .transactionMethod(TransactionMethod.ALL)
                .shippingAddress(null)
                .build();
        list.add(sale1);

        Trade purchase1 = Trade.builder()
                .id(3L)
                .seller(null)
                .buyer(user0)
                .product(product)
                .tradeStatus(TradeStatus.BID)
                .returnAddress(address)
                .transactionMethod(TransactionMethod.ALL)
                .shippingAddress(null)
                .build();
        list.add(sale1);

        return list;
    }

    private Trade createTrade(){
        User user = user1();
        Product product = createProduct().toEntity();
        Address address = new Address(3L,"내집","대정로1","101동1004호","56432");
        return Trade.builder()
                .id(11L)
                .seller(user)
                .buyer(null)
                .product(product)
                .tradeStatus(TradeStatus.BID)
                .price(230000L)
                .returnAddress(address)
                .shippingAddress(null)
                .build();
    }

    private List<TradeDto.TradeInfoResponse> createTradeInfoList(){
        List<TradeDto.TradeInfoResponse> list = new ArrayList<>();
        Long id = 1L;
        String email = id+"@naver.com";
        for(int i = 0; i < 5; i++){
            TradeDto.TradeInfoResponse response1 = TradeDto.TradeInfoResponse.builder()
                    .id(id++)
                    .tradeStatus(TradeStatus.PROGRESS)
                    .build();

            list.add(response1);
            TradeDto.TradeInfoResponse response2 = TradeDto.TradeInfoResponse.builder()
                    .id(id++)
                    .tradeStatus(TradeStatus.PROGRESS)
                    .build();

            list.add(response2);
            TradeDto.TradeInfoResponse response3 = TradeDto.TradeInfoResponse.builder()
                    .id(id++)
                    .tradeStatus(TradeStatus.CANCEL)
                    .build();

            list.add(response3);
        }
        return list;
    }


    private List<TradeDto.TradeInfoResponse> createTradeInfoSearchBySellerEmail(
            TradeDto.TradeSearchCondition tradeSearchCondition){
        List<Trade> trades = createTrades();
        List<TradeDto.TradeInfoResponse> listSearchedBySellerEmail = new ArrayList<>();

        List<Long> tradeId = trades.stream()
                .filter(s -> s.getSeller().getEmail().equals(tradeSearchCondition.getSellerEmail()))
                .map(Trade::getId)
                .collect(Collectors.toList());


        List<TradeStatus> tradeStatuses = trades.stream()
                .filter(s -> s.getSeller().getEmail().equals(tradeSearchCondition.getSellerEmail()))
                .map(Trade::getTradeStatus)
                .collect(Collectors.toList());

        int length = tradeId.size();

        for(int count = 0; count < length; count++){
            listSearchedBySellerEmail.add(TradeDto.TradeInfoResponse.builder()
                            .id(tradeId.get(count))
                            .tradeStatus(tradeStatuses.get(count))
                            .build());
        }
        return listSearchedBySellerEmail;
    }

    @Test
    @DisplayName("상품 판매")
    void sales()throws Exception{
        //given
        String email = "rddd@naver.com";
        TradeDto.TradeRequest request = TradeDto.TradeRequest.builder()
                .tradeId(11L)
                .productId(1L)
                .addressId(3L)
                .build();
        //when
        doNothing().when(tradeService).sale(email,request);

        //then
        mockMvc.perform(post("/trade/sell")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("trade/sell",
                        requestFields(
                                fieldWithPath("tradeId").description(JsonFieldType.NUMBER)
                                        .description("tradeId"),
                                fieldWithPath("productId").description(JsonFieldType.NUMBER)
                                        .description("productId"),
                                fieldWithPath("addressId").description(JsonFieldType.NUMBER)
                                        .description("addressId")

                        )));

    }


    @Test
    @DisplayName("상품 구매")
    void purchase()throws Exception{
        //given
        String email = "rddd@naver.com";
        TradeDto.TradeRequest request = TradeDto.TradeRequest.builder()
                .tradeId(11L)
                .productId(1L)
                .addressId(3L)
                .build();
        //when
        doNothing().when(tradeService).purchase(email,request);

        //then
        mockMvc.perform(post("/trade/buy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("trade/buy",
                        requestFields(
                                fieldWithPath("tradeId").description(JsonFieldType.NUMBER)
                                        .description("tradeId"),
                                fieldWithPath("addressId").description(JsonFieldType.NUMBER)
                                        .description("배송 주소"),
                                fieldWithPath("productId").description(JsonFieldType.NUMBER)
                                        .description("productId")
                        )));

    }

    @Test
    @DisplayName("구매자가 거래 완료 처리한다.")
    void ConfirmPurchase()throws Exception{
        //given
        Long id = 1L;
        String email = "rdj1014@naver.com";

        //when
        doNothing().when(tradeService).confirmPurchase(id,email);

        //then
        mockMvc.perform(patch("/trade/{id}/purchase-confirmation",id))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("trade/confirm/purchase",
                        pathParameters(
                                parameterWithName("id").description("거래완료 처리할 거래 ID")
                        )));

    }


    @Test
    @DisplayName("특정 거래의 출고 운송장 번호 입력")
    void updateForwardingTrackingNumber()throws Exception{
        //given
        Long id = 1L;
        TradeDto.TrackingNumberRequest request = TradeDto.TrackingNumberRequest.builder()
                .trackingNumber("12345678")
                .build();

        //when
        doNothing().when(tradeService)
                .updateTrackingNumber(id,request.getTrackingNumber());

        //then
        mockMvc.perform(patch("/trade/{id}/forwarding-tracking-number",id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk())
                        .andDo(print())
                        .andDo(document("trade/update/forwarding-tracking-number",
                        pathParameters(
                                parameterWithName("id").description("운송장을 등록할 ID")
                        ),
                                requestFields(
                                        fieldWithPath("trackingNumber")
                                                .type(JsonFieldType.STRING)
                                                .description("등록할 운송장 번호")
                                )));

    }
    @Test
    @DisplayName("특정 거래의 출고 운송장 번호 입력")
    void updateReturnTrackingNumber()throws Exception{
        //given
        Long id = 1L;
        String  email = "rdj1014@naver.com";
        TradeDto.TrackingNumberRequest request = TradeDto.TrackingNumberRequest.builder()
                .trackingNumber("12345678")
                .build();

        //when
        doNothing().when(tradeService)
                .updateReceivingTrackingNumber(id,email,request.getTrackingNumber());

        //then
        mockMvc.perform(patch("/trade/{id}/receiving-tracking-number",id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("trade/update/receiving-tracking-number",
                        pathParameters(
                                parameterWithName("id").description("운송장을 등록할 ID")
                        ),
                        requestFields(
                                fieldWithPath("trackingNumber")
                                        .type(JsonFieldType.STRING)
                                        .description("등록할 운송장 번호")
                        )));

    }

}