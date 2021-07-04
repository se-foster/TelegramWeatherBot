package sber.itschool.WeatherBot.Config;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sber.itschool.WeatherBot.Config.WeatherSubclass.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Setter
@Component
public class CurrentWeather {

    private Weather[] weather;
    private Main main;
    private Wind wind;
    private long dt;
    private Sys sys;
    private int timezone;
    private String name;

    final Icons icons = new Icons();

    public String currentForecast() {

        DateTimeFormatter date = DateTimeFormatter.ofPattern("E d MMM HH:mm", Locale.forLanguageTag("ru"));
        DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm");
        ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(timezone);

        String temperature = String.format("\uD83C\uDF21️ %+.0f°C, ощущается как %+.0f°C",
                main.getTemp(), main.getFeels_like());

        return name +
                "\n\uD83D\uDDD3 " +
                LocalDateTime.ofEpochSecond(dt, 0, zoneOffset).format(date) + "\n" +
                icons.iconsMap.get(weather[0].getIcon()) + " " +
                weather[0].getDescription() + "\n" +
                temperature  + "\n" +
                "атмосф. давление " + Math.round(main.getPressure() * 0.75) + " мм рт.ст.\n" +
                "влажность " + main.getHumidity() + "% " +
                "\uD83D\uDCA8 " + Math.round(wind.getSpeed()) + " м/с " +
                wind.getDirection() + "\n" +
                "\uD83C\uDF05 " +
                LocalDateTime.ofEpochSecond(sys.getSunrise(), 0, zoneOffset).format(time)
                + "    \uD83C\uDF06 " +
                LocalDateTime.ofEpochSecond(sys.getSunset(), 0, zoneOffset).format(time);
    }
}
