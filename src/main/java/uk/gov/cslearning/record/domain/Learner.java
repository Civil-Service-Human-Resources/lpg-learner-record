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
    private int id;

    @Column(nullable = false, length = 60)
    private String uid;

    @Column(nullable = false, length = 150)
    private String learnerEmail;

    @ToString.Exclude
    @OneToMany(mappedBy = "learner")
    private List<Booking> bookings = new ArrayList<>();
}
