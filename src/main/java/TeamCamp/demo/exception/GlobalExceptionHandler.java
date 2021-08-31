package TeamCamp.demo.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import TeamCamp.demo.exception.certification.AuthenticationNumberMismatchException;
import TeamCamp.demo.exception.product.ProductNotFoundException;
import TeamCamp.demo.exception.user.*;

import java.time.LocalDateTime;

import static TeamCamp.demo.util.ResponseConstants.BAD_REQUEST;
import static TeamCamp.demo.util.ResponseConstants.DUPLICATION_EMAIL;
import static TeamCamp.demo.util.ResponseConstants.DUPLICATION_NICKNAME;
import static TeamCamp.demo.util.ResponseConstants.FAIL_TO_CHANGE_NICKNAME;
import static TeamCamp.demo.util.ResponseConstants.ILLEGAL_MIME_TYPE;
import static TeamCamp.demo.util.ResponseConstants.NOT_AUTHORIZED;
import static TeamCamp.demo.util.ResponseConstants.PRODUCT_NOT_FOUND;
import static TeamCamp.demo.util.ResponseConstants.TOKEN_EXPIRED;
import static TeamCamp.demo.util.ResponseConstants.UNAUTHORIZED_USER;
import static TeamCamp.demo.util.ResponseConstants.USER_NOT_FOUND;
import static TeamCamp.demo.util.ResponseConstants.WRONG_PASSWORD;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DuplicateEmailException.class)
    protected final ResponseEntity<String> duplicateEmailException(
            DuplicateEmailException ex, WebRequest request) {
        log.debug("Duplicate email :: {}, detection time = {}",request.getDescription(false));
        return DUPLICATION_EMAIL;
    }

    @ExceptionHandler(DuplicateNicknameException.class)
    protected final ResponseEntity<String> duplicateNicknameException(
            DuplicateNicknameException ex, WebRequest request) {
        log.debug("Duplicate nickname :: {}, detection time = {}",request.getDescription(false));
        return DUPLICATION_NICKNAME;
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<String> methodArgumentNotValidException(
            MethodArgumentNotValidException ex) {
        log.error(ex.getFieldError().getDefaultMessage(),ex);
        return new ResponseEntity<>(ex.getFieldError().getDefaultMessage(),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<String> handleUserNotFoundException(
            UserNotFoundException ex, WebRequest request) {
        log.debug("로그인 실패 : 존재하지 않는 ID 또는 패스워드 불일치",request.getDescription(false));
        return USER_NOT_FOUND;
    }

    @ExceptionHandler(UnauthenticatedUserException.class)
    public final ResponseEntity<String> handleUnauthenticatedUserException(
            UnauthenticatedUserException ex, WebRequest request) {
        log.error("Failed to Execution ::  {}, detection time={} ", request.getDescription(false),
                LocalDateTime.now(), ex);
        return UNAUTHORIZED_USER;
    }

    @ExceptionHandler(AuthenticationNumberMismatchException.class)
    public final ResponseEntity<Void>handleAuthenticationNumberMismatchException(
            AuthenticationNumberMismatchException ex){
        log.debug("인증번호 불일치",ex);
        return BAD_REQUEST;
    }


    @ExceptionHandler(WrongPasswordException.class)
    public final ResponseEntity<String> handleWrongPasswordException(
            WrongPasswordException ex,WebRequest request){
        log.debug("Wrong_password :: {}, detection time = {}",request.getDescription(false));
        return WRONG_PASSWORD;
    }

    @ExceptionHandler(UnableToChangeNicknameException.class)
    public final ResponseEntity<String> handleUnableToChangeNicknameException(
            UnableToChangeNicknameException ex) {
        log.error("닉네임은 7일에 한번만 변경 가능합니다.",ex);
        return FAIL_TO_CHANGE_NICKNAME;
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public final ResponseEntity<String> productNotFoundException(
            ProductNotFoundException ex){
        log.debug("존재하지 않는 상품입니다.",ex);
        return PRODUCT_NOT_FOUND;
    }


    @ExceptionHandler(TokenExpiredException.class)
    public final ResponseEntity handleTokenExpiredException ( TokenExpiredException ex,WebRequest request){
        log.debug("Token Expired :: {} , detection time = {}",request.getDescription(false), LocalDateTime.now(), ex);
        return TOKEN_EXPIRED;

    }

    @ExceptionHandler(NotAuthorizedException.class)
    public final ResponseEntity handleNotAuthorized(NotAuthorizedException ex,WebRequest webRequest){
        log.debug("Not Authorized :: {}, detection time ={}", webRequest.getDescription(false),
                LocalDateTime.now(), ex);
        return NOT_AUTHORIZED;
    }

    public final ResponseEntity<String>illegalMineTypeException(IllegalMineTypeException ex){
        log.debug("올바르지 않은 확장자 입니다." ,ex );
        return ILLEGAL_MIME_TYPE;
    }

}
