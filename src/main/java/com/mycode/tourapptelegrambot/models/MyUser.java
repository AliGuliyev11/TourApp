package com.mycode.tourapptelegrambot.models;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

/**
 * @author Ali Guliyev
 * @version 1.0
 */

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class MyUser {
    @Id
    Long id;
    String chatId;
    String uuid;
    String phoneNumber;

    @Override
    public String toString() {
        return "MyUser{" +
                "id=" + id +
                ", chatId='" + chatId + '\'' +
                ", uuid='" + uuid + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
