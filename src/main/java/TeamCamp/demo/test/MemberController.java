package TeamCamp.demo.test;


import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @GetMapping("")
    public ResponseEntity<?> getMember(){
        List<Member> members = memberService.getProduct();
        return new ResponseEntity<>(members, HttpStatus.OK);
    }

    @GetMapping("master")
    public ResponseEntity<?> getMemberFromMaster(){
        List<Member> members = memberService.getProductMaster();
        return new ResponseEntity<>(members,HttpStatus.OK);
    }
}
