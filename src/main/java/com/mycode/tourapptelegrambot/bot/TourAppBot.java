package com.mycode.tourapptelegrambot.bot;

import com.mycode.tourapptelegrambot.bot.botfacade.TelegramFacade;
import com.mycode.tourapptelegrambot.bot.commands.ContinueCommand;
import com.mycode.tourapptelegrambot.bot.commands.StartCommand;
import com.mycode.tourapptelegrambot.bot.commands.StopCommand;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.io.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Telegram bot class where Webhook extends and setted
 *
 * @author Ali Guliyev
 * @version 1.0
 */

public class TourAppBot extends TelegramWebhookBot {


    String botToken;
    String botUsername;
    String webhookPath;
    TelegramFacade telegramFacade;

    public TourAppBot(TelegramFacade telegramFacade) {
        this.telegramFacade = telegramFacade;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    /**
     * Every requests enters this method
     */

    @SneakyThrows
    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {

        final BotApiMethod<?> replyMessageToUser = telegramFacade.handleUpdate(update);

        return replyMessageToUser;
    }


    public void Execute(BotApiMethod<?> replyMessageToUser) throws TelegramApiException {
        execute(replyMessageToUser);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotPath() {
        return webhookPath;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public void setBotUsername(String botUsername) {
        this.botUsername = botUsername;
    }

    public void setWebhookPath(String webhookPath) {
        this.webhookPath = webhookPath;
    }

    @Value("${start.case.file}")
    String fileDestination;

    /**
     * Method for sending photo
     *
     * @param chatId       current private chat id
     * @param imageCaption caption of image
     * @param imagePath    image path
     */

    @SneakyThrows
    public void sendPhoto(String chatId, String imageCaption, String imagePath) {
        File image;
        try {
            image = ResourceUtils.getFile(imagePath);
        } catch (IOException e) {
            saveImage(imagePath, fileDestination);
            image = ResourceUtils.getFile(fileDestination);
        }
        InputFile inputFile = new InputFile();
        inputFile.setMedia(image);
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setPhoto(inputFile);
        sendPhoto.setChatId(chatId);
        sendPhoto.setCaption(imageCaption);
        try {
            execute(sendPhoto);
        } catch (Exception e) {
            sendPhoto.setPhoto(new InputFile().setMedia(ResourceUtils.getFile(fileDestination)));
            execute(sendPhoto);
        }


    }

    /**
     * This method for save image to destination file
     *
     * @param imageUrl        imageUrl from URL address
     * @param destinationFile where be added destination file
     */

    public static void saveImage(String imageUrl, String destinationFile) throws IOException {
        try {
            URL url = new URL(imageUrl);
            InputStream is = url.openStream();
            OutputStream os = new FileOutputStream(destinationFile);
            byte[] b = new byte[2048];
            int length;
            while ((length = is.read(b)) != -1) {
                os.write(b, 0, length);
            }
            is.close();
            os.close();
        } catch (Exception ignored) {

        }

    }


    /**
     * Setting bot commands
     */

    @SneakyThrows
    @PostConstruct
    public void botCommands() {
        List<BotCommand> botCommands = new ArrayList<>();
        botCommands.add(new StartCommand());
        botCommands.add(new ContinueCommand());
        botCommands.add(new StopCommand());
        execute(SetMyCommands.builder().commands(botCommands).build());
    }

    /**
     * Sending offer to user
     *
     * @param chatId        current private chat id
     * @param image         agent's offer travel package image
     * @param acceptButtons inline keyboard for accept travel package
     */

    @SneakyThrows
    public void sendOffer(String chatId, File image, InlineKeyboardMarkup acceptButtons) {
        SendPhoto sendPhoto = new SendPhoto();
        InputFile inputFile = new InputFile();
        inputFile.setMedia(image);
        sendPhoto.setPhoto(inputFile);
        sendPhoto.setChatId(chatId);
        sendPhoto.setReplyMarkup(acceptButtons);
        execute(sendPhoto);
    }

}
