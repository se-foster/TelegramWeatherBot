package sber.itschool.WeatherBot.Config.WeatherSubclass;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class City {
    private String name;
    private int timezone;
    private long sunrise;
    private long sunset;
}
