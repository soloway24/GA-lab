package lab.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
@EnableAutoConfiguration
public class BeanConfiguration {

    @Bean
    public Random random() {
        return new Random();
    }

}
