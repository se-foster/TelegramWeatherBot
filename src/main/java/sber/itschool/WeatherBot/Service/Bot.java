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
import sber.itschool.WeatherBot.Config.BotState;
import sber.itschool.WeatherBot.Config.User;

import java.util.*;

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
    ReplyKeyboardMarkup keyboardCity = new ReplyKeyboardMarkup();
    ReplyKeyboardMarkup keyboardForecast = new ReplyKeyboardMarkup();

    @Override
    public void onUpdateReceived(Update update) {
        initializeKeyboards();

        checkUser(update);
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
//            if (update.getMessage().getText().equals("/start")) {
//                greeting(update);
//            }
            if (update.getMessage().getText().equals("Сменить город")) {
                preferences(update);
            }
            else if (update.getMessage().getText().equals("Название города")) {
                users.get(chatId).setBotState(BotState.CHANGE_CITY);
            }
        }
    }

    public void changeCityName(Update update) {
        Long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText("Введите название города на русском или английском языке");
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    public void greeting(Update update) {
        Long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText("Привет! Это бот прогноза погоды - выпускной проект студента SberItSchool Сергея О.");
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void initializeKeyboards() {
        ArrayList<KeyboardRow> keyboardCityArray = new ArrayList<>();
        KeyboardRow keyboardCityRow1 = new KeyboardRow();
        KeyboardRow keyboardCityRow2 = new KeyboardRow();
        keyboardCityRow1.add(new KeyboardButton("Название города"));
        keyboardCityRow1.add(new KeyboardButton("Индекс"));
        keyboardCityRow2.add(new KeyboardButton("GPS координаты"));
        keyboardCityArray.add(keyboardCityRow1);
        keyboardCityArray.add(keyboardCityRow2);
        keyboardCity.setKeyboard(keyboardCityArray);
        keyboardCity.setResizeKeyboard(true);

        ArrayList<KeyboardRow> keyboardForecastArray = new ArrayList<>();
        KeyboardRow keyboardForecast1 = new KeyboardRow();
        KeyboardRow keyboardForecast2 = new KeyboardRow();
        KeyboardRow keyboardForecast3 = new KeyboardRow();
        keyboardForecast1.add(new KeyboardButton("Погода сейчас"));
        keyboardForecast1.add(new KeyboardButton("На ближайший час"));
        keyboardForecast2.add(new KeyboardButton("Почасовой на 2 дня"));
        keyboardForecast2.add(new KeyboardButton("На неделю"));
        keyboardForecast3.add(new KeyboardButton("История за 5 дней"));
        keyboardForecast3.add(new KeyboardButton("Сменить город"));
        keyboardForecastArray.add(keyboardForecast1);
        keyboardForecastArray.add(keyboardForecast2);
        keyboardForecastArray.add(keyboardForecast3);
        keyboardForecast.setKeyboard(keyboardForecastArray);
        keyboardForecast.setResizeKeyboard(true);
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
        sendMessage.setText("Текущие настройки" + users.get(chatId).toString());
        sendMessage.setReplyMarkup(keyboardForecast);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}

