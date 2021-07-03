package sber.itschool.WeatherBot.Config;

import sber.itschool.WeatherBot.Config.WeatherSubclass.*;
import lombok.Setter;
import org.springframework.stereotype.Component;
import sber.itschool.WeatherBot.Config.WeatherSubclass.WeatherList;
import java.text.SimpleDateFormat;
import java.util.*;

@Setter
@Component
public class FutureWeather {

    private WeatherList[] list;
    private City city;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("E d MMM", Locale.forLanguageTag("ru"));
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat dayNumber = new SimpleDateFormat("d");
    private SimpleDateFormat hour = new SimpleDateFormat("h");

    public ArrayList<String> getForecastDates(){
        Date date;
        ArrayList<String> dates = new ArrayList<>();
        for (var i : list) {
            date = new Date((i.getDt() + city.getTimezone()) * 1000);
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

        final Icons icons = new Icons();

        if (dateUserChoice != null) {
            dateToCompare = dateUserChoice;
        } else {
            dateToCompare = dates.get(0);
        }

        result = city.getName() + "\n\uD83D\uDDD3 " + dateToCompare +
                " \uD83C\uDF05 " + timeFormat.format(new Date((city.getSunrise() + city.getTimezone()) * 1000)) +
                " \uD83C\uDF06 " + timeFormat.format(new Date((city.getSunset() + city.getTimezone()) * 1000)) + "\n\n";

        for (var i : list) {
            date = new Date((i.getDt() + city.getTimezone()) * 1000);
            if (dateFormat.format(date).equals(dateToCompare)) {
                temperature = String.format("\uD83C\uDF21️ %+.0f°C ", i.getMain().getTemp());
                result = String.join(" ", result,
                        icons.iconsMap.get(hour.format(date)), timeFormat.format(date),
                        icons.iconsMap.get(i.getWeather()[0].getIcon()), i.getWeather()[0].getDescription(), "\n",
                        temperature,
                        "\uD83D\uDCA8", Integer.toString(Math.round(i.getWind().getSpeed())), "м/с",
                        i.getWind().getDirection(), "☔", (int) (i.getPop() * 100) + "%",
                        "\n\n");
            }
        }
        return result;
    }
}
