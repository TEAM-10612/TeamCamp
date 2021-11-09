package TeamCamp;

import TeamCamp.demo.common.config.DatabaseProperty;
import TeamCamp.demo.common.properties.AppProperties;
import TeamCamp.demo.common.properties.CacheProperties;
import TeamCamp.demo.common.s3.AwsProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;



//데이터 소스 직접 설정을 위해 DataSourceAutoConfiguration 클래스를 제외한다.
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableConfigurationProperties(value = {AppProperties.class, CacheProperties.class, AwsProperties.class , DatabaseProperty.class})
public class Application {


    public static final String APPLICATION_LOCATIONS = "spring.config.locations="
            + "/app/config/application.yml"
            + "/app/config/application-prod.yml";
    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class)
                .properties(APPLICATION_LOCATIONS)
                .run();
    }

}