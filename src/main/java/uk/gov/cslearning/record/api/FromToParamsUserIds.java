package uk.gov.cslearning.record.api;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FromToParamsUserIds extends FromToParams {

    @Size(min = 1, max = 100)
    private List<String> learnerIds;

}
