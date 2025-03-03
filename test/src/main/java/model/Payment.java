package model;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Payment {

    @JsonProperty("@id")
    String paymentId;

    @JsonProperty("id")
    int id;

    @JsonProperty("labelDE")
    String labelDE;

    @JsonProperty("labelEN")
    String labelEN;

    @JsonProperty("type")
    String type;

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setLabelDE(String labelDE) {
        this.labelDE = labelDE;
    }

    public String getLabelDE() {
        return labelDE;
    }

    public void setLabelEN(String labelEN) {
        this.labelEN = labelEN;
    }

    public String getLabelEN() {
        return labelEN;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}