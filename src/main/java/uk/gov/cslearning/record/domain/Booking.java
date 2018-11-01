package uk.gov.cslearning.record.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int learnerId;

    @Column(nullable = false)
    private int eventId;

    @Column
    private String paymentDetails;

    @Column(nullable = false)
    private String status;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime bookingTime;

    public Booking() {}

    public Booking(int id){
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

    public int getLearnerId() {
        return learnerId;
    }

    public void setLearnerId(int learnerId) {
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
