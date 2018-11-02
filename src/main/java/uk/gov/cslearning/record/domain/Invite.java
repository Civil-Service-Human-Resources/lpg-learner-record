package uk.gov.cslearning.record.domain;

import javax.persistence.*;

@Entity
public class Invite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(cascade = CascadeType.ALL)
    private Event event;

    @Column(nullable = false, length = 50)
    private String learnerEmail;

    public Invite(){}

    public Invite(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getLearnerEmail() {
        return learnerEmail;
    }

    public void setLearnerEmail(String learnerEmail) {
        this.learnerEmail = learnerEmail;
    }

    @Override
    public String toString() {
        return "Invite{" +
                "id=" + id +
                ", event=" + event.toString() +
                ", learnerEmail='" + learnerEmail + '\'' +
                '}';
    }
}
