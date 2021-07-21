package com.mycode.tourapptelegrambot.models;

import com.mycode.tourapptelegrambot.enums.Languages;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.joda.time.LocalDate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Entity(name = "myOrder")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Order implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "language")
    Languages language;
    @Column(name = "travel_type")
    String Ordertravel;
    @Column(name = "where_from")
    String Orderaddress1;
    @Column(name = "where_to")
    String Orderaddress2;
    @Column(name = "order_date")
    Date Orderdate;
    @Column(name = "traveller_count")
    int Ordertraveller;
    @Column(name = "budget")
    int Orderbudget;
    @Column(name = "travel_time")
    int Orderdateto;
    String chatId;
    String userId;
    Date createdDate;

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", language=" + language +
                ", Ordertravel='" + Ordertravel + '\'' +
                ", Orderaddress1='" + Orderaddress1 + '\'' +
                ", Orderaddress2='" + Orderaddress2 + '\'' +
                ", Orderdate=" + Orderdate +
                ", Ordertraveller=" + Ordertraveller +
                ", Orderbudget=" + Orderbudget +
                ", Orderdateto=" + Orderdateto +
                ", chatId='" + chatId + '\'' +
                ", userId='" + userId + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}
