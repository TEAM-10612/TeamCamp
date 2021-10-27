package TeamCamp.demo.exception.user;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException (String message){
        super(message);
    }
}
