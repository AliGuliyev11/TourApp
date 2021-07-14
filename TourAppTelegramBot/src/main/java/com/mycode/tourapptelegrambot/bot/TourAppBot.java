package com.mycode.tourapptelegrambot.bot;

import com.mycode.tourapptelegrambot.bot.botfacace.TelegramFacade;
import com.mycode.tourapptelegrambot.bot.commands.ContinueCommand;
import com.mycode.tourapptelegrambot.bot.commands.NewCommand;
import com.mycode.tourapptelegrambot.bot.commands.StartCommand;
import com.mycode.tourapptelegrambot.bot.commands.StopCommand;
import lombok.SneakyThrows;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.commands.GetMyCommands;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatDescription;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TourAppBot extends TelegramWebhookBot {


    String botToken;
    String botUsername;
    String webhookPath;
    TelegramFacade telegramFacade;

    public TourAppBot(TelegramFacade telegramFacade) {
//        super(options);
        this.telegramFacade = telegramFacade;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @SneakyThrows
    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {

        final BotApiMethod<?> replyMessageToUser = telegramFacade.handleUpdate(update);

        return replyMessageToUser;
    }


    public void Execute(BotApiMethod<?> replyMessageToUser) throws TelegramApiException {
        execute(replyMessageToUser);
    }

    @SneakyThrows
    public void voice(Voice voice) {
        GetFile getFile = new GetFile();
        getFile.setFileId(voice.getFileId());
        String filePath = execute(getFile).getFilePath();
        File file = downloadFile(filePath, new File("src/main/resources/static/docs/audio-file2.flac"));
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

    @SneakyThrows
    public void sendPhoto(String chatId, String imageCaption, String imagePath) {
        File image = ResourceUtils.getFile(imagePath);
        InputFile inputFile = new InputFile();
        inputFile.setMedia(image);
        Objects.requireNonNull(image.getName(), "photoName cannot be null!");
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setPhoto(inputFile);
        sendPhoto.setChatId(chatId);
        sendPhoto.setCaption(imageCaption);
        execute(sendPhoto);
    }

    @SneakyThrows
    @PostConstruct
    public void botCommands() {
        List<BotCommand> botCommands = new ArrayList<>();
        botCommands.add(new StartCommand());
        botCommands.add(new NewCommand());
        botCommands.add(new ContinueCommand());
        botCommands.add(new StopCommand());
        execute(SetMyCommands.builder().commands(botCommands).build());
    }

    @SneakyThrows
    public void sendOffer(String chatId, File image, String caption, InlineKeyboardMarkup acceptButtons) {
        SendPhoto sendPhoto = new SendPhoto();
        InputFile inputFile = new InputFile();
        inputFile.setMedia(image);
        sendPhoto.setPhoto(inputFile);
        sendPhoto.setChatId(chatId);
        sendPhoto.setCaption(caption);
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
