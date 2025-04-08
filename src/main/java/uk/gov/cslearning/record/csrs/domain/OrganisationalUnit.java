package uk.gov.cslearning.record.csrs.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class OrganisationalUnit {
    private Integer id;
    private Integer parentId;
    private String code;
    private String name;
    private List<String> paymentMethods = new ArrayList<>();
}
