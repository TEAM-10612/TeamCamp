package TeamCamp.demo.util.certification.sms;

import lombok.Setter;

@Setter
public class SmsMessageTemplate {
    public String buildCertificationNumber(String certificationNumber){
        StringBuilder sb = new StringBuilder();
        sb.append("[camp-Share] 인증번호는 ");
        sb.append(certificationNumber);
        sb.append("입니다. ");

        return sb.toString();
    }
}
