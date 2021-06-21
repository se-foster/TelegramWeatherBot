package sber.itschool.WeatherBot.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import sber.itschool.WeatherBot.Config.BotConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
@Slf4j
@PropertySource("classpath:application.properties")
public class WeatherRequest {

    public String GetMessage() throws IOException {

        String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + "Moscow" +
                "&appid=" + "dee947874e6c8e4b549749e38847082a" + "&lang=ru";
        URL urlObject = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
//        int responseCode = connection.getResponseCode();
//        if (responseCode == 404) {
//            throw new IllegalArgumentException();
//        }
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
}
