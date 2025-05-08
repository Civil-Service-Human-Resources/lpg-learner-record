package uk.gov.cslearning.record.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FromToParams {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    protected LocalDate from;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    protected LocalDate to;
}
