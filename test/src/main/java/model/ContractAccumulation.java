package model;

import com.fasterxml.jackson.annotation.JsonProperty;

   
public class ContractAccumulation {

   @JsonProperty("@ref")
   private String contractAccId;

    public String getContractAccId() {
        return contractAccId;
    }

    public void setContractAccId(String contractAccId) {
        this.contractAccId = contractAccId;
    }
}