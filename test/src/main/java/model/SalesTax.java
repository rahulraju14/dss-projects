package model;

import com.fasterxml.jackson.annotation.JsonProperty;

   
public class SalesTax {

   @JsonProperty("@id")
   String salesTaxId;

   @JsonProperty("id")
   int id;

   @JsonProperty("type")
   String type;

   @JsonProperty("value")
   int value;

    public String getSalesTaxId() {
        return salesTaxId;
    }

    public void setSalesTaxId(String salesTaxId) {
        this.salesTaxId = salesTaxId;
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