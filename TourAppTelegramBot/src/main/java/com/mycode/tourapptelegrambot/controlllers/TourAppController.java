package com.mycode.tourapptelegrambot.controlllers;

import com.mycode.tourapptelegrambot.bot.TourAppBot;
import com.mycode.tourapptelegrambot.models.MyUser;
import com.mycode.tourapptelegrambot.repositories.UserRepo;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.FileOutputStream;

@RestController
public class TourAppController {

    private final TourAppBot telegramBot;
    UserRepo userRepo;

    public TourAppController(TourAppBot telegramBot, UserRepo userRepo) {
        this.telegramBot = telegramBot;
        this.userRepo = userRepo;
    }

    @SneakyThrows
    @PostMapping("offer/{id}")
    public ResponseEntity<BotApiMethod<?>> sendOffer(@PathVariable String id, @RequestParam(value = "File") MultipartFile file) {

        MyUser user = userRepo.getMyUserByUuid(id);
        if (user != null) {

            File myFile = new File("src/main/resources/static/docs/image.png");
            myFile.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(myFile);
            fileOutputStream.write(file.getBytes());
            fileOutputStream.close();
            File photo = new File(myFile.getAbsolutePath());

            SendPhoto sendPhoto = new SendPhoto().setPhoto(photo);
            sendPhoto.setChatId(user.getChatId());
            telegramBot.execute(sendPhoto);
            return new ResponseEntity<>(new SendMessage(user.getChatId(), ""), HttpStatus.OK);
        }

//        return new ResponseEntity<>("" , HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }


    @PostMapping
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return telegramBot.onWebhookUpdateReceived(update);
//            return new SendMessage(update.getMessage().getChatId(),"Salam");
    }
}
