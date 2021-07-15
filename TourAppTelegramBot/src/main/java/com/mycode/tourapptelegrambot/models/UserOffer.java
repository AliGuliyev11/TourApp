package com.mycode.tourapptelegrambot.models;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.File;
import java.util.Objects;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode
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

    @Override
    public String toString() {
        return "UserOffer{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", file=" + file +
                ", agencyName='" + agencyName + '\'' +
                ", agencyNumber='" + agencyNumber + '\'' +
                ", myUser=" + myUser +
                ", isFirstFive=" + isFirstFive +
                '}';
    }

}
