package sber.itschool.WeatherBot.Config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Setter;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.util.*;

@Setter
@Component
@JsonIgnoreProperties(value = {"cod", "message", "cnt"})
public class FutureWeather extends CurrentWeather {

    @Setter
    @JsonIgnoreProperties(value = {"clouds", "visibility", "rain", "snow", "sys", "dt_txt"})
    private static class WeatherArray {
        private long dt;
        private Main main;
        private Weather[] weather;
        private Wind wind;
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

    public ArrayList<String> getForecastDates(){
        Date date;
        ArrayList<String> dates = new ArrayList<>();
        for (var i : list) {
            date = new Date((i.dt + city.timezone) * 1000);
            if (!dates.contains(dateFormat.format(date))) {
                dates.add(dateFormat.format(date));
            }
        }
        return dates;
    }

    public String forecastForChosenDate(String dateUserChoice) {

        Date date;
        ArrayList<String> dates = getForecastDates();
        String temperature;
        String result;
        String dateToCompare;

        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        dayNumber.setTimeZone(TimeZone.getTimeZone("UTC"));
        hour.setTimeZone(TimeZone.getTimeZone("UTC"));


        if (dateUserChoice != null) {
            dateToCompare = dateUserChoice;
        } else {
            dateToCompare = dates.get(0);
        }

        result = city.name + "\n\uD83D\uDDD3 " + dateToCompare +
                " \uD83C\uDF05 " + timeFormat.format(new Date((city.sunrise + city.timezone) * 1000)) +
                " \uD83C\uDF06 " + timeFormat.format(new Date((city.sunset + city.timezone) * 1000)) + "\n\n";

        for (var i : list) {
            date = new Date((i.dt + city.timezone) * 1000);
            if (dateFormat.format(date).equals(dateToCompare)) {
                temperature = String.format("\uD83C\uDF21️ %+.0f°C ", i.main.temp);
                result = String.join(" ", result,
                        icons.iconsMap.get(hour.format(date)), timeFormat.format(date),
                        icons.iconsMap.get(i.weather[0].icon), i.weather[0].description, "\n",
                        temperature,
                        "\uD83D\uDCA8", Integer.toString(Math.round(i.wind.speed)), "м/с",
                        "\uD83E\uDDED", i.wind.getDirection(), "☔", (int) (i.pop * 100) + "%",
                        "\n\n");
            }
        }

        return result;



    }
}
