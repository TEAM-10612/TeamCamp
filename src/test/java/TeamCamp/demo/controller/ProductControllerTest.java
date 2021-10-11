package TeamCamp.demo.controller;

import TeamCamp.demo.domain.model.product.ProductState;
import TeamCamp.demo.domain.model.trade.OrderStandard;
import TeamCamp.demo.domain.model.users.User;
import TeamCamp.demo.domain.model.users.UserLevel;
import TeamCamp.demo.domain.model.users.UserStatus;
import TeamCamp.demo.domain.model.users.user.address.AddressBook;
import TeamCamp.demo.dto.ProductDto;
import TeamCamp.demo.dto.TradeDto;
import TeamCamp.demo.dto.UserDto;
import TeamCamp.demo.service.ProductService;
import TeamCamp.demo.service.UserService;
import TeamCamp.demo.service.WishListService;
import TeamCamp.demo.service.loginservice.SessionLoginService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(ProductApiController.class)
@ActiveProfiles("test")
@MockBean(JpaMetamodelMappingContext.class)
class ProductControllerTest {
    @MockBean
    private ProductService productService;

    @MockBean
    UserService userService;

    @MockBean
    private SessionLoginService sessionLoginService;

    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    public void setup(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .apply(sharedHttpSession())
                .build();
    }

    public User toEntity() {
        return User.builder()
                .id(33L)
                .email("rddd@naver.com")
                .password("12222333")
                .nicknameModifiedDate(LocalDateTime.now())
                .nickname("ryu")
                .phone("01022334455")
                .userLevel(UserLevel.UNAUTH)
                .userStatus(UserStatus.NORMAL)
                .build();
    }

    private UserDto.UserInfo userInfo(){
        return UserDto.UserInfo.builder()
                .id(1L)
                .phone("01037734455")
                .nickname("ryudd")
                .nicknameModifiedDate(LocalDateTime.now())
                .password("1221122121")
                .point(1L)
                .email("rdj10149@naver.com")
                .userStatus(UserStatus.NORMAL)
                .userLevel(UserLevel.AUTH)
                .build();
    }

    private String productOriginImagePath = "https://cusproduct.s3.ap-northeast-2.amazonaws.com/product.png";
    private String productThumbnailImagePath = "https://cusproduct.s3.ap-northeast-2.amazonaws.com/product.png";

    private ProductDto.SaveRequest createSaveRequest(){
        return ProductDto.SaveRequest.builder()
                .name("텐트")
                .userInfo(userInfo())
                .productDescription("good")
                .productState(ProductState.BEST)
                .originImagePath("https://cusproduct.s3.ap-northeast-2.amazonaws.com/product.png")
                .build();
    }

    private ProductDto.ProductInfoResponse createProductInfo(){
        return ProductDto.ProductInfoResponse.builder()
                .id(1L)
                .name("화로")
                .user(userInfo())
                .productState(ProductState.BEST)
                .productDescription("good")
                .thumbnailImagePath(productThumbnailImagePath)
                .originImagePath(productOriginImagePath)
                .tradeCompleteInfos(createCompleteTrades())
                .build();
    }

    private List<TradeDto.TradeCompleteInfo>createCompleteTrades(){
        List<TradeDto.TradeCompleteInfo>list = new ArrayList<>();
        TradeDto.TradeCompleteInfo tradeCompleteInfo = TradeDto.TradeCompleteInfo.builder()
                .completeTime(LocalDateTime.now())
                .price(230000L)
                .build();
        list.add(tradeCompleteInfo);
        return list;
    }

    private ProductDto.ThumbnailResponse thumbnailResponse (){
        return ProductDto.ThumbnailResponse.builder()
                .id(12L)
                .productThumbnailImagePath(productThumbnailImagePath)
                .lowerPrice(100000L)
                .name("화로")
                .build();
    }

    private Pageable createPageable(){
        return PageRequest.of(0,10);
    }

    private Page<ProductDto.ThumbnailResponse> createProductThumbnailPage(){
        List<ProductDto.ThumbnailResponse> thumbnailResponseList = new ArrayList<>();
        thumbnailResponseList.add(thumbnailResponse());
        Pageable pageable = createPageable();

        return new PageImpl<>(thumbnailResponseList,pageable,1);
    }
    private ProductDto.SearchCondition searchCondition(){
        return ProductDto.SearchCondition.builder()
                .keyword("로")
                .orderStandard(OrderStandard.LOW_PRICE)
                .build();
    }
    private MockMultipartFile createImageFile() {
        return new MockMultipartFile("productImage", "productImage", MediaType.IMAGE_PNG_VALUE,
                "sample".getBytes());
    }

