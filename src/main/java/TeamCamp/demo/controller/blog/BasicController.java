package TeamCamp.demo.controller.blog;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import TeamCamp.demo.domain.model.users.user.User;

import java.nio.charset.Charset;

@RestController
public class BasicController {

    @GetMapping("/test/{id}")
    public ResponseEntity<Message> getUser(@PathVariable Long id){
        User user = User.builder()
                .email("r213213@Test.com")
                .nickname("aa")
                .phone("00011111111")
                .build();
        Message message = new Message();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application","json", Charset.forName("UTF-8")));

        message.setStatus(Status.OK);
        message.setMessage("성공ㅋ");
        message.setData(user);
        return new ResponseEntity<>(message,httpHeaders,HttpStatus.OK);
    }
}
