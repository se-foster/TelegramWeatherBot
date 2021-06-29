package sber.itschool.WeatherBot.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Location;
import sber.itschool.WeatherBot.Config.CurrentWeather;
import sber.itschool.WeatherBot.Config.FutureWeather;
import sber.itschool.WeatherBot.Config.WeatherConfig;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

@Component
@Slf4j
public class WeatherRequest {

    final WeatherConfig сonfig;

    public WeatherRequest(WeatherConfig сonfig) {
        this.сonfig = сonfig;
    }

    public String getForecast(String city, String forecastType) {
        String urlString = "https://api.openweathermap.org/data/2.5/" + forecastType + "?q="
                + city + "&appid=" + сonfig.getWeatherKey() + "&lang=ru&units=metric";
        if (forecastType.equals("weather"))
            return callCurrentForecast(urlString);
        else
            return callFutureForecast(urlString);
    }

    public String getForecast(Integer index, String forecastType) {
        String urlString = "https://api.openweathermap.org/data/2.5/" + forecastType + "?zip="
                + index + ",ru" + "&appid=" + сonfig.getWeatherKey() + "&lang=ru&units=metric";
        if (forecastType.equals("weather"))
            return callCurrentForecast(urlString);
        else
            return callFutureForecast(urlString);
    }

    public String getForecast(Location location, String forecastType) {
        String urlString = "https://api.openweathermap.org/data/2.5/" + forecastType +
                "?lat=" + location.getLatitude() + "&lon=" + location.getLongitude() +
                "&appid=" + сonfig.getWeatherKey() + "&lang=ru&units=metric";
        if (forecastType.equals("weather"))
            return callCurrentForecast(urlString);
        else
            return callFutureForecast(urlString);
    }

    private String callCurrentForecast(String urlString)  {

        ObjectMapper objectMapper = new ObjectMapper();
        CurrentWeather currentWeather;
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
        FutureWeather futureWeather;
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
