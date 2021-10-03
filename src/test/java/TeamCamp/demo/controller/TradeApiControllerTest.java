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
import TeamCamp.demo.service.TradeService;
import TeamCamp.demo.service.loginservice.SessionLoginService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
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

    private String ProductOriginImagePath = "https://cusproduct.s3.ap-northeast-2.amazonaws.com/9b9a6239-fbae-4ce3-bba3-731ddfc1fea8.jpg";
    private String ProductThumbnailImagePath = "https://cusproduct.s3.ap-northeast-2.amazonaws.com/9b9a6239-fbae-4ce3-bba3-731ddfc1fea8.jpg";
    private String ProductChangedOriginImagePath =  "https://cusproduct.s3.ap-northeast-2.amazonaws.com/9b9a6239-fbae-4ce3-bba3-731ddfc1fea8.jpg";
    private String ProductChangedThumbnailImagePath =  "https://cusproduct.s3.ap-northeast-2.amazonaws.com/9b9a6239-fbae-4ce3-bba3-731ddfc1fea8.jpg";

    private Product createProduct(){
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

    private List<Trade> createTrades(){
        User user0 = user1();
        User user1 = user2();
        Address address = new Address(1L,"내집","대정로1","101동1004호","56432");
        Product product = createProduct();
        List<Trade> list = new ArrayList<>();

        Trade sale1 = Trade.builder()
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
                .seller(null)
                .buyer(user0)
                .product(product)
                .tradeStatus(TradeStatus.BID)
                .returnAddress(address)
                .transactionMethod(TransactionMethod.ALL)
                .shippingAddress(null)
                .build();
        list.add(sale1);

        Trade sale2 = Trade.builder()
                .seller(user1)
                .buyer(null)
                .product(product)
                .tradeStatus(TradeStatus.BID)
                .returnAddress(address)
                .transactionMethod(TransactionMethod.ALL)
                .shippingAddress(null)
                .build();
        list.add(sale1);

        Trade purchase2 = Trade.builder()
                .buyer(user1)
                .seller(null)
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
        Product product = createProduct();
        Address address = new Address(1L,"내집","대정로1","101동1004호","56432");
        return Trade.builder()
                .id(1L)
                .seller(user)
                .buyer(null)
                .product(product)
                .tradeStatus(TradeStatus.BID)
                .price("230000")
                .returnAddress(address)
                .shippingAddress(null)
                .build();
    }

}