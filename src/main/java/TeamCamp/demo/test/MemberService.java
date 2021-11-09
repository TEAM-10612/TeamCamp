package TeamCamp.demo.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {
    @Autowired
    private MemberRepository memberRepository;

    public List<Member> getProduct() {
        return memberRepository.findAll();
    }

    public List<Member> getProductMaster() {
        return memberRepository.findAll();
    }
}
