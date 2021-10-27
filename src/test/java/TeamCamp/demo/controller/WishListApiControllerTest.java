package TeamCamp.demo.controller;

import TeamCamp.demo.domain.model.product.Product;
import TeamCamp.demo.domain.model.product.ProductState;
import TeamCamp.demo.domain.model.users.User;
import TeamCamp.demo.domain.model.users.UserLevel;
import TeamCamp.demo.domain.model.users.UserStatus;
import TeamCamp.demo.dto.ProductDto;
import TeamCamp.demo.dto.ProductDto.WishProductResponse;
import TeamCamp.demo.dto.UserDto;
import TeamCamp.demo.service.WishListService;
import TeamCamp.demo.service.loginservice.SessionLoginService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;
import static org.springframework.util.Assert.state;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(WishListApiController.class)
@ActiveProfiles("test")
@MockBean(JpaMetamodelMappingContext.class)
class WishListApiControllerTest {

    @MockBean
    private WishListService wishListService;

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
                .email("rddd@naver.com")
                .password("12222333")
                .nicknameModifiedDate(LocalDateTime.now())
                .nickname("ryu")
                .phone("01022334455")
                .userLevel(UserLevel.UNAUTH)
                .userStatus(UserStatus.NORMAL)
                .build();
    }
    User user = toEntity();
    UserDto.UserInfoDto userInfoDto = UserDto.UserInfoDto.builder()
            .email("rdj1014@naver.com")
            .userLevel(UserLevel.AUTH)
            .phone("01011121233")
            .nickname("ryu")
            .build();
    private String ProductOriginImagePath = "https://TremCamp-product-origin.s3.ap-northeast-2.amazonaws.com/sample.png";
    private String ProductThumbnailImagePath = "https://TremCamp-product-thumbnail.s3.ap-northeast-2.amazonaws.com/sample.png";



    private Set<WishProductResponse> createWishList(){
        Set<WishProductResponse> set = new HashSet<>();


        ProductDto.WishProductResponse wishProductResponse = WishProductResponse.builder()
                .id(1L)
                .productId(2L)
                .name("텐트")
                .userInfoDto(userInfoDto)
                .build();
        set.add(wishProductResponse);

        return set;
    }
    private Product createProduct(){
        return Product.builder()
                .id(2L)
                .name("텐트")
                .user(user)
                .productDescription("good")
                .productState(ProductState.BEST)
                .originImagePath(ProductOriginImagePath)
                .thumbnailImagePath(ProductThumbnailImagePath)
                .build();
    }

    @Test
    @DisplayName("위시리스트 조회")
    void getWishList()throws Exception{
        //given
        Set<WishProductResponse> wishList = createWishList();

        //when
        BDDMockito.given(wishListService.getWishList(any())).willReturn(wishList);

        //then
        mockMvc.perform(get("/wishlists"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("users/carts/getWishList",responseFields(
                        fieldWithPath("[].id").type(JsonFieldType.NUMBER)
                                .description("위시리스트 ID"),
                        fieldWithPath("[].productId").type(JsonFieldType.NUMBER)
                                .description("Product ID"),
                        fieldWithPath("[].name").type(JsonFieldType.STRING)
                                .description("제품 이름"),
                        fieldWithPath("[].userInfoDto.email").type(JsonFieldType.STRING)
                                .description("회원  이메일"),
                        fieldWithPath("[].userInfoDto.phone").type(JsonFieldType.STRING)
                                .description("회원  전화번호"),
                        fieldWithPath("[].userInfoDto.nickname").type(JsonFieldType.STRING)
                                .description("회원  닉네임"),
                        fieldWithPath("[].userInfoDto.userLevel").type(JsonFieldType.STRING)
                                .description("회원 권한")
                        )));

    }


    @Test
    @DisplayName("위시리스트에 상품을 추가한다.")
    void addWishlist()throws Exception{
        //given
        String email = "rddd@naver.com";
        ProductDto.IdRequest idRequest = ProductDto.IdRequest.builder()
                .id(1L)
                .build();
        //when
        doNothing().when(wishListService).addWishList(email,idRequest);

        //then
        mockMvc.perform(post("/wishlists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(idRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("users/carts/addWishList",requestFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("상품 ID")
                )));

    }

    @Test
    @DisplayName("위시리스트에 상품을 삭제한다.")
    void deleteWishlist()throws Exception{
        //given
        ProductDto.IdRequest idRequest = ProductDto.IdRequest.builder()
                .id(1L)
                .build();
        //when
        doNothing().when(wishListService).deleteWishList(idRequest);

        //then
        mockMvc.perform(delete("/wishlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(idRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("users/carts/deleteWishList",requestFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("상품 ID")
                )));

    }
}