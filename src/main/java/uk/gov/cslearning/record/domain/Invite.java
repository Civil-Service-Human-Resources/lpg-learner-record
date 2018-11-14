package uk.gov.cslearning.record.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Invite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name="eventId")
    private Event event;

    @Column(nullable = false, length = 50)
    private String learnerEmail;
}
