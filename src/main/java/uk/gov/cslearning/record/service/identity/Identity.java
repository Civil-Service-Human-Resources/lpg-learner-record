package uk.gov.cslearning.record.service.identity;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Identity {

    private String uid;

    private String username;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("uid", uid)
                .toString();
    }
}
