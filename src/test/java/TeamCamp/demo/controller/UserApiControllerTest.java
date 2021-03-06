package TeamCamp.demo.controller;

import TeamCamp.demo.domain.model.product.Product;
import TeamCamp.demo.domain.model.product.ProductState;
import TeamCamp.demo.domain.model.users.UserLevel;
import TeamCamp.demo.domain.model.users.UserStatus;
import TeamCamp.demo.domain.model.users.user.Account;
import TeamCamp.demo.domain.model.users.User;
import TeamCamp.demo.domain.model.address.Address;
import TeamCamp.demo.dto.AddressDto;
import TeamCamp.demo.dto.ProductDto;
import TeamCamp.demo.exception.user.*;
import TeamCamp.demo.service.UserService;
import TeamCamp.demo.service.email.EmailCertificationService;
import TeamCamp.demo.service.login.SessionLoginService;
import TeamCamp.demo.service.sms.SmsCertificationService;
import TeamCamp.demo.dto.UserDto;
import TeamCamp.demo.dto.UserDto.SaveRequest;
import TeamCamp.demo.exception.certification.AuthenticationNumberMismatchException;
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
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.*;

import static TeamCamp.demo.util.UserConstants.USER_ID;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;


@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(UserApiController.class)
@ActiveProfiles("test")
@MockBean(JpaMetamodelMappingContext.class)
class UserApiControllerTest {

    @MockBean
    private UserService userService;

    @MockBean
    private SmsCertificationService smsCertificationService;

    @MockBean
    private EmailCertificationService emailCertificationService;
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

    private String ProductOriginImagePath = "https://TremCamp-product-origin.s3.ap-northeast-2.amazonaws.com/sample.png";
    private String ProductThumbnailImagePath = "https://TremCamp-product-thumbnail.s3.ap-northeast-2.amazonaws.com/sample.png";

    private Product createProduct(){
        return Product.builder()
                .id(1L)
                .name("??????")
                .user(user)
                .productDescription("good")
                .productState(ProductState.BEST)
                .originImagePath(ProductOriginImagePath)
                .thumbnailImagePath(ProductThumbnailImagePath)
                .build();
    }

    private Set<ProductDto.WishProductResponse> createWishList(){
        Set<ProductDto.WishProductResponse> set = new HashSet<>();

        ProductDto.WishProductResponse wishProductResponse = ProductDto.WishProductResponse.builder()
                .id(1L)
                .productId(createProduct().getId())
                .name(createProduct().getName())
                .build();
        set.add(wishProductResponse);

        return set;
    }

