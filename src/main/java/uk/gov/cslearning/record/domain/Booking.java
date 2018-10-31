package uk.gov.cslearning.record.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class Booking {

    @Column(nullable = false)
    private Long learnerId;

    @Column(nullable = false)
    private Long eventId;

    private String paymentDetails;

    @Column(nullable = false)
    private String status;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime bookingTime;

    public Booking() {}

    public Long getLearnerId() {
        return learnerId;
    }

    public void setLearnerId(Long learnerId) {
        this.learnerId = learnerId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(String paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "learnerId=" + learnerId +
                ", eventId=" + eventId +
                ", paymentDetails='" + paymentDetails + '\'' +
                ", status='" + status + '\'' +
                ", bookingTime=" + bookingTime +
                '}';
    }
}
