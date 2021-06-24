package sber.itschool.WeatherBot.Config;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CurrentWeather {
    Map<String, String> icons = new HashMap<>();
    String icon;
    float temp;
    float feelsLike;
    int pressure;
    int humidity;
    int visibility;
    float windSpeed;
    int windDeg;
    Date sunrise;
    Date sunset;
    int timezone;

    public CurrentWeather() {
        icons.put("10d", "\uD83C\uDF26");
        icons.put("09d", "\uD83C\uDF27");
        icons.put("11d", "\uD83C\uDF29");
        icons.put("13d", "❄️");
        icons.put("50d", "\uD83C\uDF2B");
        icons.put("01d", "☀");
        icons.put("01n", "\uD83C\uDF0C");
        icons.put("02d", "\uD83C\uDF24");
        icons.put("02n", "\uD83C\uDF14");
        icons.put("03d", "⛅");
        icons.put("03n", "☁");
        icons.put("04d", "\uD83C\uDF25");
        icons.put("04n", "☁");
    }
}
