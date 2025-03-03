package model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

   
public class LasResponse {

   @JsonProperty("draw")
   private int draw;

   @JsonProperty("recordsTotal")
   private int recordsTotal;

   @JsonProperty("recordsFiltered")
   private int recordsFiltered;

   @JsonProperty("data")
   private List<Data> data;

   @JsonProperty("size")
   private int size;

    public void setDraw(int draw) {
        this.draw = draw;
    }
    public int getDraw() {
        return draw;
    }
    
    public void setRecordsTotal(int recordsTotal) {
        this.recordsTotal = recordsTotal;
    }
    public int getRecordsTotal() {
        return recordsTotal;
    }
    
    public void setRecordsFiltered(int recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }
    public int getRecordsFiltered() {
        return recordsFiltered;
    }
    
    public void setData(List<Data> data) {
        this.data = data;
    }
    public List<Data> getData() {
        return data;
    }
    
    public void setSize(int size) {
        this.size = size;
    }
    public int getSize() {
        return size;
    }
    
}