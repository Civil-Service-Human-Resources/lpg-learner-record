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
    private int id;

    @Column(nullable = false, length = 60, unique = true)
    private String catalogueId;

    @Column(nullable = false, unique = true)
    private String path;

    @ToString.Exclude
    @OneToMany(mappedBy = "event")
    private List<Booking> bookings = new ArrayList<>();
}
