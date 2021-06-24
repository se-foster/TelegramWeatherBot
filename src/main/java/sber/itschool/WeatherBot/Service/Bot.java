package sber.itschool.WeatherBot.Service;

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
    Map<Long, User> users = new HashMap<>();
    ReplyKeyboardMarkup keyboardCity;
    ReplyKeyboardMarkup keyboardForecast;
    WeatherRequest weatherRequest = new WeatherRequest();

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

    @Override
    public void onUpdateReceived(Update update) {
        if (keyboardCity == null || keyboardForecast == null)
            initializeKeyboards();
        if (!isNewUser(update) && (update.hasMessage() && update.getMessage().hasText())) {
            readUserCommand(update);
        }
        processBotState(update);
    }

    private void readUserCommand(Update update) {
        Long chatId = update.getMessage().getChatId();
        switch (update.getMessage().getText()) {
            case "/start":
                greeting(update);
                break;
            case "Сменить город":
                users.get(chatId).setBotState(BotState.CHANGE_CITY_OR_INDEX);
                break;
            case "по названию":
                users.get(chatId).setBotState(BotState.CHANGE_CITY);
                break;
            case "по индексу":
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

    private void processBotState(Update update) {
        Long chatId = update.getMessage().getChatId();
        switch (users.get(chatId).getBotState()) {
            case DEFAULT:
                forecast(update);
                break;
            case CHANGE_CITY_OR_INDEX:
                changeCityOrIndex(update);
                break;
            case CHANGE_CITY:
                changeCity(update);
                break;
            case READ_CITY:
                readCity(update);
                break;
            case CHANGE_INDEX:
                changeIndex(update);
                break;
            case READ_INDEX:
                readIndex(update);
                break;
            case CURRENT_FORECAST:
            case FUTURE_FORECAST:
                requestForecast(update);
                break;
        }
    }

    private void requestForecast(Update update) {
        String forecastType;
        String messageText;
        SendMessage sendMessage = new SendMessage();
        Long chatId = update.getMessage().getChatId();

        if (users.get(chatId).getBotState() == BotState.CURRENT_FORECAST) {
            forecastType = "weather";
        } else {
            forecastType = "forecast";
        }
        sendMessage.setChatId(chatId.toString());
        if (users.get(chatId).getCity() != null) {
            messageText = weatherRequest.getForecast(users.get(chatId).getCity(), forecastType);
        } else {
            messageText = weatherRequest.getForecast(users.get(chatId).getIndex(), forecastType);
        }
        if (messageText == null) {
            messageText = "Такой город по названию или индексу не найден, измени настройки";
            users.get(chatId).setBotState(BotState.DESTINATION_NOT_FOUND);
        }
        sendMessage.setText(messageText);
        sendMessageToUser(sendMessage);
        if (users.get(chatId).getBotState() == BotState.DESTINATION_NOT_FOUND) {
            changeCityOrIndex(update);
        }
    }

    private void readIndex(Update update) {
        Long chatId = update.getMessage().getChatId();
        String userInput = update.getMessage().getText();
        if (!isIndex(userInput)) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId.toString());
            sendMessage.setText("Неправильный индекс!\n" +
                    "Отправьте индекс города РФ в формате 123456");
            sendMessageToUser(sendMessage);
            return;
        }
        users.get(chatId).setIndex(Integer.parseInt(userInput));
        users.get(chatId).setCity(null);
        users.get(chatId).setBotState(BotState.DEFAULT);
        forecast(update);
    }

    private boolean isIndex(String userInput) {
        for (char c : userInput.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        // проверка, что индекс не начинается с нулей
        int index = Integer.parseInt(userInput);
        return Integer.toString(index).length() == 6;
    }

    private void changeIndex(Update update) {
        Long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText("Отправьте индекс города РФ в формате 123456\n");
        sendMessageToUser(sendMessage);
        users.get(chatId).setBotState(BotState.READ_INDEX);
    }

    private void readCity(Update update) {
        Long chatId = update.getMessage().getChatId();
        users.get(chatId).setCity(update.getMessage().getText());
        users.get(chatId).setIndex(null);
        users.get(chatId).setBotState(BotState.DEFAULT);
        forecast(update);
    }

    private void changeCity(Update update) {
        Long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText("Отправьте название города на русском или английском языке");
        sendMessageToUser(sendMessage);
        users.get(chatId).setBotState(BotState.READ_CITY);
    }

    private void greeting(Update update) {
        Long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText("Привет! Это бот прогноза погоды - выпускной проект студента SberItSchool Сергея О.");
        sendMessageToUser(sendMessage);
    }

    private void initializeKeyboards() {
        keyboardCity = new ReplyKeyboardMarkup();
        ArrayList<KeyboardRow> keyboardCityArray = new ArrayList<>();
        KeyboardRow keyboardCityRow = new KeyboardRow();
        keyboardCityRow.add(new KeyboardButton("по названию"));
        keyboardCityRow.add(new KeyboardButton("по индексу"));
        keyboardCityArray.add(keyboardCityRow);
        keyboardCity.setKeyboard(keyboardCityArray);
        keyboardCity.setResizeKeyboard(true);

        keyboardForecast = new ReplyKeyboardMarkup();
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

    private boolean isNewUser(Update update) {

        Long chatId = update.getMessage().getChatId();
        if (users.get(chatId) == null) {
            users.put(chatId, new User());
        }

        User user = users.get(chatId);
        if (user.getBotState() == null) {
            user.setBotState(BotState.CHANGE_CITY_OR_INDEX);
            return true;
        }
        return false;
    }

    private void changeCityOrIndex(Update update) {
        Long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText("Давай настроим город! Выбери способ:");
        sendMessage.setReplyMarkup(keyboardCity);
        sendMessageToUser(sendMessage);
    }

    private void forecast(Update update) {
        String settings;
        Long chatId = update.getMessage().getChatId();
        if (users.get(chatId).getCity() != null) {
            settings = "Город: " + users.get(chatId).getCity();
        } else
            settings = "Индекс: " + users.get(chatId).getIndex().toString();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText("Текущие настройки - " + settings + "\n" +
                "Выбери интересующий прогноз");
        sendMessage.setReplyMarkup(keyboardForecast);
        sendMessageToUser(sendMessage);
    }

    private void sendMessageToUser(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.toString(), e);
        }
    }
}

