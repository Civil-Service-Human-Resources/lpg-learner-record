package uk.gov.cslearning.record.api.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FailedResource<R> {

    private R resource;
    private String reason;

}
