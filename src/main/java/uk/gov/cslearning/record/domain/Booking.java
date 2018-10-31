package uk.gov.cslearning.record.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Booking {

    @JsonIgnore
    @EmbeddedId
    private BookingIdentity identity;

    private String paymentDetails;

    @Column(nullable = false)
    private String status;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime bookingTime;

    public Booking() {}

    public Booking(Long learnerId, Long eventId){
        this.identity = new BookingIdentity(learnerId, eventId);
    }

    public BookingIdentity getIdentity() {
        return identity;
    }

    public void setIdentity(BookingIdentity identity) {
        this.identity = identity;
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
                "identity=" + identity +
                ", paymentDetails='" + paymentDetails + '\'' +
                ", status='" + status + '\'' +
                ", bookingTime=" + bookingTime +
                '}';
    }
}
