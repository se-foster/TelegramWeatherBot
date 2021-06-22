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
            switch (update.getMessage().getText()) {
                case "Сменить город":
                    users.get(chatId).setBotState(BotState.CHANGE_CITY_OR_INDEX);
                    break;
                case "Название города":
                    users.get(chatId).setBotState(BotState.CHANGE_CITY);
                    break;
                case "Индекс":
                    users.get(chatId).setBotState(BotState.CHANGE_INDEX);
                    break;
                case "Погода сейчас":
                    users.get(chatId).setBotState(BotState.CURRENT_FORECAST);
                    break;
                case "На 5 дней":
                    users.get(chatId).setBotState(BotState.FUTURE_FORECAST);
                    break;
            }
        }
        processBotState(update);
    }

    private void processBotState(Update update) {
        Long chatId = update.getMessage().getChatId();
        switch (users.get(chatId).getBotState()) {
            case DEFAULT:
                //TODO
                break;
            case CHANGE_CITY_OR_INDEX:
                changeCityOrIndex(update);
                break;
            case CHANGE_CITY:
                changeCity(update);
                break;
            case READ_CITY:
                readCity(update);
            case CHANGE_INDEX:
                changeIndex(update);
                break;
            case READ_INDEX:
                readIndex(update);
                break;
            case CURRENT_FORECAST:
                //TODO
                break;
            case FUTURE_FORECAST:
                //TODO
                break;
        }
    }

    public void readIndex(Update update) {
        Long chatId = update.getMessage().getChatId();
        users.get(chatId).setIndex(Integer.parseInt(update.getMessage().getText()));
        users.get(chatId).setCity(null);
        users.get(chatId).setBotState(BotState.DEFAULT);
    }

    public void changeIndex(Update update) {
        Long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText("Отправьте индекс города РФ в формате 123456\n");
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        users.get(chatId).setBotState(BotState.READ_INDEX);
    }

    public void readCity(Update update) {
        Long chatId = update.getMessage().getChatId();
        users.get(chatId).setCity(update.getMessage().getText());
        users.get(chatId).setIndex(null);
        users.get(chatId).setBotState(BotState.DEFAULT);
    }

    public void changeCity(Update update) {
        Long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText("Отправьте название города на русском или английском языке");
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        users.get(chatId).setBotState(BotState.READ_CITY);
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
        KeyboardRow keyboardCityRow = new KeyboardRow();
        keyboardCityRow.add(new KeyboardButton("Название города"));
        keyboardCityRow.add(new KeyboardButton("Индекс"));
        keyboardCityArray.add(keyboardCityRow);
        keyboardCity.setKeyboard(keyboardCityArray);
        keyboardCity.setResizeKeyboard(true);

        ArrayList<KeyboardRow> keyboardForecastArray = new ArrayList<>();
        KeyboardRow keyboardForecast1 = new KeyboardRow();
        KeyboardRow keyboardForecast2 = new KeyboardRow();
        keyboardForecast1.add(new KeyboardButton("Погода сейчас"));
        keyboardForecast1.add(new KeyboardButton("На 5 дней"));
        keyboardForecast2.add(new KeyboardButton("Сменить город"));
        keyboardForecastArray.add(keyboardForecast1);
        keyboardForecastArray.add(keyboardForecast2);
        keyboardForecast.setKeyboard(keyboardForecastArray);
        keyboardForecast.setResizeKeyboard(true);
    }

    private void checkUser(Update update) {

        Long chatId = update.getMessage().getChatId();

        if (users.get(chatId) == null) {
            users.put(chatId, new User());
        }
        User user = users.get(chatId);

        if (user.getCity() == null && user.getIndex() == null) {
            user.setBotState(BotState.CHANGE_CITY_OR_INDEX);
        }
    }

    private void changeCityOrIndex(Update update) {
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

