package TeamCamp.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaymentViewController {

    @GetMapping("/point/charge")
    public String paymentPage(){
        return "payment";
    }
}
