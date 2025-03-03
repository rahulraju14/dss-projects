package model;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Maturity {

    @JsonProperty("@ref")
    String maturityId;

    public String getMaturityId() {
        return maturityId;
    }

    public void setMaturityId(String maturityId) {
        this.maturityId = maturityId;
    }
}