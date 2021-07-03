package sber.itschool.WeatherBot.Config;

import lombok.Setter;
import org.springframework.stereotype.Component;
import sber.itschool.WeatherBot.Config.WeatherSubclass.*;
import java.text.SimpleDateFormat;
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

    private SimpleDateFormat date = new SimpleDateFormat("E d MMM HH:mm", Locale.forLanguageTag("ru"));
    private SimpleDateFormat time = new SimpleDateFormat("HH:mm");
    // localdate - zonedate
    final Icons icons = new Icons();

    public String currentForecast() {

        String temperature = String.format("\uD83C\uDF21️ %+.0f°C, ощущается как %+.0f°C",
                main.getTemp(), main.getFeels_like());

        date.setTimeZone(TimeZone.getTimeZone("UTC"));
        time.setTimeZone(TimeZone.getTimeZone("UTC"));

        return name + "\n\uD83D\uDDD3 " + date.format(new Date((dt + timezone) * 1000)) + "\n" +
                icons.iconsMap.get(weather[0].getIcon()) + " " + weather[0].getDescription() + "\n" +
                temperature  + "\n" +
                "атмосф. давление " + Math.round(main.getPressure() * 0.75) + " мм рт.ст.\n" +
                "влажность " + main.getHumidity() + "% " +
                "\uD83D\uDCA8 " + Math.round(wind.getSpeed()) + " м/с " + wind.getDirection() + "\n" +
                "\uD83C\uDF05 " + time.format(new Date((sys.getSunrise() + timezone) * 1000)) +
                "    \uD83C\uDF06 " + time.format(new Date((sys.getSunset() + timezone) * 1000));
    }
}
