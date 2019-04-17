package uk.gov.cslearning.record.domain.collections;

import lombok.Data;

import java.util.List;

@Data
public class Statements {
    private String id;
    private String personaIdentifier;
    private List<String> activities;

    public Statements() {
    }
}
