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
                .name("텐트")
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
    @DisplayName("회원가입 - 유효성 검사를 통과했다면 회원가입에 성공한다.")
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
    @DisplayName(value = "회원가입 - 중복된 닉네임 또는 중복된 이메일로 회원가입에 실패한다.")
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
                                .description("로그인시 사용할 이메일"),
                        fieldWithPath("password").type(JsonFieldType.STRING)
                                .description("8자 이상 20자 이하의 비밀번호"),
                        fieldWithPath("nickname").type(JsonFieldType.STRING)
                                .description("닉네임"),
                        fieldWithPath("phone").type(JsonFieldType.STRING)
                                .description("휴대폰 번호")
                )));

    }

    /**
     * pathParameters를 사용할거면 MockMvcBuilders 보다 RestDocumentationRequestBuilders를 이용하는 것이 좋다고 한다.
     * @throws Exception
     */
    @Test
    @DisplayName("이메일 중복 검사 - 사용 가능한 이메일")
    void DuplicateEmailCheck_successful() throws Exception {
        String email = "rdj10149@gmail.com";
        given(userService.emailDuplicateCheck(email)).willReturn(false);
        mockMvc.perform(RestDocumentationRequestBuilders.get("/users/user-emails/{email}/exists", email))
                .andExpect(status().isOk())
                .andExpect(content().string("false"))
                .andDo(document("users/duplicateEmail/successful",
                        pathParameters(
                                parameterWithName("email").description("이메일"))));
    }

    @Test
    @DisplayName("이메일 중복 검사 - 중복된 이메일")
    void DuplicateEmailCheck_failure() throws Exception {
        String email = "rdj10149@gmail.com";
        given(userService.emailDuplicateCheck(email)).willReturn(true);
        mockMvc.perform(RestDocumentationRequestBuilders.get("/users/user-emails/{email}/exists", email))
                .andExpect(status().isOk())
                .andExpect(content().string("true"))
                .andDo(document("users/duplicateEmail/failure", pathParameters(parameterWithName("email").description("이메일"))));
    }


    @Test
    @DisplayName(value = "닉네임 중복 검사 - 사용 가능")
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
                                parameterWithName("nickname").description("닉네임 "))));

    }

    @Test
    @DisplayName(value = "닉네임 중복 검사 - 닉네임 중복")
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
                                parameterWithName("nickname").description("닉네임"))));


    }

    @Test
    @DisplayName(value = "사용자가 입력한 번호로 인증 문자를 전송한다.")
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
                        fieldWithPath("phone").type(JsonFieldType.STRING).description("인증번호를 받을 휴대폰"),
                        fieldWithPath("certificationNumber").type(null)
                                .description("null: 인증 번호 발송시 사용하지 않는 값")

                )));

    }

    @Test
    @DisplayName(value = "휴대폰 인증 - 인증번호가 일치하면 휴대폰 인증에 성공한다.")
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
                        fieldWithPath("phone").type(JsonFieldType.STRING).description("인증번호를 받은 휴대폰 번호"),
                        fieldWithPath("certificationNumber").type(JsonFieldType.STRING).description("사용자가 입력한 인증번호 ")
                )));

        System.out.println(objectMapper.writeValueAsString(request));
    }

    @Test
    @DisplayName(value = "휴대폰 인증 - 인증번호가 일치하지 않으면 인증에 실패한다.")
    void smsCertification_failure()throws Exception{
        //given
        UserDto.SmsCertificationRequest request = UserDto.SmsCertificationRequest.builder()
                .phone("01011223344")
                .certificationNumber("12345")
                .build();

        //when
        doThrow(new AuthenticationNumberMismatchException("인증 번호가 일치하지 않습니다."))
                .when(smsCertificationService).verifySms(any());

        //then
        mockMvc.perform(post("/users/sms-certification/confirms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("users/certification/sms/failure",requestFields(
                        fieldWithPath("phone").type(JsonFieldType.STRING).description("인증번호를 받은 휴대폰 번호"),
                        fieldWithPath("certificationNumber").type(JsonFieldType.STRING).description("사용자가 입력한 인증번호 ")
                )));

        System.out.println(objectMapper.writeValueAsString(request));
    }

    @Test
    @DisplayName(value = "이메일 인증  - 회원가입시 발송된 이메일에서 토큰 링크를 클릭하면 정상적으로 인증에 성공한다.")
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
                        parameterWithName("token").description("회원가입시 발송되는 토큰"),
                        parameterWithName("email").description("회원가입시 입력한 이메일")
                )));

    }

    @Test
    @DisplayName("이메일 인증 - 24시간 내에 이메일 인증을 완료하지 않으면 인증 토큰이 만료되어 이메일 인증에 실패한다.")
    void emailTokenCertification_failure()throws Exception{
        //given
        String token = UUID.randomUUID().toString();
        String email = "rdj10149@gmail.com";

        //when
        doThrow(new TokenExpiredException("인증 토큰이 만료되었습니다."))
                .when(userService)
                .updateEmailVerified(token,email);

        //then
        mockMvc.perform(get("/users/email-check-token")
                .param("token",token)
                .param("email",email))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andDo(document("users/emailAuth/failure",requestParameters(
                        parameterWithName("token").description("회원가입시 발송되는 랜덤 토큰"),
                        parameterWithName("email").description("회원가입시 입력한 이메일")
                )));

    }

    @Test
    @DisplayName("이메일 인증 토큰을 재전송 한다.")
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
    @DisplayName("로그인 - 등록된 ID와 일치하는 패스워드 입력시 로그인에 성공한다.")
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
    @DisplayName("로그인 - 존재하지 않는 id 또는 비밀번호 불일치시 로그인에 실패한다.")
    void login_failure()throws Exception{
        UserDto.LoginRequest request = UserDto.LoginRequest.builder()
                .email("test11@naver.com")
                .password("test1234")
                .build();

        doThrow(new UserNotFoundException("아이디 또는 비밀번호가 일치하지 않습니다."))
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
    @DisplayName("로그아웃- 로그아웃에 성공한다.")
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
    @DisplayName("마이페이지 조회")
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
                                fieldWithPath("email").type(JsonFieldType.STRING).description("회원 이메일 "),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("회원 닉네임 "),
                                fieldWithPath("phone").type(JsonFieldType.STRING).description("회원 전화번호 "),
                                fieldWithPath("userLevel").type(JsonFieldType.STRING)
                                        .description("이메일 인증 여부")

                        )));


    }

    @Test
    @DisplayName("비밀번호 찾기 - 존재하는 email 입력시 비밀번호를 찾기 위한 리소스(email.phone)을 리턴한다.")
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
                                fieldWithPath("email").type(JsonFieldType.STRING).description("회원 이메일"),
                                fieldWithPath("phone").type(JsonFieldType.STRING).description("회원 휴대폰 번호")
                        ),
                        pathParameters(
                                parameterWithName("email").description("이메일")
                        )
                ));
    }

    @Test
    @DisplayName("비밀번호 찾기 - 존재하지 않는 email 입력시 비밀번호를 찾기 위한 리소스(email,phone) 리턴에 실패한다.")
    void getUserResource_failure()throws Exception{
        //given
        String email = "test123@test.com";

        //when
        doThrow(new UserNotFoundException("존재하지 않는 이메일입니다.")).when(userService)
                .getUserResource(any());

        //then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/users/find/{email}",email))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andDo(document("users/forgetPassword/resource/failure", pathParameters(
                        parameterWithName("email").description("이메일")
                )));


    }
    @Test
    @DisplayName("비밀번호 찾기 - 비밀번호 찾기에서 email 인증을 선택하면 이메일로 인증번호가 발송된다.")
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
                        fieldWithPath("email").type(JsonFieldType.STRING).description("인증받을 이메일"),
                        fieldWithPath("certificationNumber").type(JsonFieldType.NULL).description("null: 인증 이메일 발송시 사용하지 않는 값")
                )));

    }

    @Test
    @DisplayName("비밀번호 찾기 - 인증번호가 일치하면 이메일 인증에 성공한다.")
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
                                .description("비밀번호를 찾기를 원하는 이메일"),
                        fieldWithPath("certificationNumber").type(JsonFieldType.STRING)
                                .description("사용자가 입력한 인증번호")

                )));


    }
    @Test
    @DisplayName("비밀번호 찾기 - 인증번호가 일치하지 않으면 이메일 인증에 실패한다.")
    void  emailCertification_failure()throws Exception{
        //given
        UserDto.EmailCertificationRequest request = UserDto.EmailCertificationRequest.builder()
                .certificationNumber("12345")
                .email("rdj1014@naver.com")
                .build();

        //when
        doThrow(new AuthenticationNumberMismatchException("인증번호가 일치하지 않습니다."))
                .when(emailCertificationService).verifyEmail(any());

        //then
        mockMvc.perform(post("/users/email-certification/confirms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("users/certification/email/failure",requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING)
                                .description("비밀번호를 찾기를 원하는 이메일"),
                        fieldWithPath("certificationNumber").type(JsonFieldType.STRING)
                                .description("사용자가 입력한 인증번호")
                )));

    }
    @Test
    @DisplayName("비밀번호 찾기 - 인증이 완료되면 비밀번호를 변경한다.")
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
                                .description("비밀번호를 변경할 회원 ID(email)"),
                        fieldWithPath("passwordAfter").type(JsonFieldType.STRING).description("변경할 비밀번호"),
                        fieldWithPath("passwordBefore").type(JsonFieldType.NULL)
                                .description("null : 비밀번호 찾기에서는 이전 비밀번호를 알 수 없음")
                )))
                ;
    }

    @Test
    @DisplayName("회원 탈퇴 - 비밀번호가 일치하면 회원 탈퇴가 성공한다.")
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
                        fieldWithPath("password").type(JsonFieldType.STRING).description("회원 비밀번호")
                )));

    }

    @Test
    @DisplayName("회원 탈퇴 - 비밀번호가 일치 하지 않으면 회원 탈퇴에 실패한다.")
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
                        fieldWithPath("password").type(JsonFieldType.STRING).description("회원 비밀번호")
                )));

    }
    @Test
    @DisplayName("비밀번호 변경 - 이전 비밀번호가 일치하면 비밀번호 변경에 성공한다.")
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
                        fieldWithPath("email").type(JsonFieldType.STRING).description("비밀번호를 변경을 원하는 회원 ID"),
                        fieldWithPath("passwordAfter").type(JsonFieldType.STRING).description("변경할 비밀번호"),
                        fieldWithPath("passwordBefore").type(JsonFieldType.STRING).description("이전 비밀번호")
                )));

    }
    @Test
    @DisplayName("비밀번호 변경 - 이전 비밀번호가 일치하지 않으면 비밀번호 변경에 실패한다.")
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
                        fieldWithPath("email").type(JsonFieldType.STRING).description("비밀번호를 변경을 원하는 회원 ID"),
                        fieldWithPath("passwordAfter").type(JsonFieldType.STRING).description("변경할 비밀번호"),
                        fieldWithPath("passwordBefore").type(JsonFieldType.STRING).description("이전 비밀번호")
                )));

    }

    @Test
    @DisplayName("환급 계좌 - 환급 계좌를 설정/변경한다.")
    void changeAccount() throws Exception {
        //given
        Account account = new Account("SC","12121212","류동재");
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
                        fieldWithPath("bankName").type(JsonFieldType.STRING).description("은행명"),
                        fieldWithPath("accountNumber").type(JsonFieldType.STRING).description("계좌 번호"),
                        fieldWithPath("depositor").type(JsonFieldType.STRING).description("예금주")
                )));


    }


    @Test
    @DisplayName("환급 계좌 - USER의 환급 계좌 정보를 리턴한다.")
    void getAccountResource() throws Exception {
        //given
        Account account = new Account("SC","12121212","류동재");

        //when
        given(userService.getAccount(any())).willReturn(account);

        //then
        mockMvc.perform(get("/users/account")
                        .content(objectMapper.writeValueAsString(account))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("users/changeUserInfo/account/Resource",responseFields(
                        fieldWithPath("bankName").type(JsonFieldType.STRING).description("은행명"),
                        fieldWithPath("accountNumber").type(JsonFieldType.STRING).description("계좌 번호"),
                        fieldWithPath("depositor").type(JsonFieldType.STRING).description("예금주")
                )));
    }


    @Test
    @DisplayName("닉네임 변경 - 닉네임을 변경한지 7일이 초과되었고, 닉네임 중복 검사에 통과하면 닉네임 변경에 성공한다.")
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
                                .description("null : 닉네임 변경시 사용되지 않는 값"),
                        fieldWithPath("password").type(JsonFieldType.NULL)
                                .description("null : 닉네임 변경시 사용되지 않는 값"),
                        fieldWithPath("phone").type(JsonFieldType.NULL)
                                .description("null : 닉네임 변경시 사용되지 않는 값"),
                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("새로운 닉네임")
                )));
    }

    @Test
    @DisplayName("닉네임 변경 - 닉네임을 변경한지 7일이 초과되지 않았다면 닉네임 변경에 실패한다.")
    void changeNickname_failure() throws Exception {
        //given
        SaveRequest request = SaveRequest.builder()
                .email(null)
                .password(null)
                .phone(null)
                .nickname("newNickname")
                .build();

        //when
        doThrow(new UnableToChangeNicknameException("닉네임은 7일에 한번만 변경가능합니다."))
                .when(userService).updateNickname(any(),any());

        //then
        mockMvc.perform(patch("/users/nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("users/changeUserInfo/nickname/changeFailure", requestFields(
                        fieldWithPath("email").type(JsonFieldType.NULL)
                                .description("null : 닉네임 변경시 사용되지 않는 값"),
                        fieldWithPath("password").type(JsonFieldType.NULL)
                                .description("null : 닉네임 변경시 사용되지 않는 값"),
                        fieldWithPath("phone").type(JsonFieldType.NULL)
                                .description("null : 닉네임 변경시 사용되지 않는 값"),
                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("새로운 닉네임")
                )));
    }

    @Test
    @DisplayName("주소록 - 주소록에 주소를 추가한다.")
    void addAddressBook() throws Exception {
        //given
        AddressDto.SaveRequest requestDto = AddressDto.SaveRequest.builder()
                .id(1L)
                .addressName("새 집")
                .roadAddress("새집로 123")
                .detailAddress("789동 123호")
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
                        fieldWithPath("addressName").type(JsonFieldType.STRING).description("주소록 이름"),
                        fieldWithPath("roadAddress").type(JsonFieldType.STRING).description("도로명 주소"),
                        fieldWithPath("detailAddress").type(JsonFieldType.STRING).description("상세 주소"),
                        fieldWithPath("postalCode").type(JsonFieldType.STRING).description("우편번호")
                )));
    }

    @Test
    @DisplayName("주소록 - 회원의 주소록 정보를 가져온다.")
    void getAddressBook() throws Exception {
        //given
        List<Address>addressList = new ArrayList<>();
        Address address = new Address(1L,"집","어디로","123","12345");
        addressList.add(address);

        //when
        given(userService.getAddressBook(any())).willReturn(addressList);

        //then
        mockMvc.perform(get("/users/addressBook")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("users/changeUserInfo/addressBook/Resource", responseFields(
                        fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("주소 ID[PK]"),
                        fieldWithPath("[].addressName").type(JsonFieldType.STRING).description("주소 이름"),
                        fieldWithPath("[].roadAddress").type(JsonFieldType.STRING)
                                .description("도로명 주소"),
                        fieldWithPath("[].detailAddress").type(JsonFieldType.STRING).description("상세 주소"),
                        fieldWithPath("[].postalCode").type(JsonFieldType.STRING).description("우편 번호")
                )));

    }

    @Test
    @DisplayName("주소록 - 주소록을 삭제한다.")
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
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("삭제할 주소의 ID[PK]")
                )));
    }

    @Test
    @DisplayName("주소록 - 주소록에 있는 주소 중 하나를 수정한다.")
    void updateAddressBook() throws Exception {
        //given
        AddressDto.SaveRequest saveRequest = AddressDto.SaveRequest.builder()
                .id(1L)
                .addressName("새 집")
                .roadAddress("새집로 123")
                .detailAddress("789동 123호")
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
                        fieldWithPath("addressName").type(JsonFieldType.STRING).description("주소록 이름"),
                        fieldWithPath("roadAddress").type(JsonFieldType.STRING).description("도로명 주소"),
                        fieldWithPath("detailAddress").type(JsonFieldType.STRING).description("상세 주소"),
                        fieldWithPath("postalCode").type(JsonFieldType.STRING).description("우편번호")
                )));
    }
}