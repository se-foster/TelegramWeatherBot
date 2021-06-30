package sber.itschool.WeatherBot.Config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Getter
@PropertySource("classpath:application.properties")
public class WeatherConfig {

    @Value("${weatherKey}")
    String weatherKey;
}
