package uk.gov.cslearning.record.domain;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gov.adlnet.xapi.model.*;
import gov.adlnet.xapi.model.Result;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.UUID;

@Document
public class Statement {
    private String id;
    private String timestamp;
    private String stored;
    private String version;
    private String verb;
    private String actor;
    private String object;
    private String result;
    private String context;
    private String authority;
    private String attachments;

    public Statement() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getStored() {
        return stored;
    }

    public void setStored(String stored) {
        this.stored = stored;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVerb() {
        return verb;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getAttachments() {
        return attachments;
    }

    public void setAttachments(String attachments) {
        this.attachments = attachments;
    }

    @Override
    public String toString() {
        return "Statement{" +
                "id='" + id + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", stored='" + stored + '\'' +
                ", version='" + version + '\'' +
                ", verb='" + verb + '\'' +
                ", actor='" + actor + '\'' +
                ", object='" + object + '\'' +
                ", result='" + result + '\'' +
                ", context='" + context + '\'' +
                ", authority='" + authority + '\'' +
                ", attachments='" + attachments + '\'' +
                '}';
    }
}
