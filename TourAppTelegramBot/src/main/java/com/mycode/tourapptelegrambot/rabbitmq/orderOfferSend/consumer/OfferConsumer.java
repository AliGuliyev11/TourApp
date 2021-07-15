package com.mycode.tourapptelegrambot.rabbitmq.orderOfferSend.consumer;

import com.mycode.tourapptelegrambot.bot.TourAppBot;
import com.mycode.tourapptelegrambot.dto.Offer;
import com.mycode.tourapptelegrambot.models.MyUser;
import com.mycode.tourapptelegrambot.redis.RedisCache.OfferCache;
import com.mycode.tourapptelegrambot.redis.redisEntity.OfferCount;
import com.mycode.tourapptelegrambot.repositories.UserRepo;
import com.mycode.tourapptelegrambot.services.OfferService;
import com.mycode.tourapptelegrambot.utils.Emojis;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import static com.mycode.tourapptelegrambot.inlineButtons.AcceptOffer.getAcceptButtons;
import static com.mycode.tourapptelegrambot.inlineButtons.LoadMore.getLoadButtons;

@Service
public class OfferConsumer {

    private final TourAppBot telegramBot;
    private final UserRepo userRepo;
    private final OfferCache offerCache;
    private final OfferService offerService;

    public OfferConsumer(TourAppBot telegramBot, UserRepo userRepo, OfferCache offerCache, OfferService offerService) {
        this.telegramBot = telegramBot;
        this.userRepo = userRepo;
        this.offerCache = offerCache;
        this.offerService = offerService;
    }

    @SneakyThrows
    @RabbitListener(queues = "offerQueue")
    public void onMessage(Offer offer) {

        MyUser user = userRepo.getMyUserByUuid(offer.getUserId());
        if (user != null) {
            int count = offerCache.get(user.getId());
            count++;
            if (count == 6) {
                offerService.save(offer, user,false);
                telegramBot.execute(SendMessage.builder().chatId(user.getChatId()).text("A").replyMarkup(getLoadButtons()).build());
            } else if (count < 6) {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setPhoto(new InputFile().setMedia(offer.getFile()));
                sendPhoto.setChatId(user.getChatId());
                sendPhoto.setReplyMarkup(getAcceptButtons(offer.getId()));

                String text = "Agent:" + offer.getAgencyName() + "\n" + offer.getAgencyNumber() + Emojis.Phone;
                sendPhoto.setCaption(text);
                telegramBot.execute(sendPhoto);
                offerService.save(offer, user,true);
                offerCache.save(OfferCount.builder().userId(user.getId()).count(count).build());
            } else {
                offerService.save(offer, user,false);
            }
            offerCache.save(OfferCount.builder().userId(user.getId()).count(count).build());

        }

        System.out.println(offer);
    }
}
