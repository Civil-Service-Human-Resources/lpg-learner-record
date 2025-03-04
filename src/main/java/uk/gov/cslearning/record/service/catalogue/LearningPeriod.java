package uk.gov.cslearning.record.service.catalogue;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class LearningPeriod implements Serializable {

    @Nullable
    private LocalDate startDate;
    private LocalDate endDate;
}
