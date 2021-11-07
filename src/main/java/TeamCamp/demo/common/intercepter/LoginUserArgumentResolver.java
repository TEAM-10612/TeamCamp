package TeamCamp.demo.common.intercepter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import TeamCamp.demo.service.login.SessionLoginService;
import TeamCamp.demo.common.annotation.CurrentUser;

@Component
@RequiredArgsConstructor
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final SessionLoginService loginService;

    /**
     * 현재 파라미터를 리졸버가 지원할지 true/false로 반환한다.
     * 해당 메서드가 참이면 resolveArgument()를 반환한다.
     * @param parameter
     * @return
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return loginService.getLoginUser();
    }
}
