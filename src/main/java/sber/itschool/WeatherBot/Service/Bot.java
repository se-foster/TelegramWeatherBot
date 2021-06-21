package sber.itschool.WeatherBot.Service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import sber.itschool.WeatherBot.Config.BotConfig;
import sber.itschool.WeatherBot.Config.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class Bot extends TelegramLongPollingBot {

    final BotConfig botConfig;

    public Bot(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotUserName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    Map<Long, User> users = new HashMap<>();
    ReplyKeyboardMarkup keyboardCity = new ReplyKeyboardMarkup(
            new ArrayList<>().toArray(new KeyboardButton("Город"))
    );
    ReplyKeyboardMarkup keyboardForecast = new ReplyKeyboardMarkup();

    @Override
    public void onUpdateReceived(Update update) {

//        initializeKeyboards();
        checkUser(update);

    }

    private void initializeKeyboards() {
        ArrayList<KeyboardRow> keyboardCityArray = new ArrayList<>();
        KeyboardRow keyboardRowCity = new KeyboardRow();
        keyboardRowCity.add("Город");
        keyboardRowCity.add("Индекс");
        keyboardRowCity.add("GPS координаты");
        keyboardCityArray.add(keyboardRowCity);
        keyboardCity.setKeyboard(keyboardCityArray);
        keyboardCity.setResizeKeyboard(true);
        keyboardCity.setSelective(true);
        keyboardCity.setOneTimeKeyboard(true);


        ArrayList<KeyboardRow> keyboardForecastArray = new ArrayList<>();
        KeyboardRow keyboardRowForecast = new KeyboardRow();
        keyboardRowForecast.add("Погода сейчас");
        keyboardRowForecast.add("На ближайший час");
        keyboardRowForecast.add("Почасовой на 2 дня");
        keyboardRowForecast.add("На неделю");
        keyboardRowForecast.add("История за 5 дней");
        keyboardForecastArray.add(keyboardRowForecast);
        keyboardForecast.setKeyboard(keyboardForecastArray);
        keyboardForecast.setResizeKeyboard(true);
        keyboardForecast.setSelective(true);
        keyboardForecast.setOneTimeKeyboard(true);
    }

    private void checkUser(Update update) {

        Long chatId = update.getMessage().getChatId();

        if (users.get(chatId) == null) {
            users.put(chatId, new User());
        }

        User user = users.get(chatId);

        if (user.getCity() == null) {
            preferences(update);
        } else {
            forecast(update);
        }
    }

    private void preferences(Update update) {
        Long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText("Каким способом настроим город?");
        sendMessage.setReplyMarkup(keyboardCity);
        users.get(chatId).setCity("Москва");
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void forecast(Update update) {
        Long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText("Выбери интересующий прогноз:");
        sendMessage.setReplyMarkup(keyboardForecast);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}

