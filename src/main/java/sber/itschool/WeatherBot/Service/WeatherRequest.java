package sber.itschool.WeatherBot.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Location;
import sber.itschool.WeatherBot.Config.CurrentWeather;
import sber.itschool.WeatherBot.Config.FutureWeather;
import sber.itschool.WeatherBot.Config.WeatherConfig;
import sber.itschool.WeatherBot.Exception.CriticalWeatherApiException;
import sber.itschool.WeatherBot.Exception.PlaceNotFoundException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

@Component
@Slf4j
@Import(ObjectMapper.class)
public class WeatherRequest {

    final WeatherConfig config;
    CurrentWeather currentWeather;
    FutureWeather futureWeather;
    @Autowired
    ObjectMapper objectMapper;

    public WeatherRequest(WeatherConfig config) {
        this.config = config;
    }

    public String getForecast(String city, String forecastType, String dateUserChoice)
            throws CriticalWeatherApiException, PlaceNotFoundException {
        URI uri;
        try {
            uri = new URIBuilder()
                    .setScheme("https")
                    .setHost("api.openweathermap.org")
                    .setPath("data/2.5/" + forecastType)
                    .setParameter("q", city)
                    .setParameter("appid", config.getWeatherKey())
                    .setParameter("lang", "ru")
                    .setParameter("units", "metric")
                    .build();
        } catch (URISyntaxException e) {
            log.error(e.toString());
            throw new CriticalWeatherApiException();
        }
        if (forecastType.equals("weather"))
            return callCurrentForecast(uri);
        else
            return callFutureForecast(uri, dateUserChoice);
    }

    public String getForecast(Integer index, String forecastType, String dateUserChoice)
            throws CriticalWeatherApiException, PlaceNotFoundException {
        URI uri;
        try {
            uri = new URIBuilder()
                    .setScheme("https")
                    .setHost("api.openweathermap.org")
                    .setPath("data/2.5/" + forecastType)
                    .setParameter("zip", index.toString() + ",ru")
                    .setParameter("appid", config.getWeatherKey())
                    .setParameter("lang", "ru")
                    .setParameter("units", "metric")
                    .build();
        } catch (URISyntaxException e) {
            log.error(e.toString());
            throw new CriticalWeatherApiException();
        }
        if (forecastType.equals("weather"))
            return callCurrentForecast(uri);
        else
            return callFutureForecast(uri, dateUserChoice);
    }

    public String getForecast(Location location, String forecastType, String dateUserChoice)
            throws CriticalWeatherApiException, PlaceNotFoundException {
        URI uri;
        try {
            uri = new URIBuilder()
                    .setScheme("https")
                    .setHost("api.openweathermap.org")
                    .setPath("data/2.5/" + forecastType)
                    .setParameter("lat", location.getLatitude().toString())
                    .setParameter("lon", location.getLongitude().toString())
                    .setParameter("appid", config.getWeatherKey())
                    .setParameter("lang", "ru")
                    .setParameter("units", "metric")
                    .build();
        } catch (URISyntaxException e) {
            log.error(e.toString());
            throw new CriticalWeatherApiException();
        }
        if (forecastType.equals("weather"))
            return callCurrentForecast(uri);
        else
            return callFutureForecast(uri, dateUserChoice);
    }

    private String callCurrentForecast(URI uri)
            throws PlaceNotFoundException, CriticalWeatherApiException {

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            currentWeather = objectMapper.readValue(uri.toURL(), CurrentWeather.class);
        } catch (FileNotFoundException e) {
            throw new PlaceNotFoundException();
        } catch (IOException e) {
            log.error(e.toString());
            throw new CriticalWeatherApiException();
        }
        return currentWeather.currentForecast();
    }

    private String callFutureForecast(URI uri, String dateUserChoice)
            throws PlaceNotFoundException, CriticalWeatherApiException {

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            futureWeather = objectMapper.readValue(uri.toURL(), FutureWeather.class);
        } catch (FileNotFoundException e) {
            throw new PlaceNotFoundException();
        } catch (IOException e) {
            log.error(e.toString());
            throw new CriticalWeatherApiException();
        }

        return futureWeather.forecastForChosenDate(dateUserChoice);
    }

    public ArrayList<String> getForecastDates(){
        return futureWeather.getForecastDates();
    }
}
