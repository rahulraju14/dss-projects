package com.dss.auditlog.utils;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ExcelRowTracker {

    private int sheetIndex = 0;
    private int rowIndex = 0;

    public void incrementSheetCount() {
        this.sheetIndex++;
    }

    public void incrementRowCount() {
        this.rowIndex++;
    }

    public void decrementSheetCount() {
        this.sheetIndex--;
    }

    public void decrementRowCount() {
        this.rowIndex--;
    }

    public void resetSheetCount() {
        this.sheetIndex = 0;
    }

    public void resetRowCount() {
        this.rowIndex = 0;
    }

    public void updateRowCount(int rowCount) {
        this.rowIndex = rowCount;
    }
}
