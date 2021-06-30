package sber.itschool.WeatherBot.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import sber.itschool.WeatherBot.Config.BotConfig;
import sber.itschool.WeatherBot.Enum.BotState;
import sber.itschool.WeatherBot.Config.User;
import java.util.*;

@Component
@Slf4j
public class Bot extends TelegramLongPollingBot {

    final BotConfig config;
    Map<Long, User> users;
    @Autowired
    WeatherRequest weatherRequest;
    @Autowired
    Serializer serializer;

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

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId;

        if (users == null) {
            users = serializer.deserialize();
        }
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getFrom().getId();
        } else {
            return;
        }
        if (users.get(chatId) == null) {
            users.put(chatId, new User());
        }
        if ((update.hasMessage() && update.getMessage().hasText()) || update.hasCallbackQuery()) {
            readUserCommand(update, chatId);
        }
        if (update.hasMessage()) {
            processBotState(update.getMessage(), chatId);
        } else if (update.hasCallbackQuery()) {
            processBotState(update.getCallbackQuery(), chatId);
        }

        serializer.serialize(users);
    }

    private void readUserCommand(Update update, Long chatId) {
        String userCommand;
        if (update.hasMessage() && update.getMessage().hasText()) {
            userCommand = update.getMessage().getText();
        } else if (update.hasCallbackQuery()) {
            userCommand = update.getCallbackQuery().getData();
        } else {
            return;
        }

        if (users.get(chatId).getBotState() != BotState.DEFAULT &&
                (userCommand.equals("/current") || userCommand.equals("/future"))) {
            sendTextMessageToUser(chatId, "сначала отправь город, индекс или GPS координаты");
            return;
        }
        switch (userCommand) {
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

    private void sendTextMessageToUser(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.toString(), e);
        }
    }

    private void processBotState(Message message, Long chatId) {
        if (message.hasText() && message.getText().equals("/start")
                && users.get(chatId).getBotState() != BotState.CHANGE_SETTINGS) {
            return;
        }
        switch (users.get(chatId).getBotState()) {
            case DEFAULT:
                sendTextMessageToUser(chatId, "Не понимаю, выбери команду из меню \uD83D\uDE43");
                break;
            case CHANGE_SETTINGS:
                sendTextMessageToUser(chatId, "Отправь название города на русском или английском языке, " +
                        "6 цифр индекса города РФ или геолокацию");
                users.get(chatId).setBotState(BotState.READ_SETTINGS);
                break;
            case READ_SETTINGS:
                readSettings(message, chatId);
                break;
            case CURRENT_FORECAST:
                requestCurrentForecast(chatId);
                users.get(chatId).setBotState(BotState.DEFAULT);
                break;
            case FUTURE_FORECAST:
                requestFutureForecast(chatId, null);
                users.get(chatId).setBotState(BotState.DEFAULT);
                break;
        }
    }

    private void processBotState(CallbackQuery callbackQuery, Long chatId) {
        switch (users.get(chatId).getBotState()) {
            case CHANGE_SETTINGS:
                sendTextMessageToUser(chatId, "Отправь название города на русском или английском языке, " +
                        "6 цифр индекса города РФ или геолокацию");
                users.get(chatId).setBotState(BotState.READ_SETTINGS);
                break;
            case CURRENT_FORECAST:
                requestCurrentForecast(chatId);
                users.get(chatId).setBotState(BotState.DEFAULT);
                break;
            case FUTURE_FORECAST:
                requestFutureForecast(chatId, null);
                users.get(chatId).setBotState(BotState.DEFAULT);
                break;
            case DEFAULT:
                requestFutureForecast(chatId, callbackQuery);
        }
    }

    private void readSettings(Message message, Long chatId) {
        // button 1
        InlineKeyboardButton currentForecastButton = new InlineKeyboardButton();
        currentForecastButton.setText("⌚️ текущий прогноз");
        currentForecastButton.setCallbackData("/current");
        // button 2
        InlineKeyboardButton futureForecastButton = new InlineKeyboardButton();
        futureForecastButton.setText("\uD83D\uDDD3 на 5 дней");
        futureForecastButton.setCallbackData("/future");
        // button 3
        InlineKeyboardButton settingsButton = new InlineKeyboardButton();
        settingsButton.setText("⚙ сменить город");
        settingsButton.setCallbackData("/settings");
        // row 1
        List<InlineKeyboardButton> row1 = new LinkedList<>();
        row1.add(currentForecastButton);
        row1.add(futureForecastButton);
        // row 2
        List<InlineKeyboardButton> row2 = new LinkedList<>();
        row2.add(settingsButton);
        // rows
        List<List<InlineKeyboardButton>> rows = new LinkedList<>();
        rows.add(row1);
        rows.add(row2);
        // keyboard
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.setKeyboard(rows);

        if (message.hasLocation()) {
            users.get(chatId).setLocation(message.getLocation());
            sendMessageToUserWithKeyboard(chatId, users.get(chatId).getSettings(), keyboard);
        } else if (message.hasText()) {
            String userInput = message.getText();
            if (userInput.equals("/current") || userInput.equals("/future")) {
                return;
            }
            if (isIndex(userInput)) {
                users.get(chatId).setIndex(Integer.parseInt(userInput));
                sendMessageToUserWithKeyboard(chatId, users.get(chatId).getSettings(), keyboard);
            } else if (isCity(userInput)) {
                users.get(chatId).setCity(userInput);
                sendMessageToUserWithKeyboard(chatId, users.get(chatId).getSettings(), keyboard);
            } else {
                sendTextMessageToUser(chatId, "Что-то не похоже на индекс или название города \uD83E\uDD13");
            }
        }
    }

    private void sendMessageToUserWithKeyboard(Long chatId, String text, InlineKeyboardMarkup keyboard) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(keyboard);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.toString(), e);
        }
    }

    private void requestCurrentForecast(Long chatId) {
        String messageText;

        if (users.get(chatId).getCity() != null) {
            messageText = weatherRequest.getForecast(users.get(chatId).getCity(), "weather", null);
        } else if (users.get(chatId).getIndex() != null) {
            messageText = weatherRequest.getForecast(users.get(chatId).getIndex(), "weather", null);
        } else if (users.get(chatId).getLocation() != null) {
            messageText = weatherRequest.getForecast(users.get(chatId).getLocation(), "weather", null);
        } else {
            return;
        }

        if (messageText.equals("CityNotFound")) {
            messageText = "Такой город по названию или индексу не найден, измени настройки";
        } else if (messageText.equals("ERROR")) {
            messageText = "Произошла критическая ошибка на при запросе прогноза погоды от сервера. Попробуйте позже";
            users.get(chatId).setBotState(BotState.DEFAULT);
        }

        sendTextMessageToUser(chatId, messageText);
    }

    private void requestFutureForecast(Long chatId, CallbackQuery callbackQuery) {
        // button settings
        InlineKeyboardButton settingsButton = new InlineKeyboardButton();
        settingsButton.setText("⚙ сменить город");
        settingsButton.setCallbackData("/settings");
        // buttons
        InlineKeyboardButton btmBack = new InlineKeyboardButton();
        InlineKeyboardButton btmForward = new InlineKeyboardButton();
        // rows
        List<InlineKeyboardButton> row1 = new LinkedList<>();
        List<InlineKeyboardButton> row2 = new LinkedList<>();

        String messageText = null;
        String userChoice;
        if (callbackQuery == null) {
            userChoice = null;
        } else {
            userChoice = callbackQuery.getData();
        }

        if (users.get(chatId).getCity() != null) {
            messageText = weatherRequest.getForecast(users.get(chatId).getCity(), "forecast", userChoice);
        } else if (users.get(chatId).getIndex() != null) {
            messageText = weatherRequest.getForecast(users.get(chatId).getIndex(), "forecast", userChoice);
        } else if (users.get(chatId).getLocation() != null) {
            messageText = weatherRequest.getForecast(users.get(chatId).getLocation(), "forecast", userChoice);
        }

        if (messageText.equals("CityNotFound")) {
            sendTextMessageToUser(chatId, "Такой город по названию или индексу не найден, измени настройки");
            return;
        } else if (messageText.equals("ERROR")) {
            sendTextMessageToUser(chatId, "Произошла ошибка на при запросе прогноза погоды от сервера. " +
                    "Попробуйте позже");
            users.get(chatId).setBotState(BotState.DEFAULT);
            return;
        }

        ArrayList<String> dates = weatherRequest.getForecastDates();

        int userChoicePos = dates.indexOf(userChoice);

        if (userChoicePos > 0) {
            btmBack.setText("⬅️ " + dates.get(userChoicePos - 1));
            btmBack.setCallbackData(dates.get(userChoicePos - 1));
            row1.add(btmBack);
            if (userChoicePos < dates.size() - 1) {
                btmForward.setText("➡️️ " + dates.get(userChoicePos + 1));
                btmForward.setCallbackData(dates.get(userChoicePos + 1));
                row1.add(btmForward);
            }
        } else { // userChoicePos == 0 || userChoicePos == -1
            btmForward.setText("➡️️ " + dates.get(1));
            btmForward.setCallbackData(dates.get(1));
            row1.add(btmForward);
        }

        row2.add(settingsButton);
        // rows
        List<List<InlineKeyboardButton>> rows = new LinkedList<>();
        rows.add(row1);
        rows.add(row2);
        // keyboard
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.setKeyboard(rows);

        if (callbackQuery != null) {
            editMessage(chatId, messageText, keyboard, callbackQuery.getMessage().getMessageId());
        } else
            sendMessageToUserWithKeyboard(chatId, messageText, keyboard);
    }

    private void editMessage(Long chatId, String text, InlineKeyboardMarkup keyboard, Integer messageId) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId.toString());
        editMessageText.setText(text);
        editMessageText.setMessageId(messageId);
        editMessageText.setReplyMarkup(keyboard);
        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            log.error(e.toString(), e);
        }
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

