package TeamCamp.demo.service.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import TeamCamp.demo.common.properties.AppProperties;
import TeamCamp.demo.dto.UserDto.EmailCertificationRequest;
import TeamCamp.demo.dao.email.EmailCertificationDao;
import TeamCamp.demo.exception.certification.AuthenticationNumberMismatchException;
import TeamCamp.demo.exception.user.TokenExpiredException;
import TeamCamp.demo.util.certification.email.EmailContentTemplate;

import java.util.UUID;

import static TeamCamp.demo.util.RandomNumberGeneration.makeRandomNumber;
import static TeamCamp.demo.util.certification.email.EmailConstants.TITLE_EMAIL_CHECK;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailCertificationService {

    private final JavaMailSender mailSender;
    private final EmailCertificationDao emailCertificationNumberDao;
    private final EmailCertificationDao emailVerificationDao;
    private final AppProperties appProperties;


    //인증번호 전송 및 저장
    public void sendEmailForCertification(String email){
        String randomNumber = makeRandomNumber();
        String content = makeEmailContent(randomNumber);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(appProperties.getEmailFromAddress());
        message.setSubject(TITLE_EMAIL_CHECK);
        message.setText(content);
        mailSender.send(message);

        emailCertificationNumberDao.createEmail(email, randomNumber);

    }

    //인증번호 확인
    public void verifyEmail(EmailCertificationRequest  request){
        if(isVerify(request)){
            throw new AuthenticationNumberMismatchException("인증번호가 일치하지 않습니다.");
        }
        emailCertificationNumberDao.removeEmailCertification(request.getEmail());
    }
    //인증번호 일치 여부 확인 내부 로직
    public boolean isVerify(EmailCertificationRequest request) {
        return !(emailCertificationNumberDao.hasKey(request.getEmail()))&&
                emailCertificationNumberDao.getEmailCertification(request.getEmail())
                        .equals(request.getCertificationNumber());
    }

    //토큰 일치 여부
    public void sendEmailForEmailCheck(String email) {

        String token = UUID.randomUUID().toString();
        String content = makeEmailContent(token,email);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(appProperties.getEmailFromAddress());
        message.setSubject(TITLE_EMAIL_CHECK);
        message.setText(content);
        mailSender.send(message);

        emailVerificationDao.createEmail(email,token);

    }

    //인증 이메일 내용 생성
    public String makeEmailContent(String certificationNumber) {
        EmailContentTemplate content = new EmailContentTemplate();
        return content.buildCertificationNumber(certificationNumber);
    }

    //이메일 확인용 내용생성
    public String makeEmailContent(String token,String email){
        EmailContentTemplate content = new EmailContentTemplate();
        return content.buildEmailCheckContent(token,email);

    }



    //토큰 일치여부 확인 내부 로직
    private boolean isVerify(String token ,String email){
        return !(emailVerificationDao.hasKey(email))&&
                emailVerificationDao.getEmailCertification(email).equals(token);
    }

    //토큰 일치여부 검사
    public void verifyEmail(String token ,String email){
        if(isVerify(token,email)){
            throw new TokenExpiredException("인증 토큰이 만료되었습니다.");
        }
        emailVerificationDao.removeEmailCertification(email);
    }

}
