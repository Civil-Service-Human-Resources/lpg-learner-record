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
public class Learner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String uuid;

    @ToString.Exclude
    @OneToMany(mappedBy = "learner")
    private List<Booking> bookings = new ArrayList<>();
}
