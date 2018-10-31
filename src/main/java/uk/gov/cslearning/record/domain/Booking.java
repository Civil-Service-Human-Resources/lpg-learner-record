package uk.gov.cslearning.record.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Booking {

    @Id
    private Long id;

    @Column(nullable = false)
    private Long learnerId;

    @Column(nullable = false)
    private Long eventId;

    @Column
    private String paymentDetails;

    @Column(nullable = false)
    private String status;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime bookingTime;

    public Booking() {}

    public Booking(Long id){
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setBookingTime(LocalDateTime booking_time) {
        this.bookingTime = booking_time;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", eventId=" + eventId +
                ", learnerId=" + learnerId +
                ", paymentDetails='" + paymentDetails + '\'' +
                ", status='" + status + '\'' +
                ", booking_time=" + bookingTime +
                '}';
    }
}
