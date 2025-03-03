package model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Category {

    @JsonProperty("@id")
    private String categoryId;

    @JsonProperty("auditorEmail")
    private String auditorEmail;

    @JsonProperty("auditorEmailMonetaryHigh")
    private String auditorEmailMonetaryHigh;

    @JsonProperty("auditorEmailMonetaryLow")
    private String auditorEmailMonetaryLow;

    @JsonProperty("auditorEmailMonetaryMiddle")
    private String auditorEmailMonetaryMiddle;

    @JsonProperty("id")
    private int id;

    @JsonProperty("label")
    private String label;

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setAuditorEmail(String auditorEmail) {
        this.auditorEmail = auditorEmail;
    }

    public String getAuditorEmail() {
        return auditorEmail;
    }

    public void setAuditorEmailMonetaryHigh(String auditorEmailMonetaryHigh) {
        this.auditorEmailMonetaryHigh = auditorEmailMonetaryHigh;
    }

    public String getAuditorEmailMonetaryHigh() {
        return auditorEmailMonetaryHigh;
    }

    public void setAuditorEmailMonetaryLow(String auditorEmailMonetaryLow) {
        this.auditorEmailMonetaryLow = auditorEmailMonetaryLow;
    }

    public String getAuditorEmailMonetaryLow() {
        return auditorEmailMonetaryLow;
    }

    public void setAuditorEmailMonetaryMiddle(String auditorEmailMonetaryMiddle) {
        this.auditorEmailMonetaryMiddle = auditorEmailMonetaryMiddle;
    }

    public String getAuditorEmailMonetaryMiddle() {
        return auditorEmailMonetaryMiddle;
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

}