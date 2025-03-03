package model;

import com.fasterxml.jackson.annotation.JsonProperty;


public class LicenseContract {
    @JsonProperty("@ref")
    private String ref;

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }
}