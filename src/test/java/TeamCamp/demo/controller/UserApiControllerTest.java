package TeamCamp.demo.controller;

import TeamCamp.demo.service.UserService;
import TeamCamp.demo.service.email.EmailCertificationService;
import TeamCamp.demo.service.loginservice.userlogin.SessionLoginService;
import TeamCamp.demo.service.sms.SmsCertificationService;
import TeamCamp.demo.dto.UserDto;
import TeamCamp.demo.dto.UserDto.SaveRequest;
import TeamCamp.demo.exception.certification.AuthenticationNumberMismatchException;
import TeamCamp.demo.exception.user.DuplicateEmailException;
import TeamCamp.demo.exception.user.TokenExpiredException;
import TeamCamp.demo.exception.user.UserNotFoundException;
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

import java.util.UUID;

import static TeamCamp.demo.util.UserConstants.USER_ID;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
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
}