package uk.gov.cslearning.record.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="learnerId")
    private Learner learner;

    @ManyToOne
    @JoinColumn(name="eventId")
    private Event event;

    private String paymentDetails;

    @Column(nullable = false)
    private String status;

    private LocalDateTime bookingTime;
}
