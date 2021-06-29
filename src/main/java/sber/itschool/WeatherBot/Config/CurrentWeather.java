package sber.itschool.WeatherBot.Config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.*;

@Setter
@JsonIgnoreProperties(value = {"coord", "clouds", "id", "cod", "base", "visibility", "rain", "snow"})
public class CurrentWeather {

    @Setter
    @JsonIgnoreProperties(value = {"id", "main"})
    static class Weather {
        String description;
        String icon;
    }

    @Setter
    @JsonIgnoreProperties(value = {"sea_level", "grnd_level", "temp_kf"})
    static class Main {
        float temp;
        float feels_like;
        float temp_min;
        float temp_max;
        float pressure;
        int humidity;
    }

    @Setter
    static class Wind {
        float speed;
        float deg;
        float gust;

        public String getDirection() {
            if (deg < 11.25)
                return "C";
            else if (deg < 33.75)
                return "CСВ";
            else if (deg < 56.25)
                return "CВ";
            else if (deg < 78.75)
                return "ВCВ";
            else if (deg < 101.25)
                return "В";
            else if (deg < 123.75)
                return "ВЮВ";
            else if (deg < 146.25)
                return "ЮВ";
            else if (deg < 168.75)
                return "ЮЮВ";
            else if (deg < 191.25)
                return "Ю";
            else if (deg < 213.75)
                return "ЮЮЗ";
            else if (deg < 236.25)
                return "ЮЗ";
            else if (deg < 258.75)
                return "ЗЮЗ";
            else if (deg < 281.25)
                return "З";
            else if (deg < 303.75)
                return "ЗСЗ";
            else if (deg < 326.25)
                return "СЗ";
            else if (deg < 348.75)
                return "CСЗ";
            //else if (deg >= 348.75)
            return "C";
        }
    }

    @Setter
    @JsonIgnoreProperties(value = {"type", "id", "country"})
    private static class Sys {
        private long sunrise;
        private long sunset;
    }

    private Weather[] weather;
    private Main main;
    private Wind wind;
    private long dt;
    private Sys sys;
    private int timezone;
    private String name;

    private SimpleDateFormat date = new SimpleDateFormat("E d MMM HH:mm", Locale.forLanguageTag("ru"));
    private SimpleDateFormat time = new SimpleDateFormat("HH:mm");

    private final Icons icons = new Icons();

    public String CurrentForecast() {

        String temperature = String.format("\uD83C\uDF21️ %+.0f°C, ощущается как %+.0f°C",
                main.temp, main.feels_like);

        date.setTimeZone(TimeZone.getTimeZone("UTC"));
        time.setTimeZone(TimeZone.getTimeZone("UTC"));

        return name + "\n\uD83D\uDDD3 " + date.format(new Date((dt + timezone) * 1000)) + "\n" +
                icons.iconsMap.get(weather[0].icon) + " " + weather[0].description + "\n" +
                temperature  + "\n" +
                "атмосф. давление " + Math.round(main.pressure * 0.75) + " мм рт.ст.\n" +
                "влажность " + main.humidity + "% " +
                "\uD83D\uDCA8 " + Math.round(wind.speed) + " м/с" + " \uD83E\uDDED " + wind.getDirection() + "\n" +
                "\uD83C\uDF05 " + time.format(new Date((sys.sunrise + timezone) * 1000)) +
                "    \uD83C\uDF06 " + time.format(new Date((sys.sunset + timezone) * 1000));
    }
}
