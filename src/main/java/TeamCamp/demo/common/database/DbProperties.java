package TeamCamp.demo.common.database;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * yml에서 명시해줬던 값들을 주입받아서 사용하는 클래스 
 */

@Getter
@Setter
@Component
@ConfigurationProperties("spring.datasource")
public class DbProperties {

    private String url;
    private List<Slave> slaveList;

    private String driverClassName;
    private String username;
    private String password;

    @Getter
    @Setter
    public static class Slave {
        private String name;
        private String url;
    }
}
