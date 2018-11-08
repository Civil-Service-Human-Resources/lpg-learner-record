package uk.gov.cslearning.record.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name="learnerId")
    private Learner learner;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name="eventId")
    private Event event;

    @Column
    private String paymentDetails;

    @Column(nullable = false, length = 9)
    private String status;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private Instant bookingTime;
}
