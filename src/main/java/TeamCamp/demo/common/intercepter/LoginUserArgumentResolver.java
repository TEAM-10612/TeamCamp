package TeamCamp.demo.common.intercepter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import TeamCamp.demo.service.loginservice.SessionLoginService;
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

    /**
     *  hasParameterAnnotation 메소드를 사용하여 해당 메소드에 CurrentUser라는 어노테이션이 존재하는지 확인한다.
     * @param parameter
     * @param modelAndViewContainer
     * @param nativeWebRequest
     * @param webDataBinderFactory
     * @return resolveArgument() : 실제 바인딩할 객체를 반환한다.
     * @throws Exception
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        return loginService.getLoginUser();
    }
}
