package uk.gov.cslearning.record.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import uk.gov.cslearning.record.domain.converter.BookingStatusConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.google.gson.internal.$Gson$Preconditions.checkArgument;

@Entity
@Getter
@Setter
public class ModuleRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, unique = true)
    private String uid;

    @Column(nullable = false)
    @NotBlank(message = "moduleId is required")
    private String moduleId;

    @NotBlank(message = "ModuleTitle is required")
    private String moduleTitle;

    @NotBlank(message = "moduleType is required")
    private String moduleType;

    private Long duration;

    private String eventId;

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate eventDate;

    @NotNull(message = "optional is required")
    private Boolean optional = Boolean.FALSE;

    private BigDecimal cost;

    @Enumerated(EnumType.STRING)
    @NotNull
    private State state;

    @Enumerated(EnumType.STRING)
    private Result result;

    private String score;

    private Boolean rated = Boolean.FALSE;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime completionDate;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updatedAt;

    private String paymentMethod;

    private String paymentDetails;

    @Convert(converter = BookingStatusConverter.class)
    private BookingStatus bookingStatus;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.PERSIST)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumns({
            @JoinColumn(name = "course_id", referencedColumnName = "courseId"),
            @JoinColumn(name = "user_id", referencedColumnName = "userId")
    })
    private CourseRecord courseRecord;

    public ModuleRecord() {
    }

    public ModuleRecord(String moduleId) {
        checkArgument(moduleId != null);
        this.moduleId = moduleId;
    }

    @JsonIgnore
    public void update(ModuleRecord mr) {
        this.setState(mr.getState());
        this.setEventDate(mr.getEventDate());
        this.setEventId(mr.getEventId());
        this.setUpdatedAt(mr.getUpdatedAt());
        this.setCompletionDate(mr.getCompletionDate());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ModuleRecord that = (ModuleRecord) o;

        return new EqualsBuilder()
                .append(moduleId, that.moduleId)
                .append(eventId, that.eventId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(moduleId)
                .append(eventId)
                .toHashCode();
    }

}
