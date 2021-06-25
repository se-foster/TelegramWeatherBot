package sber.itschool.WeatherBot.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import sber.itschool.WeatherBot.Config.CurrentWeather;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

@Component
@Slf4j
@PropertySource("classpath:application.properties")
public class WeatherRequest {

    public String getForecast(String city, String forecastType) {
        String urlString = "https://api.openweathermap.org/data/2.5/" + forecastType + "?q="
                + city + "&appid=" + "dee947874e6c8e4b549749e38847082a" + "&lang=ru&units=metric";
        return callForecast(urlString);
    }

    public String getForecast(Integer index, String forecastType) {
        String urlString = "https://api.openweathermap.org/data/2.5/" + forecastType + "?zip="
                + index.toString() + ",ru" + "&appid=" + "dee947874e6c8e4b549749e38847082a" + "&lang=ru&units=metric";
        return callForecast(urlString);
    }

    private String callForecast(String urlString)  {

        ObjectMapper objectMapper = new ObjectMapper();
        CurrentWeather currentWeather = null;
        try {
            URL url = new URL(urlString);
            currentWeather = objectMapper.readValue(url, CurrentWeather.class);
        } catch (FileNotFoundException e) {
            return "CityNotFound";
        } catch (IOException e) {
            log.error(e.toString());
            return "ERROR";
        }
        return currentWeather.CurrentForecast();
    }

}
