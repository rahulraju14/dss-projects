package model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class Country {

    @JsonProperty("@id")
    private String countryId;

    @JsonProperty("id")
    private int id;

    @JsonProperty("labelDE")
    private String labelDE;

    @JsonProperty("labelEN")
    private String labelEN;

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
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

}