package uk.gov.cslearning.record.domain;

import javax.persistence.*;

@Entity
public class Invite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int eventId;

    @Column(nullable = false)
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

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
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
                ", eventId=" + eventId +
                ", learnerEmail='" + learnerEmail + '\'' +
                '}';
    }
}
