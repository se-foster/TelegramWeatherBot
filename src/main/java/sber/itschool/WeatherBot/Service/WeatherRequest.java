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
//        String message = null;
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = null;
        try {
            URL url = new URL(urlString);
//            HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
//            connection.setRequestMethod("GET");
//            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
//            int responseCode = connection.getResponseCode();
//            if (responseCode == 404) {
//                throw new CityNotFoundException(urlString + " doesn't found, responseCode 404");
//            }
//            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            String inputLine;
//            StringBuffer response = new StringBuffer();
//            while ((inputLine = in.readLine()) != null) {
//                response.append(inputLine);
//            }
//            in.close();
//            message = response.toString();
            map = mapper.readValue(url, Map.class);
        } catch (IOException e) {
            log.error(e.toString(), e);
        }
        System.out.println(map);
        return "nice";
    }

}
