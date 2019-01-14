package uk.gov.cslearning.record.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cslearning.record.dto.BookingCancellationReason;
import uk.gov.cslearning.record.dto.BookingStatus;

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
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private Instant bookingTime;

    @Column
    private String poNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingCancellationReason cancellationReason;
}
