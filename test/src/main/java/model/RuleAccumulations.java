package model;

import com.fasterxml.jackson.annotation.JsonProperty;


public class RuleAccumulations {

    @JsonProperty("@id")
    String ruleAccumulationId;

    @JsonProperty("contractAccumulation")
    ContractAccumulation contractAccumulation;

    @JsonProperty("id")
    int id;

    @JsonProperty("label")
    String label;

    @JsonProperty("quantity")
    int quantity;

    @JsonProperty("rate")
    int rate;

    @JsonProperty("royaltyAmountInEuro")
    RoyaltyAmountInEuro royaltyAmountInEuro;

    @JsonProperty("turnoverInEuro")
    TurnoverInEuro turnoverInEuro;

    @JsonProperty("type")
    String type;

    public String getRuleAccumulationId() {
        return ruleAccumulationId;
    }

    public void setRuleAccumulationId(String ruleAccumulationId) {
        this.ruleAccumulationId = ruleAccumulationId;
    }

    public void setContractAccumulation(ContractAccumulation contractAccumulation) {
        this.contractAccumulation = contractAccumulation;
    }

    public ContractAccumulation getContractAccumulation() {
        return contractAccumulation;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getRate() {
        return rate;
    }

    public void setRoyaltyAmountInEuro(RoyaltyAmountInEuro royaltyAmountInEuro) {
        this.royaltyAmountInEuro = royaltyAmountInEuro;
    }

    public RoyaltyAmountInEuro getRoyaltyAmountInEuro() {
        return royaltyAmountInEuro;
    }

    public void setTurnoverInEuro(TurnoverInEuro turnoverInEuro) {
        this.turnoverInEuro = turnoverInEuro;
    }

    public TurnoverInEuro getTurnoverInEuro() {
        return turnoverInEuro;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}