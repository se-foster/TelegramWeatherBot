package sber.itschool.WeatherBot.Config;

import java.text.SimpleDateFormat;
import java.util.*;

public class CurrentWeather {
    
    private static class Icons {
        private final Map<String, String> iconsMap = new HashMap<>();
        private Icons() {
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

    private static class Coord {
        private float lon;
        private float lat;

        public void setLon(float lon) {
            this.lon = lon;
        }

        public void setLat(float lat) {
            this.lat = lat;
        }
    }

    private static class Weather {
        private int id;
        private String main;
        private String description;
        private String icon;

        public void setId(int id) {
            this.id = id;
        }

        public void setMain(String main) {
            this.main = main;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }

    private static class Main {
        private float temp;
        private float feels_like;
        private float temp_min;
        private float temp_max;
        private int pressure;
        private int humidity;
        private int sea_level;
        private int grnd_level;

        public void setTemp(float temp) {
            this.temp = temp;
        }

        public void setFeels_like(float feels_like) {
            this.feels_like = feels_like;
        }

        public void setTemp_min(float temp_min) {
            this.temp_min = temp_min;
        }

        public void setTemp_max(float temp_max) {
            this.temp_max = temp_max;
        }

        public void setPressure(int pressure) {
            this.pressure = pressure;
        }

        public void setHumidity(int humidity) {
            this.humidity = humidity;
        }

        public void setSea_level(int sea_level) {
            this.sea_level = sea_level;
        }

        public void setGrnd_level(int grnd_level) {
            this.grnd_level = grnd_level;
        }
    }

    private static class Wind {
        private float speed;
        private int deg;
        private float gust;

        public void setSpeed(float speed) {
            this.speed = speed;
        }

        public void setDeg(int deg) {
            this.deg = deg;
        }

        public void setGust(float gust) {
            this.gust = gust;
        }
    }

    private static class Clouds {
        private int all;

        public void setAll(int all) {
            this.all = all;
        }
    }

    private static class Sys {
        private int type;
        private long id;
        private String country;
        private long sunrise;
        private long sunset;

        public void setType(int type) {
            this.type = type;
        }

        public void setId(long id) {
            this.id = id;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public void setSunrise(long sunrise) {
            this.sunrise = sunrise;
        }

        public void setSunset(long sunset) {
            this.sunset = sunset;
        }
    }

    private Coord coord;
    private Weather[] weather;
    private String base;
    private Main main;
    private int visibility;
    private Wind wind;
    private Clouds clouds;
    private long dt;
    private Sys sys;
    private int timezone;
    private long id;
    private String name;
    private int cod;
    private SimpleDateFormat date = new SimpleDateFormat("EE d MMMM HH:mm", Locale.forLanguageTag("ru"));

    private final Icons icons = new Icons();

    public void setCoord(Coord coord) {
        this.coord = coord;
    }

    public void setWeather(Weather[] weather) {
        this.weather = weather;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public void setClouds(Clouds clouds) {
        this.clouds = clouds;
    }

    public void setDt(long dt) {
        this.dt = dt;
    }

    public void setSys(Sys sys) {
        this.sys = sys;
    }

    public void setTimezone(int timezone) {
        this.timezone = timezone;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCod(int cod) {
        this.cod = cod;
    }

    public String CurrentForecast() {

        return name + "\n" +
              date.format(new Date((dt + timezone) * 1000)) + " " + icons.iconsMap.get(weather[0].icon) + "\n"
                ;
    }
}
