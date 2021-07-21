package com.mycode.tourapptelegrambot.services;

import com.mycode.tourapptelegrambot.bot.TourAppBot;
import com.mycode.tourapptelegrambot.dto.Offer;
import com.mycode.tourapptelegrambot.dto.ReplyToOffer;
import com.mycode.tourapptelegrambot.models.MyUser;
import com.mycode.tourapptelegrambot.models.UserOffer;
import com.mycode.tourapptelegrambot.rabbitmq.orderOfferSend.rabbitservice.RabbitMQService;
import com.mycode.tourapptelegrambot.redis.RedisCache.OfferCache;
import com.mycode.tourapptelegrambot.redis.RedisCache.OrderCache;
import com.mycode.tourapptelegrambot.redis.redisEntity.OfferCount;
import com.mycode.tourapptelegrambot.repositories.UserOfferRepo;
import com.mycode.tourapptelegrambot.utils.Emojis;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.mycode.tourapptelegrambot.inlineButtons.AcceptOffer.getAcceptButtons;
import static com.mycode.tourapptelegrambot.inlineButtons.LoadMore.getLoadButtons;

@Service
public class OfferService {

    private final UserOfferRepo userOfferRepo;
    private final ModelMapper modelMapper = new ModelMapper();
    private final TourAppBot tourAppBot;
    private final OfferCache offerCache;
    private final OrderCache orderCache;
    private final RabbitMQService rabbitStopService;
    private final LocaleMessageService messageService;

    public OfferService(UserOfferRepo userOfferRepo, @Lazy TourAppBot tourAppBot, OfferCache offerCache,
                        RabbitMQService stopService, OrderCache orderCache, LocaleMessageService messageService) {
        this.userOfferRepo = userOfferRepo;
        this.tourAppBot = tourAppBot;
        this.offerCache = offerCache;
        this.rabbitStopService = stopService;
        this.orderCache = orderCache;
        this.messageService = messageService;
    }

    public void save(Offer offer, MyUser user, boolean isFive) {
        UserOffer userOffer = new UserOffer();
        userOffer.setUserId(offer.getUserId());
        userOffer.setOfferId(offer.getOfferId());
        userOffer.setFile(offer.getFile());
        userOffer.setMyUser(user);
        userOffer.setFirstFive(isFive);
        userOfferRepo.save(userOffer);
    }


    @Value("${offer.count}")
    private int maxOfferCount;
    @SneakyThrows
    public List<BotApiMethod<?>> loadMore(Long userId, String chatId) {
        List<UserOffer> offers = userOfferRepo.getUserOffersByMyUserId(userId).stream().limit(maxOfferCount-1).collect(Collectors.toList());

        for (UserOffer item : offers) {
            tourAppBot.sendOffer(item.getMyUser().getChatId(), item.getFile(), getAcceptButtons(item.getOfferId(),
                    orderCache.get(userId), messageService));
            userOfferRepo.deleteById(item.getId());
        }

        List<BotApiMethod<?>> callbackAnswer =checkUserOfferAvailability(userId,chatId);

        saveOfferCache(userId, offers.size());
        return callbackAnswer;
    }

    private List<BotApiMethod<?>> checkUserOfferAvailability(Long userId, String chatId) {
        List<BotApiMethod<?>> callbackAnswer=new ArrayList<>();
        if (!userOfferRepo.getUserOffersByMyUserId(userId).isEmpty()) {
            callbackAnswer.add(SendMessage.builder().chatId(chatId).text("\u2B07\uFE0F")
                    .replyMarkup(getLoadButtons(orderCache.get(userId), messageService)).build());
        } else {
            offerCache.save(OfferCount.builder().userId(userId).count(0).build());
            callbackAnswer.add(SendMessage.builder().chatId(chatId)
                    .text(messageService.getMessage("no.more.load", orderCache.get(userId).getLanguage())).build());
        }
        return callbackAnswer;
    }



    private void saveOfferCache(Long userId, int size) {
        if (size == maxOfferCount) {
            offerCache.save(OfferCount.builder().userId(userId).count(size + 1).build());
        } else {
            int count=offerCache.get(userId)>=maxOfferCount?0:offerCache.get(userId);
            int offerCount=count+size;
            offerCache.save(OfferCount.builder().userId(userId).count(offerCount!=5?offerCount:offerCount+1).build());
        }
    }


    public boolean checkUserOffer(Long userId) {
        return userOfferRepo.checkUserOffer(userId);
    }


    public UserOffer findById(Long offerId) {
        return userOfferRepo.findById(offerId).get();
    }

    public void acceptOffer(Long offerId,String phoneNumber) {
        System.out.println(offerId);
        ReplyToOffer replyToOffer= ReplyToOffer.builder().offerId(offerId).phoneNumber(phoneNumber).build();
        rabbitStopService.reply(replyToOffer);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void clearUserOffer(Long userId, String uuid) {

        if (!userOfferRepo.checkUserOffersByMyUserId(userId).isEmpty()) {
            userOfferRepo.deleteAllByMyUserId(userId);
        }
        if (uuid != null) {
            rabbitStopService.stop(uuid);
        }
    }


}