    @Test
    @DisplayName("???????????? - ????????? ????????? ??????????????? ??????????????? ????????????.")
    void createUser_successful() throws Exception {
        //given
        SaveRequest saveRequest = SaveRequest.builder()
                .email("rdj10149@gmail.com")
                .password("11000000")
                .nickname("ryu")
                .phone("01012123434")
                .build();

        //when
        doNothing().when(userService).saveUser(saveRequest);

        //then

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("users/create/successful", requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING)
                                .description("The user's email address"),
                        fieldWithPath("password").type(JsonFieldType.STRING)
                                .description("The user's password"),
                        fieldWithPath("nickname").type(JsonFieldType.STRING)
                                .description("The user's nickname"),
                        fieldWithPath("phone").type(JsonFieldType.STRING).description("The user's phone")
                )));

        System.out.println(objectMapper.writeValueAsString(saveRequest));
    }

    @Test
    @DisplayName(value = "???????????? - ????????? ????????? ?????? ????????? ???????????? ??????????????? ????????????.")
    void createUser_failure() throws Exception {
        //given
        SaveRequest saveRequest = SaveRequest.builder()
                .email("rdj10149@gmail.com")
                .password("11000000")
                .nickname("ryu")
                .phone("01012123434")
                .build();

        //when
        doThrow(new DuplicateEmailException()).when(userService).saveUser(any());

        //then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveRequest)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andDo(document("users/create/failure", requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING)
                                .description("???????????? ????????? ?????????"),
                        fieldWithPath("password").type(JsonFieldType.STRING)
                                .description("8??? ?????? 20??? ????????? ????????????"),
                        fieldWithPath("nickname").type(JsonFieldType.STRING)
                                .description("?????????"),
                        fieldWithPath("phone").type(JsonFieldType.STRING)
                                .description("????????? ??????")
                )));

    }

    /**
     * pathParameters??? ??????????????? MockMvcBuilders ?????? RestDocumentationRequestBuilders??? ???????????? ?????? ????????? ??????.
     * @throws Exception
     */
    @Test
    @DisplayName("????????? ?????? ?????? - ?????? ????????? ?????????")
    void DuplicateEmailCheck_successful() throws Exception {
        String email = "rdj10149@gmail.com";
        given(userService.emailDuplicateCheck(email)).willReturn(false);
        mockMvc.perform(RestDocumentationRequestBuilders.get("/users/user-emails/{email}/exists", email))
                .andExpect(status().isOk())
                .andExpect(content().string("false"))
                .andDo(document("users/duplicateEmail/successful",
                        pathParameters(
                                parameterWithName("email").description("?????????"))));
    }

    @Test
    @DisplayName("????????? ?????? ?????? - ????????? ?????????")
    void DuplicateEmailCheck_failure() throws Exception {
        String email = "rdj10149@gmail.com";
        given(userService.emailDuplicateCheck(email)).willReturn(true);
        mockMvc.perform(RestDocumentationRequestBuilders.get("/users/user-emails/{email}/exists", email))
                .andExpect(status().isOk())
                .andExpect(content().string("true"))
                .andDo(document("users/duplicateEmail/failure", pathParameters(parameterWithName("email").description("?????????"))));
    }


    @Test
    @DisplayName(value = "????????? ?????? ?????? - ?????? ??????")
    void DuplicateNicknameCheck_successful()throws Exception{
        //given
        String nickname ="ryu";
        //when
        given(userService.nicknameDuplicateCheck(nickname)).willReturn(false);

        //then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/users/user-nicknames/{nickname}/exists",nickname))
                .andExpect(status().isOk())
                .andExpect(content().string("false"))
                .andDo(document("users/duplicateNickname/successful",
                        pathParameters(
                                parameterWithName("nickname").description("????????? "))));

    }

    @Test
    @DisplayName(value = "????????? ?????? ?????? - ????????? ??????")
    void DuplicateNickname_failure()throws Exception{
        //given
        String nickname ="ryu";
        //when
        given(userService.nicknameDuplicateCheck(nickname)).willReturn(true);

        //then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/users/user-nicknames/{nickname}/exists",nickname))
                .andExpect(status().isOk())
                .andExpect(content().string("true"))
                .andDo(document("users/duplicateNickname/failure",
                        pathParameters(
                                parameterWithName("nickname").description("?????????"))));


    }

    @Test
    @DisplayName(value = "???????????? ????????? ????????? ?????? ????????? ????????????.")
    void sendSMS()throws Exception{
        //given
        UserDto.SmsCertificationRequest request = UserDto.SmsCertificationRequest.builder()
                .phone("01022334455")
                .certificationNumber("null")
                .build();
        //when
        String phone = request.getPhone();
        doNothing().when(smsCertificationService).sendSms(phone);

        //then
        mockMvc.perform(post("/users/sms-certification/sends")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("users/certification/sms/send",requestFields(
                        fieldWithPath("phone").type(JsonFieldType.STRING).description("??????????????? ?????? ?????????"),
                        fieldWithPath("certificationNumber").type(null)
                                .description("null: ?????? ?????? ????????? ???????????? ?????? ???")

                )));

    }

    @Test
    @DisplayName(value = "????????? ?????? - ??????????????? ???????????? ????????? ????????? ????????????.")
    void smsCertification_successful()throws Exception{
        //given
        UserDto.SmsCertificationRequest request = UserDto.SmsCertificationRequest.builder()
                .phone("01011223344")
                .certificationNumber("12345")
                .build();

        //when
        doNothing().when(smsCertificationService).verifySms(request);

        //then
        mockMvc.perform(post("/users/sms-certification/confirms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("users/certification/sms/successful",requestFields(
                        fieldWithPath("phone").type(JsonFieldType.STRING).description("??????????????? ?????? ????????? ??????"),
                        fieldWithPath("certificationNumber").type(JsonFieldType.STRING).description("???????????? ????????? ???????????? ")
                )));

        System.out.println(objectMapper.writeValueAsString(request));
    }

    @Test
    @DisplayName(value = "????????? ?????? - ??????????????? ???????????? ????????? ????????? ????????????.")
    void smsCertification_failure()throws Exception{
        //given
        UserDto.SmsCertificationRequest request = UserDto.SmsCertificationRequest.builder()
                .phone("01011223344")
                .certificationNumber("12345")
                .build();

        //when
        doThrow(new AuthenticationNumberMismatchException("?????? ????????? ???????????? ????????????."))
                .when(smsCertificationService).verifySms(any());

        //then
        mockMvc.perform(post("/users/sms-certification/confirms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("users/certification/sms/failure",requestFields(
                        fieldWithPath("phone").type(JsonFieldType.STRING).description("??????????????? ?????? ????????? ??????"),
                        fieldWithPath("certificationNumber").type(JsonFieldType.STRING).description("???????????? ????????? ???????????? ")
                )));

        System.out.println(objectMapper.writeValueAsString(request));
    }

    @Test
    @DisplayName(value = "????????? ??????  - ??????????????? ????????? ??????????????? ?????? ????????? ???????????? ??????????????? ????????? ????????????.")
    void emailTokenCertification_successful()throws Exception{
        //given
        String token = UUID.randomUUID().toString();
        String email = "rdj10149@gmail.com";

        //when
        doNothing().when(userService).updateEmailVerified(token,email);

        //then
        mockMvc.perform(get("/users/email-check-token")
                .param("token",token)
                .param("email",email))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("users/emailAuth/successful", requestParameters(
                        parameterWithName("token").description("??????????????? ???????????? ??????"),
                        parameterWithName("email").description("??????????????? ????????? ?????????")
                )));

    }

    @Test
    @DisplayName("????????? ?????? - 24?????? ?????? ????????? ????????? ???????????? ????????? ?????? ????????? ???????????? ????????? ????????? ????????????.")
    void emailTokenCertification_failure()throws Exception{
        //given
        String token = UUID.randomUUID().toString();
        String email = "rdj10149@gmail.com";

        //when
        doThrow(new TokenExpiredException("?????? ????????? ?????????????????????."))
                .when(userService)
                .updateEmailVerified(token,email);

        //then
        mockMvc.perform(get("/users/email-check-token")
                .param("token",token)
                .param("email",email))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andDo(document("users/emailAuth/failure",requestParameters(
                        parameterWithName("token").description("??????????????? ???????????? ?????? ??????"),
                        parameterWithName("email").description("??????????????? ????????? ?????????")
                )));

    }

    @Test
    @DisplayName("????????? ?????? ????????? ????????? ??????.")
    void resendEmailToken()throws Exception{
        //given
        String email = "test123@test.com";
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(USER_ID,email);

        //when
        doNothing().when(emailCertificationService).sendEmailForCertification(email);

        //then
        mockMvc.perform(post("/users/resend-email-token")
                .session(session))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("users/emailAuth/resend"));

    }

    @Test
    @DisplayName("????????? - ????????? ID??? ???????????? ???????????? ????????? ???????????? ????????????.")
    void login_successful() throws Exception{
        UserDto.LoginRequest request = UserDto.LoginRequest.builder()
                .email("test11@naver.com")
                .password("test1234")
                .build();

        doNothing().when(sessionLoginService).login(request);


        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("users/login/successful",requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING)
                                .description("login ID(email)"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("password")
                )));
    }
    @Test
    @DisplayName("????????? - ???????????? ?????? id ?????? ???????????? ???????????? ???????????? ????????????.")
    void login_failure()throws Exception{
        UserDto.LoginRequest request = UserDto.LoginRequest.builder()
                .email("test11@naver.com")
                .password("test1234")
                .build();

        doThrow(new UserNotFoundException("????????? ?????? ??????????????? ???????????? ????????????."))
                .when(sessionLoginService).login(any());

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andDo(document("users/login/failure",requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING)
                                .description("login ID(email)"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("password")
                )));
    }

    @Test
    @DisplayName("????????????- ??????????????? ????????????.")
    void logout()throws Exception{
        //given
        //when
        doNothing().when(sessionLoginService).logout();

        //then
        mockMvc.perform(delete("/users/logout"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("users/logout"));

    }

    @Test
    @DisplayName("??????????????? ??????")
    void myPage()throws Exception{
        //given
        UserDto.UserInfoDto userInfoDto = UserDto.UserInfoDto.builder()
                .email("rdj1022@naver.com")
                .nickname("121212")
                .phone("01012345678")
                .userLevel(UserLevel.UNAUTH)
                .build();
        //when
        given(sessionLoginService.getCurrentUser(any())).willReturn(userInfoDto);

        //then

        mockMvc.perform(get("/users/my-infos")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("users/my-infos",
                        responseFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("?????? ????????? "),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("?????? ????????? "),
                                fieldWithPath("phone").type(JsonFieldType.STRING).description("?????? ???????????? "),
                                fieldWithPath("userLevel").type(JsonFieldType.STRING)
                                        .description("????????? ?????? ??????")

                        )));


    }

    @Test
    @DisplayName("???????????? ?????? - ???????????? email ????????? ??????????????? ?????? ?????? ?????????(email.phone)??? ????????????.")
    void getUserResource_successful()throws Exception{
        //given
        UserDto.FindUserResponse response = UserDto.FindUserResponse.builder()
                .email("test123@test.com")
                .phone("01012334444")
                .build();

        String email = "test123@test.com";
        //when
        given(userService.getUserResource(any())).willReturn(response);

        //then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/users/find/{email}", email))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("users/forgetPassword/resource/successful", responseFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("phone").type(JsonFieldType.STRING).description("?????? ????????? ??????")
                        ),
                        pathParameters(
                                parameterWithName("email").description("?????????")
                        )
                ));
    }

    @Test
    @DisplayName("???????????? ?????? - ???????????? ?????? email ????????? ??????????????? ?????? ?????? ?????????(email,phone) ????????? ????????????.")
    void getUserResource_failure()throws Exception{
        //given
        String email = "test123@test.com";

        //when
        doThrow(new UserNotFoundException("???????????? ?????? ??????????????????.")).when(userService)
                .getUserResource(any());

        //then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/users/find/{email}",email))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andDo(document("users/forgetPassword/resource/failure", pathParameters(
                        parameterWithName("email").description("?????????")
                )));


    }
    @Test
    @DisplayName("???????????? ?????? - ???????????? ???????????? email ????????? ???????????? ???????????? ??????????????? ????????????.")
    void sendEmailCertification()throws Exception{
        //given
        UserDto.EmailCertificationRequest request = UserDto.EmailCertificationRequest.builder()
                .certificationNumber(null)
                .email("rdj1014@naver.com")
                .build();
        String email = request.getEmail();

        //when
        doNothing().when(emailCertificationService).sendEmailForCertification(email);

        //then
        mockMvc.perform(post("/users/email-certification/sends")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("users/certification/email/send", requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING).description("???????????? ?????????"),
                        fieldWithPath("certificationNumber").type(JsonFieldType.NULL).description("null: ?????? ????????? ????????? ???????????? ?????? ???")
                )));

    }

    @Test
    @DisplayName("???????????? ?????? - ??????????????? ???????????? ????????? ????????? ????????????.")
    void emailCertification_successful()throws Exception{
        //given
        UserDto.EmailCertificationRequest request = UserDto.EmailCertificationRequest.builder()
                .certificationNumber("12345")
                .email("rdj1014@naver.com")
                .build();

        //when
        doNothing().when(emailCertificationService).verifyEmail(request);

        //then
        mockMvc.perform(post("/users/email-certification/confirms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("users/certification/email/successful",requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING)
                                .description("??????????????? ????????? ????????? ?????????"),
                        fieldWithPath("certificationNumber").type(JsonFieldType.STRING)
                                .description("???????????? ????????? ????????????")

                )));


    }
    @Test
    @DisplayName("???????????? ?????? - ??????????????? ???????????? ????????? ????????? ????????? ????????????.")
    void  emailCertification_failure()throws Exception{
        //given
        UserDto.EmailCertificationRequest request = UserDto.EmailCertificationRequest.builder()
                .certificationNumber("12345")
                .email("rdj1014@naver.com")
                .build();

        //when
        doThrow(new AuthenticationNumberMismatchException("??????????????? ???????????? ????????????."))
                .when(emailCertificationService).verifyEmail(any());

        //then
        mockMvc.perform(post("/users/email-certification/confirms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("users/certification/email/failure",requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING)
                                .description("??????????????? ????????? ????????? ?????????"),
                        fieldWithPath("certificationNumber").type(JsonFieldType.STRING)
                                .description("???????????? ????????? ????????????")
                )));

    }
    @Test
    @DisplayName("???????????? ?????? - ????????? ???????????? ??????????????? ????????????.")
    void changePasswordByForget()throws Exception{
        //given
        UserDto.ChangePasswordRequest request = UserDto.ChangePasswordRequest.builder()
                .email("rdj1014@naver.com")
                .passwordAfter("test1231")
                .passwordBefore(null)
                .build();

        //when
        doNothing().when(userService).updatePasswordByForget(request);

        //then

        mockMvc.perform(patch("/users/forget/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("users/forgetPassword/updatePassword", requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING)
                                .description("??????????????? ????????? ?????? ID(email)"),
                        fieldWithPath("passwordAfter").type(JsonFieldType.STRING).description("????????? ????????????"),
                        fieldWithPath("passwordBefore").type(JsonFieldType.NULL)
                                .description("null : ???????????? ??????????????? ?????? ??????????????? ??? ??? ??????")
                )))
                ;
    }

    @Test
    @DisplayName("?????? ?????? - ??????????????? ???????????? ?????? ????????? ????????????.")
    void UserWithdrawal_successful()throws Exception{
        //given
        UserDto.PasswordRequest request = UserDto.PasswordRequest.builder()
                .password("test121213")
                .build();
        String email = "rdj1014@naver.com";
        String password = request.getPassword();

        //when
        doNothing().when(userService).delete(email,password);
        doNothing().when(sessionLoginService).logout();

        //then
        mockMvc.perform(delete("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("users/withdrawal/successful",requestFields(
                        fieldWithPath("password").type(JsonFieldType.STRING).description("?????? ????????????")
                )));

    }

    @Test
    @DisplayName("?????? ?????? - ??????????????? ?????? ?????? ????????? ?????? ????????? ????????????.")
    void UserWithdrawal_failure()throws Exception{
        //given
        UserDto.PasswordRequest request = UserDto.PasswordRequest.builder()
                .password("test121213")
                .build();

        //when
        doThrow(new WrongPasswordException()).when(userService).delete(any(),any());
        doNothing().when(sessionLoginService).logout();

        //then
        mockMvc.perform(delete("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andDo(document("users/withdrawal/failure",requestFields(
                        fieldWithPath("password").type(JsonFieldType.STRING).description("?????? ????????????")
                )));

    }
    @Test
    @DisplayName("???????????? ?????? - ?????? ??????????????? ???????????? ???????????? ????????? ????????????.")
    void changePassword_successful() throws Exception {
        //given
        UserDto.ChangePasswordRequest request = UserDto.ChangePasswordRequest.builder()
                .email("rdj1014@naver.com")
                .passwordAfter("12213132")
                .passwordBefore("10000000")
                .build();
        String currentUser = "rdj1014@naver.com";

        //when
        doNothing().when(userService).updatePassword(currentUser,request);

        //then
        mockMvc.perform(patch("/users/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("users/changeUserInfo/password/successful",requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING).description("??????????????? ????????? ????????? ?????? ID"),
                        fieldWithPath("passwordAfter").type(JsonFieldType.STRING).description("????????? ????????????"),
                        fieldWithPath("passwordBefore").type(JsonFieldType.STRING).description("?????? ????????????")
                )));

    }
    @Test
    @DisplayName("???????????? ?????? - ?????? ??????????????? ???????????? ????????? ???????????? ????????? ????????????.")
    void changePassword_failure()throws Exception{
        //given
        UserDto.ChangePasswordRequest request = UserDto.ChangePasswordRequest.builder()
                .email("rdj1014@naver.com")
                .passwordAfter("12213132")
                .passwordBefore("newPassword")
                .build();

        //when
        doThrow(new WrongPasswordException()).when(userService)
                .updatePassword(any(),any());

        //then
        mockMvc.perform(patch("/users/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andDo(document("users/changeUserInfo/password/failure",requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING).description("??????????????? ????????? ????????? ?????? ID"),
                        fieldWithPath("passwordAfter").type(JsonFieldType.STRING).description("????????? ????????????"),
                        fieldWithPath("passwordBefore").type(JsonFieldType.STRING).description("?????? ????????????")
                )));

    }

    @Test
    @DisplayName("?????? ?????? - ?????? ????????? ??????/????????????.")
    void changeAccount() throws Exception {
        //given
        Account account = new Account("SC","12121212","?????????");
        String currentUser = "rdj1014@naver.com";

        //when
        doNothing().when(userService).updateAccount(currentUser,account);

        //then

        mockMvc.perform(patch("/users/account")
                .content(objectMapper.writeValueAsString(account))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("users/changeUserInfo/account/change",requestFields(
                        fieldWithPath("bankName").type(JsonFieldType.STRING).description("?????????"),
                        fieldWithPath("accountNumber").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("depositor").type(JsonFieldType.STRING).description("?????????")
                )));


    }


    @Test
    @DisplayName("?????? ?????? - USER??? ?????? ?????? ????????? ????????????.")
    void getAccountResource() throws Exception {
        //given
        Account account = new Account("SC","12121212","?????????");

        //when
        given(userService.getAccount(any())).willReturn(account);

        //then
        mockMvc.perform(get("/users/account")
                        .content(objectMapper.writeValueAsString(account))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("users/changeUserInfo/account/Resource",responseFields(
                        fieldWithPath("bankName").type(JsonFieldType.STRING).description("?????????"),
                        fieldWithPath("accountNumber").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("depositor").type(JsonFieldType.STRING).description("?????????")
                )));
    }


    @Test
    @DisplayName("????????? ?????? - ???????????? ???????????? 7?????? ???????????????, ????????? ?????? ????????? ???????????? ????????? ????????? ????????????.")
    void changeNickname_successful() throws Exception{
        //given
        SaveRequest request = SaveRequest.builder()
                .email(null)
                .password(null)
                .phone(null)
                .nickname("newNickname")
                .build();
        String currentUser = "rdj1014@naver.com";

        //when
        doNothing().when(userService).updateNickname(currentUser,request);

        //then
        mockMvc.perform(patch("/users/nickname")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("users/changeUserInfo/nickname/changeSuccessful", requestFields(
                        fieldWithPath("email").type(JsonFieldType.NULL)
                                .description("null : ????????? ????????? ???????????? ?????? ???"),
                        fieldWithPath("password").type(JsonFieldType.NULL)
                                .description("null : ????????? ????????? ???????????? ?????? ???"),
                        fieldWithPath("phone").type(JsonFieldType.NULL)
                                .description("null : ????????? ????????? ???????????? ?????? ???"),
                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("????????? ?????????")
                )));
    }

    @Test
    @DisplayName("????????? ?????? - ???????????? ???????????? 7?????? ???????????? ???????????? ????????? ????????? ????????????.")
    void changeNickname_failure() throws Exception {
        //given
        SaveRequest request = SaveRequest.builder()
                .email(null)
                .password(null)
                .phone(null)
                .nickname("newNickname")
                .build();

        //when
        doThrow(new UnableToChangeNicknameException("???????????? 7?????? ????????? ?????????????????????."))
                .when(userService).updateNickname(any(),any());

        //then
        mockMvc.perform(patch("/users/nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("users/changeUserInfo/nickname/changeFailure", requestFields(
                        fieldWithPath("email").type(JsonFieldType.NULL)
                                .description("null : ????????? ????????? ???????????? ?????? ???"),
                        fieldWithPath("password").type(JsonFieldType.NULL)
                                .description("null : ????????? ????????? ???????????? ?????? ???"),
                        fieldWithPath("phone").type(JsonFieldType.NULL)
                                .description("null : ????????? ????????? ???????????? ?????? ???"),
                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("????????? ?????????")
                )));
    }

    @Test
    @DisplayName("????????? - ???????????? ????????? ????????????.")
    void addAddressBook() throws Exception {
        //given
        AddressDto.SaveRequest requestDto = AddressDto.SaveRequest.builder()
                .id(1L)
                .addressName("??? ???")
                .roadAddress("????????? 123")
                .detailAddress("789??? 123???")
                .postalCode("23456")
                .build();
        String currentUser = "rdj1014@naver.com";

        //when
        doNothing().when(userService).addAddress(currentUser,requestDto);

        //then
        mockMvc.perform(post("/users/addressBook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("users/changeUserInfo/addressBook/add/successful", requestFields(
                        fieldWithPath("id").ignored(),
                        fieldWithPath("addressName").type(JsonFieldType.STRING).description("????????? ??????"),
                        fieldWithPath("roadAddress").type(JsonFieldType.STRING).description("????????? ??????"),
                        fieldWithPath("detailAddress").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("postalCode").type(JsonFieldType.STRING).description("????????????")
                )));
    }

    @Test
    @DisplayName("????????? - ????????? ????????? ????????? ????????????.")
    void getAddressBook() throws Exception {
        //given
        List<Address>addressList = new ArrayList<>();
        Address address = new Address(1L,"???","?????????","123","12345");
        addressList.add(address);

        //when
        given(userService.getAddressBook(any())).willReturn(addressList);

        //then
        mockMvc.perform(get("/users/addressBook")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("users/changeUserInfo/addressBook/Resource", responseFields(
                        fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("?????? ID[PK]"),
                        fieldWithPath("[].addressName").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("[].roadAddress").type(JsonFieldType.STRING)
                                .description("????????? ??????"),
                        fieldWithPath("[].detailAddress").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("[].postalCode").type(JsonFieldType.STRING).description("?????? ??????")
                )));

    }

    @Test
    @DisplayName("????????? - ???????????? ????????????.")
    void deleteAddressBook() throws Exception {
        //given
        String currentUser = "rdj1014@naver.com";
        AddressDto.IdRequest idRequest = AddressDto.IdRequest.builder()
                .id(2L)
                .build();

        //when
        doNothing().when(userService).deleteAddress(currentUser,idRequest);

        //then
        mockMvc.perform(delete("/users/addressBook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(idRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("users/changeUserInfo/addressBook/delete", requestFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("????????? ????????? ID[PK]")
                )));
    }

    @Test
    @DisplayName("????????? - ???????????? ?????? ?????? ??? ????????? ????????????.")
    void updateAddressBook() throws Exception {
        //given
        AddressDto.SaveRequest saveRequest = AddressDto.SaveRequest.builder()
                .id(1L)
                .addressName("??? ???")
                .roadAddress("????????? 123")
                .detailAddress("789??? 123???")
                .postalCode("23456")
                .build();
        //when
        doNothing().when(userService).updateAddress(saveRequest);

        //then
        mockMvc.perform(patch("/users/addressBook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(saveRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("users/changeUserInfo/addressBook/update",requestFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("ID"),
                        fieldWithPath("addressName").type(JsonFieldType.STRING).description("????????? ??????"),
                        fieldWithPath("roadAddress").type(JsonFieldType.STRING).description("????????? ??????"),
                        fieldWithPath("detailAddress").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("postalCode").type(JsonFieldType.STRING).description("????????????")
                )));
    }
}