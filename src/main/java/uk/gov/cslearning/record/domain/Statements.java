package uk.gov.cslearning.record.domain;

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
