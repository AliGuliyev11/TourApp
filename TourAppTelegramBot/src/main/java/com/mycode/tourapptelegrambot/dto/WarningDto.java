package com.mycode.tourapptelegrambot.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

/** @author Ali Guliyev
 * @version 1.0
 * @implNote  This DTO for getting warning message from agent*/

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
