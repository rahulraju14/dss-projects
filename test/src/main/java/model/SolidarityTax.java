package model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class SolidarityTax {

    @JsonProperty("@id")
    String solidarityTaxId;

    @JsonProperty("id")
    int id;

    @JsonProperty("type")
    String type;

    @JsonProperty("value")
    int value;

    public String getSolidarityTaxId() {
        return solidarityTaxId;
    }

    public void setSolidarityTaxId(String solidarityTaxId) {
        this.solidarityTaxId = solidarityTaxId;
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