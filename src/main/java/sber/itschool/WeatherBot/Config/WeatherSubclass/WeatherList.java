package sber.itschool.WeatherBot.Config.WeatherSubclass;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class WeatherList {
    private long dt;
    private Main main;
    private Weather[] weather;
    private Wind wind;
    private float pop;
}
