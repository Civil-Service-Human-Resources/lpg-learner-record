package uk.gov.cslearning.record.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import uk.gov.cslearning.record.dto.CancellationReason;
import uk.gov.cslearning.record.dto.EventStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 60, unique = true)
    private String uid;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @ToString.Exclude
    @OneToMany(mappedBy = "event")
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "event")
    private List<Invite> invites = new ArrayList<>();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CancellationReason cancellationReason;

    @JsonIgnore
    public void cancel(CancellationReason reason, Instant cancellationTime) {
        setCancellationReason(reason);
        getBookings().forEach(b -> {
            b.setStatus(BookingStatus.CANCELLED);
            b.setCancellationTime(cancellationTime);
        });
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
        bookings.forEach(b -> b.setEvent(this));
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
        booking.setEvent(this);
    }

    public boolean isLearnerBooked(String learnerUid) {
        return this.bookings.stream().anyMatch(b -> b.getLearnerUid().equals(learnerUid));
    }

    public boolean isLearnerInvited(String learnerUid) {
        return this.invites.stream().anyMatch(b -> b.getLearnerUid().equals(learnerUid));
    }

    public boolean isLearnerBookedOrInvited(String learnerUid) {
        return isLearnerBooked(learnerUid) || isLearnerInvited(learnerUid);
    }
}
