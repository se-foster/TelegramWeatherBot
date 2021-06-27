package sber.itschool.WeatherBot.Config;

import java.util.HashMap;
import java.util.Map;

public class Icons {

    Map<String, String> iconsMap = new HashMap<>();

    public Icons() {
        iconsMap.put("10d", "\uD83C\uDF26");
        iconsMap.put("09d", "\uD83C\uDF27");
        iconsMap.put("11d", "\uD83C\uDF29");
        iconsMap.put("13d", "❄️");
        iconsMap.put("50d", "\uD83C\uDF2B");
        iconsMap.put("01d", "☀️");
        iconsMap.put("01n", "\uD83C\uDF0C");
        iconsMap.put("02d", "\uD83C\uDF24");
        iconsMap.put("02n", "\uD83C\uDF14");
        iconsMap.put("03d", "⛅");
        iconsMap.put("03n", "☁️");
        iconsMap.put("04d", "\uD83C\uDF25");
        iconsMap.put("04n", "☁️");
    }
}
