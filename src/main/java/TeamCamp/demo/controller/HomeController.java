package TeamCamp.demo.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class HomeController {

    @GetMapping
    public String main(){
        return "index";
    }



    @GetMapping("/product")
    public String product()
    {
        return "product";
    }


    @GetMapping("/joinuser")
    public String signUp(){
        return "joinuser";
    }
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
