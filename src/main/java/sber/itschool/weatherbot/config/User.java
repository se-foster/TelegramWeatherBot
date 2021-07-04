package sber.itschool.weatherbot.config;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Location;
import sber.itschool.weatherbot.enums.BotState;

@Getter
@Component
public class User {

    String city;
    Integer index;
    Location location;
    BotState botState;
    String settings;

    public User() {
        this.botState = BotState.CHANGE_SETTINGS;
    }

    public void setLocation(Location location) {
        this.location = location;
        if (location != null) {
            this.botState = BotState.DEFAULT;
            this.settings = "Установлены GPS координаты" + "\n" +
                    location.getLatitude() + ":" + location.getLongitude() + "\n" +
                    "Воспользуйся меню для запроса прогноза погоды или смены настроек";
        }
    }

    public void setCity(String city) {
        this.city = city;
        if (city != null) {
            this.botState = BotState.DEFAULT;
            this.settings = "Установлен город " + city + "\n" +
                    "Воспользуйся меню для запроса прогноза погоды или смены настроек";
        }
    }

    public void setIndex(Integer index) {
        this.index = index;
        if (index != null) {
            this.botState = BotState.DEFAULT;
            this.settings = "Установлен индекс " + index + "\n" +
                    "Воспользуйся меню для запроса прогноза погоды или смены настроек";
        }
    }

    public void clear() {
        this.index = null;
        this.city = null;
        this.location = null;
        this.botState = null;
        this.settings = null;
    }

    public void setBotState(BotState botState) {
        this.botState = botState;
    }
}
