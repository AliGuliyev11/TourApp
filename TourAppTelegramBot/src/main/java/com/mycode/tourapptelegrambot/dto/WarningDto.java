package com.mycode.tourapptelegrambot.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class WarningDto implements Serializable {
    String text;
    String userId;

    @Override
    public String toString() {
        return "WarningDto{" +
                "text='" + text + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
