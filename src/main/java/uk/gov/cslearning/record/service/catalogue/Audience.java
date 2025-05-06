package uk.gov.cslearning.record.service.catalogue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Slf4j
public class Audience {
    private List<String> areasOfWork;
    private List<String> departments;
    private List<String> grades;
    private String frequency;
    private Type type;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate requiredBy;

    @JsonIgnore
    public Optional<Period> getFrequencyAsPeriod() {
        if (!StringUtils.isBlank(frequency)) {
            return Optional.of(Period.parse(frequency));
        }
        return Optional.empty();
    }

    public enum Type {
        OPEN,
        CLOSED_COURSE,
        PRIVATE_COURSE,
        REQUIRED_LEARNING
    }
}
