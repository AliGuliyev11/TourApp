package com.mycode.tourapptelegrambot.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder(toBuilder = true)
public class ReplyToOffer implements Serializable {
    Long offerId;
    String phoneNumber;

    @Override
    public String toString() {
        return "ReplyToOffer{" +
                "offerId=" + offerId +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
