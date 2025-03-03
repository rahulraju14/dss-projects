package model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class WithholdingTax {

    @JsonProperty("@id")
    String holdingTaxId;

    @JsonProperty("id")
    int id;

    @JsonProperty("type")
    String type;

    @JsonProperty("value")
    int value;

    public String getHoldingTaxId() {
        return holdingTaxId;
    }

    public void setHoldingTaxId(String holdingTaxId) {
        this.holdingTaxId = holdingTaxId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}