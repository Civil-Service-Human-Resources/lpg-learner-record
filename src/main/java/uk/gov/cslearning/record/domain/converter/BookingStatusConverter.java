package uk.gov.cslearning.record.domain.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.commons.lang3.math.NumberUtils;
import uk.gov.cslearning.record.domain.BookingStatus;

@Converter
public class BookingStatusConverter implements AttributeConverter<BookingStatus, String> {

    @Override
    public String convertToDatabaseColumn(BookingStatus bookingStatus) {
        return bookingStatus.name();
    }

    @Override
    public BookingStatus convertToEntityAttribute(String value) {
        if (NumberUtils.isCreatable(value)) {
            return BookingStatus.values()[Integer.parseInt(value)];
        } else {
            return BookingStatus.valueOf(value);
        }
    }
}
