package uk.gov.cslearning.record.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Learner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 60)
    private String uid;

    @Column(nullable = false, length = 60)
    private String learnerEmail;
}
