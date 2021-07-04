package sber.itschool.weatherbot.config.weathersubclass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
@JsonIgnoreProperties()
public class City {
    private String name;
    private int timezone;
    private long sunrise;
    private long sunset;
}
