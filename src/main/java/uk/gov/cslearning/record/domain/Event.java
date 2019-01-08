package uk.gov.cslearning.record.domain;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import uk.gov.cslearning.record.dto.CancellationReason;
import uk.gov.cslearning.record.dto.EventStatus;

import javax.persistence.*;
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
}
