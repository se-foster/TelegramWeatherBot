package sber.itschool.WeatherBot.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import sber.itschool.WeatherBot.Config.CurrentWeather;
import sber.itschool.WeatherBot.Exception.CityNotFoundException;

import javax.naming.MalformedLinkException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

@Component
@Slf4j
@PropertySource("classpath:application.properties")
public class WeatherRequest {

    public String getForecast(String city, String forecastType) {
        String messageToUser = null;
        String urlString = "https://api.openweathermap.org/data/2.5/" + forecastType + "?q="
                + city + "&appid=" + "dee947874e6c8e4b549749e38847082a" + "&lang=ru&units=metric";
        try {
            messageToUser = callForecast(urlString);
        } catch (CityNotFoundException e) {
            log.trace(e.getMessage());
        }
        return messageToUser;
    }

    public String getForecast(Integer index, String forecastType) {
        String messageToUser = null;
        String urlString = "https://api.openweathermap.org/data/2.5/" + forecastType + "?zip="
                + index.toString() + ",ru" + "&appid=" + "dee947874e6c8e4b549749e38847082a" + "&lang=ru&units=metric";
        try {
            messageToUser = callForecast(urlString);
        } catch (CityNotFoundException e) {
            log.trace(e.getMessage());
        }
        return messageToUser;
    }

    private String callForecast(String urlString) throws CityNotFoundException {

        ObjectMapper objectMapper = new ObjectMapper();
        CurrentWeather currentWeather = null;
        try {
            URL url = new URL(urlString);
            currentWeather = objectMapper.readValue(url, CurrentWeather.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return currentWeather.toString();
    }

}
