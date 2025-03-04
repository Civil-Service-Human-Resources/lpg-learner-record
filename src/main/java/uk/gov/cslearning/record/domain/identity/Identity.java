package uk.gov.cslearning.record.domain.identity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Identity {
    private String username;
    private String uid;
    private List<String> Roles;
}