    private MockMultipartFile convertMultipartFile(Object dto)
            throws JsonProcessingException {
        return new MockMultipartFile("requestDto", "requestDto", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(dto).getBytes(
                        StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("상품을 생성한다.")
    void createProduct()throws Exception{
        //given
        ProductDto.SaveRequest request = createSaveRequest();
        MockMultipartFile requestDto = convertMultipartFile(request);
        MockMultipartFile productImage = createImageFile();

        //when
        doNothing().when(productService).saveProduct(request,productImage);

        //then
        mockMvc.perform(
                multipart("/products")
                        .file(requestDto)
                        .file(productImage)
                        .characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("products/create",
                        requestPartFields("requestDto",
                                fieldWithPath("name").type(JsonFieldType.STRING).description("제품 이름"),
                                fieldWithPath("userInfo").type(JsonFieldType.OBJECT).description("제품을 올린 유저"),
                                fieldWithPath("userInfo.id").ignored(),
                                fieldWithPath("userInfo.phone").ignored(),
                                fieldWithPath("userInfo.password").ignored(),
                                fieldWithPath("userInfo.nickname").ignored(),
                                fieldWithPath("userInfo.email").ignored(),
                                fieldWithPath("userInfo.nicknameModifiedDate").ignored(),
                                fieldWithPath("userInfo.userLevel").ignored(),
                                fieldWithPath("userInfo.point").ignored(),
                                fieldWithPath("userInfo.userStatus").ignored(),
                                fieldWithPath("productDescription").type(JsonFieldType.STRING).description("제품 설명"),
                                fieldWithPath("productState").type(JsonFieldType.STRING).description("제품 상태"),
                                fieldWithPath("originImagePath").ignored(),
                                fieldWithPath("thumbnailImagePath").ignored()),
                        requestParts(
                                partWithName("requestDto").ignored(),
                                partWithName("productImage").description("상품 이미지").optional())
                ));

    }
    @Test
    @DisplayName("상품 상제 정보 조회")
    void getProductInfo()throws Exception{
        //given
        ProductDto.ProductInfoResponse response = createProductInfo();
        Long id = response.getId();

        //when
        given(productService.getProductInfo(id)).willReturn(response);

        //then
        mockMvc.perform(
                RestDocumentationRequestBuilders.get("/products/{id}",id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("products/get/details",
                        pathParameters(
                                parameterWithName("id").description("조회할 상품의 Id")
                        ),
                        responseFields(
                            fieldWithPath("id").type(JsonFieldType.NUMBER)
                                    .description("product Id"),
                            fieldWithPath("name").type(JsonFieldType.STRING)
                                    .description("product name"),
                                fieldWithPath("user").type(JsonFieldType.OBJECT)
                                        .description("상품 소유주"),
                                fieldWithPath("user.id").ignored(),
                                fieldWithPath("user.phone").ignored(),
                                fieldWithPath("user.nickname").ignored(),
                                fieldWithPath("user.password").ignored(),
                                fieldWithPath("user.userStatus").ignored(),
                                fieldWithPath("user.email").ignored(),
                                fieldWithPath("user.nicknameModifiedDate").ignored(),
                                fieldWithPath("user.userLevel").ignored(),
                                fieldWithPath("user.point").ignored(),
                                fieldWithPath("user.addressBook").ignored(),
                                fieldWithPath("productDescription").type(JsonFieldType.STRING)
                                        .description("productDescription"),
                                fieldWithPath("productState").type(JsonFieldType.STRING)
                                        .description("productStatus"),
                                fieldWithPath("originImagePath").type(JsonFieldType.STRING)
                                        .description("originImagePath"),
                                fieldWithPath("thumbnailImagePath").type(JsonFieldType.STRING)
                                        .description("thumbnailImagePath"),
                                fieldWithPath("tradeCompleteInfos[].price").type(JsonFieldType.NUMBER)
                                        .description("tradeCompleteInfos.price"),
                                fieldWithPath("tradeCompleteInfos[].completeTime").type(JsonFieldType.STRING)
                                        .description("tradeCompleteInfos.completeTime")

                        )));

    }


    @Test
    @DisplayName("검색 조건 별 상품들의 썸네일을 조회한다.")
    void findProducts()throws Exception{
        //given

        //when

        //then


    }


    @Test
    @DisplayName("상품 정보 수정")
    void updateProduct()throws Exception{
        //given
        Long id = 1L;
        ProductDto.SaveRequest updateRequest = createSaveRequest();
        MockMultipartFile requestDto = convertMultipartFile(updateRequest);
        MockMultipartFile productImage = createImageFile();

        //when
        MockMultipartHttpServletRequestBuilder builder =
                RestDocumentationRequestBuilders.fileUpload("/products/{id}",id);
        builder.with(request -> {
            request.setMethod("PATCH");
            return request;
        });

        //then
        mockMvc.perform(
                        builder
                                .file(requestDto)
                                .file(productImage)
                                .characterEncoding("utf-8")
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("products/update",
                        pathParameters(
                                parameterWithName("id").description("수정할 상품의 ID")
                        ),
                        requestPartFields("requestDto",
                                fieldWithPath("name").type(JsonFieldType.STRING).description("제품 이름"),
                                fieldWithPath("userInfo").type(JsonFieldType.OBJECT).description("제품을 올린 유저"),
                                fieldWithPath("userInfo.id").ignored(),
                                fieldWithPath("userInfo.phone").ignored(),
                                fieldWithPath("userInfo.password").ignored(),
                                fieldWithPath("userInfo.nickname").ignored(),
                                fieldWithPath("userInfo.email").ignored(),
                                fieldWithPath("userInfo.nicknameModifiedDate").ignored(),
                                fieldWithPath("userInfo.userLevel").ignored(),
                                fieldWithPath("userInfo.point").ignored(),
                                fieldWithPath("userInfo.userStatus").ignored(),
                                fieldWithPath("productDescription").type(JsonFieldType.STRING).description("제품 설명"),
                                fieldWithPath("productState").type(JsonFieldType.STRING).description("제품 상태"),
                                fieldWithPath("originImagePath").ignored(),
                                fieldWithPath("thumbnailImagePath").ignored()),
                        requestParts(
                                partWithName("requestDto").ignored(),
                                partWithName("productImage").description("상품 이미지").optional())
                ));

    }


    @Test
    @DisplayName("상품 정보 삭제")
    void deleteProduct()throws Exception{
        //given
        Long id = 1L;

        //when
        doNothing().when(productService).deleteProduct(id);

        //then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/products/{id}",id))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document(
                        "products/delete",
                        pathParameters(
                                parameterWithName("id").description("삭제할 상품의 ID")
                        )));

    }
}