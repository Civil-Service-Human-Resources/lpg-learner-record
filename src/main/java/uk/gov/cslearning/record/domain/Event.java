package uk.gov.cslearning.record.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String path;

    @ToString.Exclude
    @OneToMany(mappedBy = "event")
    private List<Booking> bookings = new ArrayList<>();

    public void addToBookings(Booking booking) {
        this.bookings.add(booking);
    }
}