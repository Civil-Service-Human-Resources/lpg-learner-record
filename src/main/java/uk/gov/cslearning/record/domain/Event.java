package uk.gov.cslearning.record.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import uk.gov.cslearning.record.dto.CancellationReason;
import uk.gov.cslearning.record.dto.EventStatus;

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

    @Column(nullable = false, unique = true)
    private String path;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @ToString.Exclude
    @OneToMany(mappedBy = "event")
    private List<Booking> bookings = new ArrayList<>();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CancellationReason cancellationReason;

    @JsonIgnore
    public List<Booking> getActiveBookings() {
        return this.bookings.stream().filter(b -> !b.getStatus().equals(BookingStatus.CANCELLED)).toList();
    }

    @JsonIgnore
    public EventIds getEventIds() {
        String[] parts = this.path.split("/");
        return new EventIds(parts[2], parts[4], parts[6]);
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
        bookings.forEach(b -> b.setEvent(this));
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
        booking.setEvent(this);
    }
}
