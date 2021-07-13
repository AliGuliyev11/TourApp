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
    Long id;
    String userId;
    File file;
    String agencyName;
    String agencyNumber;

    @Override
    public String toString() {
        return "Offer{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", file=" + file +
                ", agencyName='" + agencyName + '\'' +
                ", agencyNumber='" + agencyNumber + '\'' +
                '}';
    }
}
