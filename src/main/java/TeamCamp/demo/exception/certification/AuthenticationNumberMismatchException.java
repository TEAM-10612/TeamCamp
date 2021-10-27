package TeamCamp.demo.exception.certification;

public class AuthenticationNumberMismatchException extends RuntimeException {
    public AuthenticationNumberMismatchException(String message){
        super(message);
    }
}
