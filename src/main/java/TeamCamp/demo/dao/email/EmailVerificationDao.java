package TeamCamp.demo.dao.email;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

import static TeamCamp.demo.util.certification.email.EmailConstants.LIMIT_TIME_EMAIL_VALIDATION;
import static TeamCamp.demo.util.certification.email.EmailConstants.PREFIX_VERIFICATION;

@RequiredArgsConstructor
@Repository
public class EmailVerificationDao implements EmailCertificationDao{
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void createEmail(String email, String token) {
        stringRedisTemplate.opsForValue().set(PREFIX_VERIFICATION + email , token,
                Duration.ofSeconds(LIMIT_TIME_EMAIL_VALIDATION));
    }

    @Override
    public String getEmailCertification(String email) {
        return stringRedisTemplate.opsForValue().get(PREFIX_VERIFICATION +email);
    }

    @Override
    public void removeEmailCertification(String email) {
        stringRedisTemplate.delete(PREFIX_VERIFICATION + email);
    }

    @Override
    public boolean hasKey(String email) {
        return stringRedisTemplate.hasKey(PREFIX_VERIFICATION + email);
    }
}
