package uk.gov.cslearning.record.domain.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.commons.lang3.math.NumberUtils;
import uk.gov.cslearning.record.domain.BookingStatus;

@Converter
public class BookingStatusConverter implements AttributeConverter<BookingStatus, String> {

    @Override
    public String convertToDatabaseColumn(BookingStatus bookingStatus) {
        return bookingStatus != null ? bookingStatus.name() : null;
    }

    @Override
    public BookingStatus convertToEntityAttribute(String value) {
        BookingStatus status = null;
        if (value != null) {
            if (NumberUtils.isCreatable(value)) {
                status = BookingStatus.values()[Integer.parseInt(value)];
            } else {
                status = BookingStatus.valueOf(value);
            }
        }
        return status;
    }
}
