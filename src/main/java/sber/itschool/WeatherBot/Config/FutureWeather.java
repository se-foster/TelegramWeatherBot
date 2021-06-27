package sber.itschool.WeatherBot.Config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.*;

@Setter
@JsonIgnoreProperties(value = {"cod", "message"})
public class FutureWeather {

    @Setter
    @JsonIgnoreProperties(value = {"clouds", "visibility", "rain", "snow", "sys", "dt_txt"})
    private static class WeatherArray {
        private long dt;
        private CurrentWeather.Main main;
        private CurrentWeather.Weather[] weather;
        private CurrentWeather.Wind wind;
        private int pop;
    }

    @Setter
    @JsonIgnoreProperties(value = {"id", "coord", "country", "population"})
    private static class City {
        private String name;
        private int timezone;
        private long sunrise;
        private long sunset;
    }

    private int cnt;
    private WeatherArray[] list;
    private City city;

    private SimpleDateFormat date = new SimpleDateFormat("E d MMM HH:mm", Locale.forLanguageTag("ru"));
    private SimpleDateFormat time = new SimpleDateFormat("HH:mm");

    private final Icons icons = new Icons();

    public String FutureForecast() {

 //       String temperature = String.format("\uD83C\uDF21️%+.0f°C, ощущается как %+.0f°C",
//                main.temp, main.feels_like);

        date.setTimeZone(TimeZone.getTimeZone("UTC"));
        time.setTimeZone(TimeZone.getTimeZone("UTC"));

        return "done!";

//        return name + "\n" +
//                date.format(new Date((dt + timezone) * 1000)) +
//                " " + icons.iconsMap.get(weather[0].icon) +
//                " " + weather[0].description + "\n" +
 //               temperature  + "\n" +
 //               "атмосферное давление " + Math.round(main.pressure * 0.75) + " мм рт.ст.\n" +
//                "влажность " + main.humidity + "%   " +
//                "\uD83D\uDCA8 " + Math.round(wind.speed) + " м/с" + " \uD83E\uDDED " + wind.getDirection() + "\n" +
//                "\uD83C\uDF05 " + time.format(new Date((sys.sunrise + timezone) * 1000)) +
//                " \uD83C\uDF06 " + time.format(new Date((sys.sunset + timezone) * 1000));
    }
}
