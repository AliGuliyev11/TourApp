package com.mycode.tourapptelegrambot.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.File;
import java.io.Serializable;
import java.util.Arrays;

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
