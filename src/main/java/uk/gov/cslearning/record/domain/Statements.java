package uk.gov.cslearning.record.domain;

import gov.adlnet.xapi.model.*;
import gov.adlnet.xapi.model.Result;
import lombok.Data;

import java.util.ArrayList;

@Data
public class Statements {
    private String id;
    private String timestamp;
    private String stored;
    private String version;
    private Verb verb;
    private Actor actor;
    private IStatementObject object;
    private Result result;
    private Context context;
    private Actor authority;
    private ArrayList<Attachment> attachments;

    public Statements() {}
}
