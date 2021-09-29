package TeamCamp.demo.common.intercepter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import TeamCamp.demo.domain.model.users.UserLevel;
import TeamCamp.demo.service.loginservice.SessionLoginService;
import TeamCamp.demo.common.annotation.LoginCheck;
import TeamCamp.demo.exception.user.NotAuthorizedException;
import TeamCamp.demo.exception.user.UnauthenticatedUserException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
@RequiredArgsConstructor
public class LoginCheckInterceptor implements HandlerInterceptor {

    private final SessionLoginService loginService;

    @Inject
    private Environment environment;


    //컨트롤러 메서드 실행되기전
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler)
            throws Exception {

//        String [] activeProfiles = environment.getActiveProfiles();
//        if(activeProfiles[0].equals("test")){
//            return true;
//        }
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            LoginCheck loginCheck = handlerMethod.getMethodAnnotation(LoginCheck.class);


            if (loginCheck == null) {
                return true;
            }


            if (loginService.getLoginUser() == null) {
                throw new UnauthenticatedUserException("로그인 후 이용 가능합니다.");
            }

            UserLevel auth = loginCheck.authority();


            switch (auth){
                case ADMIN:
                    adminUserLevel();
                    break;

                case AUTH:
                    authUserLevel();
                    break;
            }

        }
        return true;
    }
    /**
     * 현재 USER의 권한(UserLevel)이 AUTH인지 확인한다. 해당 리소스는 ADMIN과 AUTH만 접근 가능하다. 따라서 UNAUTH인 경우에만 제한한다.
     */

    private void authUserLevel() {
        UserLevel auth = loginService.getUserLevel();
        if(auth == UserLevel.UNAUTH){
            throw new NotAuthorizedException("해당 리소스에 대한 접근 권한이 존재하지 않습니다.");
        }
    }

    /**
     * 현재 USER의 권한(UserLevel)이 ADMIN인지 확인한다. ADMIN이 아니라면 해당 요청을 수행할 수 없다.
     */
    private void adminUserLevel() {
        UserLevel auth = loginService.getUserLevel();
        if(auth != UserLevel.ADMIN){
            throw new NotAuthorizedException("해당 리소스에 대한 접근 권한이 존재하지 않습니다.");
        }
    }
}

