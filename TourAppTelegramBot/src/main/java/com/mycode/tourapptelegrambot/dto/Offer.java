package com.mycode.tourapptelegrambot.dto;


import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.File;
import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Offer implements Serializable {
    Long offerId;
    String userId;
    File file;

    @Override
    public String toString() {
        return "Offer{" +
                "offerId=" + offerId +
                ", userId='" + userId + '\'' +
                ", file=" + file +
                '}';
    }
}
