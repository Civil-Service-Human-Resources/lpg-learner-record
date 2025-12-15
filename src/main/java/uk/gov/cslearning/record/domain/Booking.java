package uk.gov.cslearning.record.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cslearning.record.dto.BookingCancellationReason;

import java.time.Instant;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "learnerId")
    private Learner learner;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "eventId")
    private Event event;

    @Column
    private String paymentDetails;

    @Column(nullable = false, length = 9)
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private Instant bookingTime;

    private Instant confirmationTime;

    private Instant cancellationTime;

    @Column
    private String poNumber;

    @Column
    private String accessibilityOptions;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingCancellationReason cancellationReason;

    @Column
    private String bookingReference;

    @Column
    private String learnerUid;

    public void setLearner(Learner learner) {
        this.learner = learner;
        this.learnerUid = learner.getUid();
    }

    public String getAccessibilityOptionsString() {
        return Objects.requireNonNullElse(accessibilityOptions, "");
    }
}
