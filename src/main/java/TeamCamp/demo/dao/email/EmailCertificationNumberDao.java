package TeamCamp.demo.dao.email;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

import static TeamCamp.demo.util.certification.email.EmailConstants.LIMIT_TIME_CERTIFICATION_NUMBER;
import static TeamCamp.demo.util.certification.email.EmailConstants.PREFIX_CERTIFICATION;

@RequiredArgsConstructor
@Repository
public class EmailCertificationNumberDao implements EmailCertificationDao{
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void createEmail(String email, String certificationNumber) {
        stringRedisTemplate.opsForValue()
                .set(PREFIX_CERTIFICATION + email,certificationNumber, Duration.ofSeconds(LIMIT_TIME_CERTIFICATION_NUMBER));
    }

    @Override
    public String getEmailCertification(String email) {
        return stringRedisTemplate.opsForValue().get(PREFIX_CERTIFICATION + email);
    }

    @Override
    public void removeEmailCertification(String email) {
        stringRedisTemplate.delete(PREFIX_CERTIFICATION + email);
    }

    @Override
    public boolean hasKey(String email) {
        return stringRedisTemplate.hasKey(PREFIX_CERTIFICATION+email);
    }
}
