package uk.gov.cslearning.record.domain;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class IdentityDTO {
    private String username;
    private String uid;
    private Set<String> roles = new HashSet<>();
}
