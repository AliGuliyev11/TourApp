package com.mycode.tourapptelegrambot.models;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.File;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserOffer {

    @Id
    Long id;
    String userId;
    File file;
    String agencyName;
    String agencyNumber;
    @ManyToOne
    MyUser myUser;
    boolean isFirstFive;
}
