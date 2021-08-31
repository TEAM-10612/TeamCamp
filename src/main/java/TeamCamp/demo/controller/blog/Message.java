package TeamCamp.demo.controller.blog;

import lombok.Data;

@Data
public class Message {
    private Status status;
    private String message;
    private Object data;

    public Message() {
        this.status = Status.BAD_REQUEST;
        this.data = null;
        this.message = null;
    }
}
