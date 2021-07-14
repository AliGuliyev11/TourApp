package com.mycode.tourapptelegrambot.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycode.tourapptelegrambot.bot.TourAppBot;
import com.mycode.tourapptelegrambot.dto.Offer;
import com.mycode.tourapptelegrambot.models.MyUser;
import com.mycode.tourapptelegrambot.models.UserOffer;
import com.mycode.tourapptelegrambot.rabbitmq.orderOfferSend.rabbitservice.RabbitMQService;
import com.mycode.tourapptelegrambot.redis.RedisCache.OfferCache;
import com.mycode.tourapptelegrambot.redis.redisEntity.OfferCount;
import com.mycode.tourapptelegrambot.repositories.UserOfferRepo;
import com.mycode.tourapptelegrambot.utils.Emojis;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
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
    private final ObjectMapper mapper;
    private final ModelMapper modelMapper = new ModelMapper();
    private final TourAppBot tourAppBot;
    private final OfferCache offerCache;
    private final RabbitMQService rabbitStopService;

    public OfferService(UserOfferRepo userOfferRepo, ObjectMapper mapper, @Lazy TourAppBot tourAppBot, OfferCache offerCache,
                        RabbitMQService stopService) {
        this.userOfferRepo = userOfferRepo;
        this.mapper = mapper;
        this.tourAppBot = tourAppBot;
        this.offerCache = offerCache;
        this.rabbitStopService = stopService;
    }

    public void save(Offer offer, MyUser user, boolean isFive) {
        UserOffer userOffer = modelMapper.map(offer, UserOffer.class);
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        userOffer.setMyUser(user);
        userOffer.setFirstFive(isFive);
        userOfferRepo.save(userOffer);
    }

    @SneakyThrows
    public List<BotApiMethod<?>> loadMore(Long userId, String chatId) {
        List<UserOffer> offers = userOfferRepo.getUserOffersByMyUserId(userId).stream().limit(5).collect(Collectors.toList());
        List<BotApiMethod<?>> callbackAnswer = new ArrayList<>();
        for (UserOffer item : offers) {
            String text = "Agent:" + item.getAgencyName() + "\n" + item.getAgencyNumber() + Emojis.Phone;
            tourAppBot.sendOffer(item.getMyUser().getChatId(), item.getFile(),text,getAcceptButtons(item.getId()));
//            tourAppBot.Execute(SendMessage.builder().chatId(item.getMyUser().getChatId()).text(text).replyMarkup(getAcceptButtons(item.getId())).build());
            userOfferRepo.deleteById(item.getId());
        }
        if (!userOfferRepo.getUserOffersByMyUserId(userId).isEmpty()) {
            callbackAnswer.add(SendMessage.builder().chatId(chatId).text("A").replyMarkup(getLoadButtons()).build());
        } else {
            offerCache.save(OfferCount.builder().userId(userId).count(0).build());
            callbackAnswer.add(SendMessage.builder().chatId(chatId).text("B").build());
        }

        return callbackAnswer;

    }

    public UserOffer findById(Long offerId) {
        return userOfferRepo.findById(offerId).get();
    }

    public void acceptOffer(Long offerId) {
        System.out.println(offerId);
        rabbitStopService.reply(offerId);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void clearUserOffer(Long userId, String uuid) {

        if (!userOfferRepo.getUserOffersByMyUserId(userId).isEmpty()) {
            userOfferRepo.deleteAllByMyUserId(userId);
        }
        if (uuid != null) {
            rabbitStopService.stop(uuid);
        }
    }


}
