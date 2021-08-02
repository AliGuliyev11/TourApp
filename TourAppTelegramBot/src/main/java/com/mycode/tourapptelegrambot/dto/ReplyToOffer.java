package com.mycode.tourapptelegrambot.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/** @author Ali Guliyev
 * @version 1.0
 * @implNote  This DTO for sending offer to agent*/

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
