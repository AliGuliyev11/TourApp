package com.mycode.tourapptelegrambot.models;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String userId;
    Long offerId;
    File file;
    @ManyToOne
    MyUser myUser;
    boolean isFirstFive;

    @Override
    public String toString() {
        return "UserOffer{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", file=" + file +
                ", myUser=" + myUser +
                ", isFirstFive=" + isFirstFive +
                '}';
    }
}
