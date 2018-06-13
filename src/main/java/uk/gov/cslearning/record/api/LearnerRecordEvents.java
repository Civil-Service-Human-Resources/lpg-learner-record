package uk.gov.cslearning.record.api;

import uk.gov.cslearning.record.domain.BookingStatus;
import uk.gov.cslearning.record.domain.PaymentMethod;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

public class LearnerRecordEvents {

    private String bookingReference;

    private String courseIdentifier;

    private String courseName;

    private String courseCost;

    private String eventId;

    private LocalDateTime eventDate;

    private String delegateName;

    private String delegateEmail;

    private BookingStatus bookingStatus;

    private LocalDateTime bookingStatusDate;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public String getDelegateName() {
        return delegateName;
    }

    public void setDelegateName(String delegateName) {
        this.delegateName = delegateName;
    }

    public String getDelegateEmail() {
        return delegateEmail;
    }

    public void setDelegateEmail(String delegateEmail) {
        this.delegateEmail = delegateEmail;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public LocalDateTime getBookingStatusDate() {
        return bookingStatusDate;
    }

    public void setBookingStatusDate(LocalDateTime bookingStatusDate) {
        this.bookingStatusDate = bookingStatusDate;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPurchaseOrderNumber() {
        return purchaseOrderNumber;
    }

    public void setPurchaseOrderNumber(String purchaseOrderNumber) {
        this.purchaseOrderNumber = purchaseOrderNumber;
    }

    public String getFapApproverEmail() {
        return fapApproverEmail;
    }

    public void setFapApproverEmail(String fapApproverEmail) {
        this.fapApproverEmail = fapApproverEmail;
    }

    @Enumerated(EnumType.STRING)

    private PaymentMethod paymentMethod;

    private String purchaseOrderNumber;

    private String fapApproverEmail;

    public String getBookingReference() {
        return bookingReference;
    }

    public void setBookingReference(String bookingReference) {
        this.bookingReference = bookingReference;
    }

    public String getCourseIdentifier() {
        return courseIdentifier;
    }

    public void setCourseIdentifier(String courseIdentifier) {
        this.courseIdentifier = courseIdentifier;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseCost() {
        return courseCost;
    }

    public void setCourseCost(String courseCost) {
        this.courseCost = courseCost;
    }
}
