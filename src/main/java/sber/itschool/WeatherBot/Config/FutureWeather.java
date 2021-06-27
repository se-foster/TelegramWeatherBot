package sber.itschool.WeatherBot.Config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.*;

@Setter
@JsonIgnoreProperties(value = {"cod", "message", "cnt"})
public class FutureWeather {

    @Setter
    @JsonIgnoreProperties(value = {"clouds", "visibility", "rain", "snow", "sys", "dt_txt"})
    private static class WeatherArray {
        private long dt;
        private CurrentWeather.Main main;
        private CurrentWeather.Weather[] weather;
        private CurrentWeather.Wind wind;
        private float pop;
    }

    @Setter
    @JsonIgnoreProperties(value = {"id", "coord", "country", "population"})
    private static class City {
        private String name;
        private int timezone;
        private long sunrise;
        private long sunset;
    }

    private WeatherArray[] list;
    private City city;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("E d MMM", Locale.forLanguageTag("ru"));
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat dayNumber = new SimpleDateFormat("d");
    private SimpleDateFormat hour = new SimpleDateFormat("h");

    private final Icons icons = new Icons();

    public String FutureForecast() {

        Date date;
        int day = 0;
        String result = city.name + "\n";
        String res1 = "";
        String temperature;

        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        dayNumber.setTimeZone(TimeZone.getTimeZone("UTC"));
        hour.setTimeZone(TimeZone.getTimeZone("UTC"));

        for (var i : list) {
            date = new Date((i.dt + city.timezone) * 1000);
            temperature = String.format("\uD83C\uDF21️ %+.0f°C ", i.main.temp);
//            if (day != Integer.parseInt(dayNumber.format(date))) {
//                result = String.join("\n", result,
//                        "\uD83D\uDDD3" + dateFormat.format(date) + "\n");
//                day = Integer.parseInt(dayNumber.format(date));
//            }
            result = String.join(" ", result, "\uD83D\uDDD3", dateFormat.format(date),
                    icons.iconsMap.get(hour.format(date)), timeFormat.format(date), "\n",
                    icons.iconsMap.get(i.weather[0].icon), i.weather[0].description, "\n",
                    temperature,
                    "\uD83D\uDCA8", Integer.toString(Math.round(i.wind.speed)), "м/с",
                    "\uD83E\uDDED", i.wind.getDirection(), "☔", (int) (i.pop * 100) + "%",
                    "\n\n");
        }

        return result;


//                "\uD83C\uDF05 " + time.format(new Date((sys.sunrise + timezone) * 1000)) +
//                " \uD83C\uDF06 " + time.format(new Date((sys.sunset + timezone) * 1000));
    }
}
