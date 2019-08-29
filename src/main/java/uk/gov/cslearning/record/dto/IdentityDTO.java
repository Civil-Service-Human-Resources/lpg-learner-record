package uk.gov.cslearning.record.dto;

import lombok.Data;

import java.util.List;

@Data
public class IdentityDTO {
    private String username;
    private String uid;
    private List<String> Roles;
}