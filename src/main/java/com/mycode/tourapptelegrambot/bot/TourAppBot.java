package com.mycode.tourapptelegrambot.bot;

import com.mycode.tourapptelegrambot.bot.botfacade.TelegramFacade;
import com.mycode.tourapptelegrambot.bot.commands.ContinueCommand;
import com.mycode.tourapptelegrambot.bot.commands.StartCommand;
import com.mycode.tourapptelegrambot.bot.commands.StopCommand;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.io.*;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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

    @Value("${voice.path}")
    String path;

    /**
     * Speech to text
     *
     * @param voice user voice
     * @apiNote this method for download user voice file
     */

    @SneakyThrows
    public void voice(Voice voice) {
        GetFile getFile = new GetFile();
        getFile.setFileId(voice.getFileId());
        String filePath = execute(getFile).getFilePath();
        File file = downloadFile(filePath, new File(path));
        System.out.println(file.getName());
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
        ClassLoader cl = this.getClass().getClassLoader();


        File image = null;
        if (!image.exists()) {
            image.mkdirs();
        }
        try {
            InputStream inputStream = cl.getResourceAsStream(imagePath);
            image =new InputStreamResource(inputStream).getFile();
        } catch (IOException e) {
            saveImage(imagePath, fileDestination);
            InputStream inputStream = cl.getResourceAsStream(fileDestination);
            image = new InputStreamResource(inputStream).getFile();
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
            InputStream inputStream = cl.getResourceAsStream(fileDestination);
            sendPhoto.setPhoto(new InputFile().setMedia(new InputStreamResource(inputStream).getFile()));
            execute(sendPhoto);
        }

    }

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
        } catch (Exception e) {

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

    @SneakyThrows
    public void sendDocument(String chatId, String caption, File sendFile) {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(chatId);
        sendDocument.setCaption(caption);
        InputFile inputFile = new InputFile();
        inputFile.setMedia(sendFile);
        sendDocument.setDocument(inputFile);
        execute(sendDocument);
    }
}
