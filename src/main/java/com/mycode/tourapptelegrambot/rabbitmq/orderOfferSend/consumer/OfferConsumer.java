package com.mycode.tourapptelegrambot.rabbitmq.orderOfferSend.consumer;

import com.mycode.tourapptelegrambot.bot.TourAppBot;
import com.mycode.tourapptelegrambot.dto.Offer;
import com.mycode.tourapptelegrambot.dto.WarningDto;
import com.mycode.tourapptelegrambot.models.MyUser;
import com.mycode.tourapptelegrambot.redis.RedisCache.OfferCache;
import com.mycode.tourapptelegrambot.redis.RedisCache.OrderCache;
import com.mycode.tourapptelegrambot.redis.redisEntity.OfferCount;
import com.mycode.tourapptelegrambot.repositories.BotMessageRepo;
import com.mycode.tourapptelegrambot.repositories.UserRepo;
import com.mycode.tourapptelegrambot.services.OfferService;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import static com.mycode.tourapptelegrambot.inlineButtons.AcceptOffer.getAcceptButtons;
import static com.mycode.tourapptelegrambot.inlineButtons.LoadMore.getLoadButtons;

/**
 * @author Ali Guliyev
 * @version 1.0
 */

@Service
public class OfferConsumer {

    private final TourAppBot telegramBot;
    private final UserRepo userRepo;
    private final OfferCache offerCache;
    private final OfferService offerService;
    private final OrderCache orderCache;
    private final BotMessageRepo botMessageRepo;

    public OfferConsumer(TourAppBot telegramBot, UserRepo userRepo, OfferCache offerCache, OfferService offerService,
                         OrderCache orderCache, BotMessageRepo botMessageRepo) {
        this.telegramBot = telegramBot;
        this.userRepo = userRepo;
        this.offerCache = offerCache;
        this.offerService = offerService;
        this.orderCache = orderCache;
        this.botMessageRepo=botMessageRepo;
    }

    @Value("${offer.count}")
    private int maxOfferCount;

    @Value("${offer.path}")
    private String offerPath;

    /** This method for getting agents offer
     * @implNote If offer count bigger than 5 sending load more button
     * @param offer agent's offer*/

    @SneakyThrows
    @RabbitListener(queues = "offerQueue")
    public void onMessage(Offer offer) {

        MyUser user = userRepo.getMyUserByUuid(offer.getUserId());
        if (user != null) {
            int count = offerCache.get(user.getId());
            System.out.println(count);
            count++;
            if (count == maxOfferCount) {
                FileUtils.writeByteArrayToFile(ResourceUtils.getFile(offerPath),offer.getFile());
                offerService.save(offer, user, false);
                telegramBot.execute(SendMessage.builder().chatId(user.getChatId()).text("\u2B07\uFE0F")
                        .replyMarkup(getLoadButtons(orderCache.get(user.getId()), botMessageRepo)).build());
            } else if (count < maxOfferCount) {
                FileUtils.writeByteArrayToFile(ResourceUtils.getFile(offerPath),offer.getFile());
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setPhoto(new InputFile().setMedia(ResourceUtils.getFile(offerPath)));
                sendPhoto.setChatId(user.getChatId());
                sendPhoto.setReplyMarkup(getAcceptButtons(offer.getOfferId(), orderCache.get(user.getId()), botMessageRepo));
                telegramBot.execute(sendPhoto);
                offerService.save(offer, user, true);
                offerCache.save(OfferCount.builder().userId(user.getId()).count(count).build());
            } else {
                FileUtils.writeByteArrayToFile(ResourceUtils.getFile(offerPath),offer.getFile());
                offerService.save(offer, user, false);
            }
            offerCache.save(OfferCount.builder().userId(user.getId()).count(count).build());

        }

        System.out.println(offer);
    }

    /** This method for sendind warning message from agent
     * @implNote If user request expired sending warning message to user
     * @param warningDto agent's warning message*/

    @SneakyThrows
    @RabbitListener(queues = "offerMadeQueue")
    public void warningConsumer(WarningDto warningDto) {
            MyUser user = userRepo.getMyUserByUuid(warningDto.getUserId());
            if (user!=null){
                telegramBot.execute(SendMessage.builder().chatId(user.getChatId()).text(warningDto.getText()).build());
            }
    }
}
