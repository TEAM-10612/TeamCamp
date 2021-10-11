package TeamCamp.demo.service.loginservice;

import TeamCamp.demo.exception.user.NotAuthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import TeamCamp.demo.domain.model.users.UserLevel;
import TeamCamp.demo.domain.model.users.User;
import TeamCamp.demo.dto.UserDto;
import TeamCamp.demo.domain.repository.UserRepository;
import TeamCamp.demo.encrypt.EncryptionService;
import TeamCamp.demo.exception.user.UnauthenticatedUserException;
import TeamCamp.demo.exception.user.UserNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;

import static TeamCamp.demo.util.UserConstants.AUTH_State;
import static TeamCamp.demo.util.UserConstants.USER_ID;


@Service
@RequiredArgsConstructor
public class SessionLoginService {


    private final HttpSession session;

    private final UserRepository userRepository;

    private final EncryptionService encryptionService;

    /**
     * 아이디 비밀번호 일치 여부
     * @param loginRequest
     */
    @Transactional(readOnly = true)
    public void existByEmailAndPassword(UserDto.LoginRequest loginRequest) {
        loginRequest.passwordEncryption(encryptionService);
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        if (!userRepository.existsByEmailAndPassword(email, password)) {
            throw new UserNotFoundException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }
    }
    @Transactional(readOnly = true)
    public void login(UserDto.LoginRequest request) {
        existByEmailAndPassword(request);
        String email = request.getEmail();
        setUserLevel(email);
        session.setAttribute(USER_ID,email);

    }

    public void setUserLevel(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));
        banCheck(user);

        session.setAttribute(AUTH_State,user.getUserLevel());
    }

    public void logout() {
        session.removeAttribute(USER_ID);
    }

    public String getLoginUser() {
        return (String) session.getAttribute(USER_ID);
    }


    public UserDto.UserInfoDto getCurrentUser(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthenticatedUserException("존재하지 않는 사용자 입니다.")).toUserInfoDto();
    }

    private void banCheck(User user) {
        if(user.isBan()){
            throw new NotAuthorizedException("관리자에 의해 이용이 정지된 회원입니다.");
        }
    }



    public UserLevel getUserLevel() {
        return (UserLevel) session.getAttribute(AUTH_State);
    }
}
