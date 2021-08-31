package TeamCamp.demo.exception.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import TeamCamp.demo.exception.certification.smscertification.SmsSendFailedException;

import java.time.LocalDateTime;

import static TeamCamp.demo.util.ResponseConstants.BAD_REQUEST;
import static TeamCamp.demo.util.ResponseConstants.DUPLICATION_EMAIL;
import static TeamCamp.demo.util.ResponseConstants.DUPLICATION_NICKNAME;


@Slf4j
@RestControllerAdvice
public class CustomizeResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    //xception이 발생한 시점과 발생한 url을 log로 출력하여 exception이 발생한 시간과 url, 원인을 알 수 있도록 구현
    //   (리턴되는 body에는 exception message만 전달하도록 수정)
    @ExceptionHandler(DuplicateEmailException.class)
    public final ResponseEntity<String> handleEmailDuplicateException(DuplicateEmailException ex, WebRequest request){
        log.error("failed to signUp :: {}, detection time ={}",request.getDescription(false),
                LocalDateTime.now(), ex);
        return DUPLICATION_EMAIL;

    }

    @ExceptionHandler(DuplicateNicknameException.class)
    public final ResponseEntity<String> handleNickNameDuplicateException(DuplicateNicknameException ex,WebRequest request){
        log.error("failed to signUp :: {} , detection time ={} ", request.getDescription(false),
                LocalDateTime.now(),ex);
        return DUPLICATION_NICKNAME;
    }

    @ExceptionHandler(SmsSendFailedException.class)
    public final ResponseEntity handleFailedToSendMessageException(){
        return BAD_REQUEST;
    }
}