package TeamCamp;

import TeamCamp.demo.common.properties.AppProperties;
import TeamCamp.demo.common.properties.CacheProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@EnableConfigurationProperties(value = {AppProperties.class, CacheProperties.class})
public class Application {
    public static final String APPLICATION_LOCATIONS =
            "spring.config.location ="+
                    "classpath:application.properties";

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class)
                .properties(APPLICATION_LOCATIONS)
                .run(args);
    }

}