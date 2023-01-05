package uk.gov.cslearning.record.csrs.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganisationalUnit {
    private String code;
    private String name;
    private List<String> paymentMethods = new ArrayList<>();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPaymentMethods() {
        return Collections.<String>unmodifiableList(paymentMethods);
    }

    public void setPaymentMethods(List<String> paymentMethods) {
        this.paymentMethods = Collections.<String>unmodifiableList(paymentMethods);
    }
}
