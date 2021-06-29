package sber.itschool.WeatherBot.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import sber.itschool.WeatherBot.Config.BotConfig;
import sber.itschool.WeatherBot.Config.BotState;
import sber.itschool.WeatherBot.Config.User;

import java.io.File;
import java.util.*;

@Component
@Slf4j
public class Bot extends TelegramLongPollingBot {

    final BotConfig config;
    Map<Long, User> users;
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    WeatherRequest weatherRequest;

    public Bot(BotConfig config) {
        this.config = config;
    }

    @Override
    public String getBotUsername() {
        return config.getBotUserName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = update.getMessage().getChatId();
        if (users == null) {
            TypeReference<HashMap<Long, User>> typeRef = new TypeReference<HashMap<Long, User>>() {};
            users = objectMapper.readValue(new File("Users.json"), typeRef);
        } else {
            objectMapper.writeValue(new File("Users.json"), users);
        }
        if (users.get(chatId) == null) {
            users.put(chatId, new User());
        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            readUserCommand(update, chatId);
        }
        processBotState(update, chatId);
    }

    private void readUserCommand(Update update, Long chatId) {
        String userCommand = update.getMessage().getText();
        if (users.get(chatId).getBotState() != BotState.DEFAULT &&
                (userCommand.equals("/current") || userCommand.equals("/future"))) {
            sendMessageToUser(chatId, "сначала отправь город, индекс или GPS координаты");
            return;
        }
        switch (update.getMessage().getText()) {
            case "/settings":
                users.get(chatId).clear();
                users.get(chatId).setBotState(BotState.CHANGE_SETTINGS);
                break;
            case "/current":
                users.get(chatId).setBotState(BotState.CURRENT_FORECAST);
                break;
            case "/future":
                users.get(chatId).setBotState(BotState.FUTURE_FORECAST);
                break;
        }
    }

    private void sendMessageToUser(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.toString(), e);
        }
    }

    private void processBotState(Update update, Long chatId) {
        if (update.getMessage().hasText() &&
                update.getMessage().getText().equals("/start") &&
                users.get(chatId).getBotState() != BotState.CHANGE_SETTINGS) {
            return;
        }
        switch (users.get(chatId).getBotState()) {
            case DEFAULT:
                sendMessageToUser(chatId, "Не понимаю, выбери команду из меню \uD83D\uDE43");
                break;
            case CHANGE_SETTINGS:
                sendMessageToUser(chatId, "Отправь название города на русском или английском языке, " +
                        "6 цифр индекса города РФ или геолокацию");
                users.get(chatId).setBotState(BotState.READ_SETTINGS);
                break;
            case READ_SETTINGS:
                readSettings(update, chatId);
                break;
            case CURRENT_FORECAST:
                requestCurrentForecast(chatId);
                users.get(chatId).setBotState(BotState.DEFAULT);
                break;
            case FUTURE_FORECAST:
                requestFutureForecast(update, chatId);
                users.get(chatId).setBotState(BotState.DEFAULT);
                break;
        }
    }

    private void readSettings(Update update, Long chatId) {
        if (update.getMessage().hasLocation()) {
            users.get(chatId).setLocation(update.getMessage().getLocation());
            sendMessageToUser(chatId, users.get(chatId).getSettings());
        } else if (update.getMessage().hasText()) {
            String userInput = update.getMessage().getText();
            if (userInput.equals("/current") || userInput.equals("/future")) {
                return;
            }
            if (isIndex(userInput)) {
                users.get(chatId).setIndex(Integer.parseInt(userInput));
                sendMessageToUser(chatId, users.get(chatId).getSettings());
            } else if (isCity(userInput)) {
                users.get(chatId).setCity(userInput);
                sendMessageToUser(chatId, users.get(chatId).getSettings());
            } else {
                sendMessageToUser(chatId, "Что-то не похоже на индекс или название города \uD83E\uDD13");
            }
        }
    }

    private void requestCurrentForecast(Long chatId) {
//        String forecastType;
        String messageText;

//        if (users.get(chatId).getBotState() == BotState.CURRENT_FORECAST) {
//            forecastType = "weather";
//        } else {
//            forecastType = "forecast";
//        }

        if (users.get(chatId).getCity() != null) {
            messageText = weatherRequest.getForecast(users.get(chatId).getCity(), "weather");
        } else if (users.get(chatId).getIndex() != null) {
            messageText = weatherRequest.getForecast(users.get(chatId).getIndex(), "weather");
        } else { //else if (users.get(chatId).getLocation() != null)
            messageText = weatherRequest.getForecast(users.get(chatId).getLocation(), "weather");
        }

        if (messageText.equals("CityNotFound")) {
            messageText = "Такой город по названию или индексу не найден, измени настройки";
            users.get(chatId).setBotState(BotState.DESTINATION_NOT_FOUND);
        } else if (messageText.equals("ERROR")) {
            messageText = "Произошла критическая ошибка на при запросе прогноза погоды от сервера. Попробуйте позже";
            users.get(chatId).setBotState(BotState.DEFAULT);
        }

        sendMessageToUser(chatId, messageText);
    }

    private void requestFutureForecast(Update update, Long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Тык");
        inlineKeyboardButton.setCallbackData("Button \"Тык\" has been pressed");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton);
        List<List<InlineKeyboardButton>> rowList= new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

//        String forecastType;
        String messageText;

//        if (users.get(chatId).getBotState() == BotState.CURRENT_FORECAST) {
//            forecastType = "weather";
//        } else {
//            forecastType = "forecast";
//        }

        if (users.get(chatId).getCity() != null) {
            messageText = weatherRequest.getForecast(users.get(chatId).getCity(), "forecast");
        } else if (users.get(chatId).getIndex() != null) {
            messageText = weatherRequest.getForecast(users.get(chatId).getIndex(), "forecast");
        } else { //else if (users.get(chatId).getLocation() != null)
            messageText = weatherRequest.getForecast(users.get(chatId).getLocation(), "forecast");
        }

        if (messageText.equals("CityNotFound")) {
            messageText = "Такой город по названию или индексу не найден, измени настройки";
            users.get(chatId).setBotState(BotState.DESTINATION_NOT_FOUND);
        } else if (messageText.equals("ERROR")) {
            messageText = "Произошла критическая ошибка на при запросе прогноза погоды от сервера. Попробуйте позже";
            users.get(chatId).setBotState(BotState.DEFAULT);
        }

        sendMessageToUser(chatId, messageText);
    }

    private boolean isIndex(String userInput) {
        for (char c : userInput.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        int index;
        try {
            index = Integer.parseInt(userInput);
        }
        catch (NumberFormatException e) {
            return false;
        }
        return Integer.toString(index).length() == 6;
    }

    private boolean isCity(String userInput) {
        for (char c : userInput.toCharArray()) {
            if (Character.isDigit(c)) {
                return false;
            }
        }
        for (char c : userInput.toCharArray()) {
            if (!Character.isAlphabetic(c) && c != ' ' && c != '-' && c != '\'') {
                return false;
            }
        }
        return userInput.length() > 1;
    }
}

