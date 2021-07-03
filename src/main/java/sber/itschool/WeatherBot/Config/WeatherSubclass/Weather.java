package sber.itschool.WeatherBot.Config.WeatherSubclass;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class Weather {
    String description;
    String icon;
}
