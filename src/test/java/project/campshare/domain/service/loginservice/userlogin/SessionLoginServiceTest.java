package project.campshare.domain.service.loginservice.userlogin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.campshare.domain.model.users.user.User;
import project.campshare.dto.UserDto.LoginRequest;
import project.campshare.dto.UserDto.SaveRequest;
import project.campshare.dto.UserDto.UserInfoDto;
import project.campshare.domain.repository.UserRepository;
import project.campshare.encrypt.EncryptionService;
import project.campshare.exception.user.UserNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionLoginServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    EncryptionService encryptionService;

    @InjectMocks
    SessionLoginService sessionLoginService;

    private User user;

    private SaveRequest saveRequest;

    @BeforeEach
    void setUp() {
        saveRequest = SaveRequest.builder()
                .email("test@test.com")
                .password("kkyykk")
                .nickname("17171771")
                .phone("01077778888")
                .build();

        user = saveRequest.toEntity();
    }

    public LoginRequest createLoginDto() {
        return LoginRequest.builder()
                .email("test@test.com")
                .password("test1234")
                .build();

    }
    @Test
    @DisplayName("로그인 성공 : 아이디와 비밀번호 일치")
    void LoginSucceed()throws Exception{
       LoginRequest loginRequest = createLoginDto();
       when(userRepository.existsByEmailAndPassword(loginRequest.getEmail(),
               encryptionService.encrypt(loginRequest.getPassword())))
               .thenReturn(true);

       sessionLoginService.existByEmailAndPassword(loginRequest);

       verify(userRepository,atLeastOnce()).existsByEmailAndPassword(loginRequest.getEmail(),encryptionService.encrypt(loginRequest.getPassword()));

    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호가 일치하지 않거나 존재하지 않는 ID를 요청할 경우 UserNotFoundException이 발생한다.")
    public void LoginFail() {
        LoginRequest loginRequest = createLoginDto();

        when(userRepository.existsByEmailAndPassword(loginRequest.getEmail(),
                encryptionService.encrypt(loginRequest.getPassword())))
                .thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> sessionLoginService
                        .existByEmailAndPassword(loginRequest));

        verify(userRepository, atLeastOnce())
                .existsByEmailAndPassword(loginRequest.getEmail(),
                        encryptionService.encrypt(loginRequest.getPassword()));
    }

    @Test
    @DisplayName("내 정보 - 로그인 한 상태에서 my-infos를 요청하면 정상적으로 내 정보가 리턴된다.")
    public void getMyInfo_Success() {

        LoginRequest loginRequest = createLoginDto();

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(java.util.Optional.ofNullable(user));

        UserInfoDto userInfoDto = sessionLoginService.getCurrentUser(loginRequest.getEmail());

        assertThat(userInfoDto).isNotNull();
        assertThat(userInfoDto.getEmail()).isEqualTo(user.getEmail());
        assertThat(userInfoDto.getNickname()).isEqualTo(user.getNickname());

        verify(userRepository, atLeastOnce())
                .findByEmail(any());
    }

}