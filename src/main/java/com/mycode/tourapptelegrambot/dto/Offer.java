package com.mycode.tourapptelegrambot.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

/**
 * @author Ali Guliyev
 * @version 1.0
 * @implNote This DTO for getting offer from agent
 */


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Offer implements Serializable {
    Long offerId;
    String userId;
    byte[] file;

    @Override
    public String toString() {
        return "Offer{" +
                "offerId=" + offerId +
                ", userId='" + userId + '\'' +
                '}';
    }
}
