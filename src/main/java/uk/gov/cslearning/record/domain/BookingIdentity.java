package uk.gov.cslearning.record.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class BookingIdentity implements Serializable {

    @Column(nullable = false)
    private Long eventId;

    @Column(nullable = false)
    private Long learnerId;

    public BookingIdentity() {}

    public BookingIdentity(Long eventId, Long learnerId){
        this.eventId = eventId;
        this.learnerId = learnerId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getLearnerId() {
        return learnerId;
    }

    public void setLearnerId(Long learnerId) {
        this.learnerId = learnerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingIdentity that = (BookingIdentity) o;
        return Objects.equals(eventId, that.eventId) &&
                Objects.equals(learnerId, that.learnerId);
    }
}
