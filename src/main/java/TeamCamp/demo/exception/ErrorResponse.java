package TeamCamp.demo.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse {

    private HttpStatus status;
    private String message;

    @Builder
    public ErrorResponse(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
