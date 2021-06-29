package sber.itschool.WeatherBot.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Location;
import sber.itschool.WeatherBot.Config.CurrentWeather;
import sber.itschool.WeatherBot.Config.FutureWeather;

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
        if (forecastType.equals("weather"))
            return callCurrentForecast(urlString);
        else
            return callFutureForecast(urlString);
    }

    public String getForecast(Integer index, String forecastType) {
        String urlString = "https://api.openweathermap.org/data/2.5/" + forecastType + "?zip="
                + index + ",ru" + "&appid=" + "dee947874e6c8e4b549749e38847082a" + "&lang=ru&units=metric";
        if (forecastType.equals("weather"))
            return callCurrentForecast(urlString);
        else
            return callFutureForecast(urlString);
    }

    public String getForecast(Location location, String forecastType) {
        String urlString = "https://api.openweathermap.org/data/2.5/" + forecastType +
                "?lat=" + location.getLatitude() + "&lon=" + location.getLongitude() +
                "&appid=" + "dee947874e6c8e4b549749e38847082a" + "&lang=ru&units=metric";
        if (forecastType.equals("weather"))
            return callCurrentForecast(urlString);
        else
            return callFutureForecast(urlString);
    }

    private String callCurrentForecast(String urlString)  {

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

    private String callFutureForecast(String urlString)  {

        ObjectMapper objectMapper = new ObjectMapper();
        FutureWeather futureWeather = null;
        try {
            URL url = new URL(urlString);
            futureWeather = objectMapper.readValue(url, FutureWeather.class);
        } catch (FileNotFoundException e) {
            return "CityNotFound";
        } catch (IOException e) {
            log.error(e.toString());
            return "ERROR";
        }

        return futureWeather.FutureForecast();
    }

}
