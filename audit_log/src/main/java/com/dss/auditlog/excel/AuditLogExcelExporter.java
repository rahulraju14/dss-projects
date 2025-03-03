package com.dss.auditlog.excel;

import com.dss.auditlog.events.BackgroundTaskEvent;
import com.dss.auditlog.events.ProgressBarEvent;
import com.dss.auditlog.utils.ExcelRowTracker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataProvider;
import io.jmix.core.Entity;
import io.jmix.core.Id;
import io.jmix.core.common.util.Preconditions;
import io.jmix.core.metamodel.datatype.Datatype;
import io.jmix.core.metamodel.model.MetaProperty;
import io.jmix.core.metamodel.model.MetaPropertyPath;
import io.jmix.core.metamodel.model.Range;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.UiEventPublisher;
import io.jmix.flowui.backgroundtask.BackgroundTask;
import io.jmix.flowui.backgroundtask.BackgroundTaskHandler;
import io.jmix.flowui.backgroundtask.BackgroundWorker;
import io.jmix.flowui.backgroundtask.TaskLifeCycle;
import io.jmix.flowui.component.ListDataComponent;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.component.grid.EnhancedDataGrid;
import io.jmix.flowui.component.grid.TreeDataGrid;
import io.jmix.flowui.data.grid.ContainerDataGridItems;
import io.jmix.flowui.data.grid.ContainerTreeDataGridItems;
import io.jmix.flowui.download.DownloadFormat;
import io.jmix.flowui.download.Downloader;
import io.jmix.gridexportflowui.GridExportProperties;
import io.jmix.gridexportflowui.exporter.ExportMode;
import io.jmix.gridexportflowui.exporter.excel.ExcelAutoColumnSizer;
import io.jmix.gridexportflowui.exporter.excel.ExcelExporter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Time;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class AuditLogExcelExporter extends ExcelExporter {

    @Autowired
    private BackgroundWorker backgroundWorker;

    @Autowired
    private UiEventPublisher uiEventPublisher;

    public AuditLogExcelExporter(GridExportProperties gridExportProperties, Notifications notifications, CustomEntitiesLoaderFactory allEntitiesLoaderFactory) {
        super(gridExportProperties, notifications, allEntitiesLoaderFactory);
    }

    public void customExportDataGrid(Downloader downloader, Grid<Object> dataGrid, ExportMode exportMode, LocalDate from, LocalDate to, String docName) {
        log.info("-- Invoked export data grid --");
        String progressId = UUID.randomUUID().toString().split("-")[0];
        BackgroundTaskHandler backgroundTaskHandler = taskHandler(progressId, downloader, dataGrid, exportMode, from, to, docName);
        uiEventPublisher.publishEvent(new BackgroundTaskEvent(this, progressId, backgroundTaskHandler));
        backgroundTaskHandler.execute();
    }

    private BackgroundTaskHandler taskHandler(String progressId, Downloader downloader, Grid<Object> dataGrid, ExportMode exportMode, LocalDate from, LocalDate to, String docName) {
        log.info("--- Executing taskHandler by thread: {}", Thread.currentThread().getName());
        return backgroundWorker.handle(new BackgroundTask<Double, Workbook>(5, TimeUnit.DAYS) {
            @Override
            public boolean handleTimeoutException() {
                log.error("Failed to process task due to timeout");
                return super.handleTimeoutException();
            }

            @Override
            public Workbook run(TaskLifeCycle<Double> taskLifeCycle) {
                try {
                    return getByteArrayDownloadedDataProvider(downloader, dataGrid, exportMode, taskLifeCycle, from, to);
                } catch (Exception ex) {
                    uiEventPublisher.publishEvent(new ProgressBarEvent(this, progressId, null, FALSE, null, fileName, null, TRUE));
                    log.error("Error exporting audit log records: ", ex);
                }
                return null;
            }

            @Override
            public void done(Workbook result) {
                log.info("Export auditLog report completed...");
                String fileName = docName + "." + DownloadFormat.XLSX.getFileExt();
                uiEventPublisher.publishEvent(new ProgressBarEvent(this, progressId, null, TRUE, result, fileName, DownloadFormat.XLSX.getContentType(), FALSE));
            }

            @Override
            public void progress(List<Double> changes) {
                Double progressValue = changes.get(changes.size() - 1);
                String fileName = docName + "." + DownloadFormat.XLSX.getFileExt();
                uiEventPublisher.publishEvent(new ProgressBarEvent(this, progressId, progressValue, FALSE, null, fileName,null, FALSE));
            }
        });
    }

    private RichTextString createStringCellValue(String str) {
        return new XSSFRichTextString(str);
    }

    private void createFont(Workbook workbook){
        workbook.createFont(); // creating standard font at index 0
        workbook.createFont().setBold(TRUE); // creating bold font at index 1
    }

    private Workbook getByteArrayDownloadedDataProvider(Downloader downloader, Grid<Object> dataGrid, ExportMode exportMode, TaskLifeCycle<Double> taskLifeCycle, LocalDate from, LocalDate to) {
        log.info("-- Background task execution started... Thread: {}....", Thread.currentThread().getName());
        Preconditions.checkNotNullArgument(downloader, "Downloader is null");

        ExcelRowTracker counter = new ExcelRowTracker();
        Workbook wb = createWorkbook();
        wb.createSheet(getSheetName(counter.getSheetIndex()));
        createFont(wb);

        List<Grid.Column<Object>> columns = dataGrid.getColumns();
        int r = 0;

        ExcelAutoColumnSizer[] sizers = createColumnSizers(columns.size());
        createColumnHeaders(wb, sizers, columns, counter.getSheetIndex());

        CellStyle timeFormatCellStyle = getCellStyle(CellStyleAttributes.TIME_FORMAT, wb);
        CellStyle dateFormatCellStyle = getCellStyle(CellStyleAttributes.DATE_FORMAT, wb);
        CellStyle dateTimeFormatCellStyle = getCellStyle(CellStyleAttributes.DATE_TIME_FORMAT, wb);
        CellStyle integerFormatCellStyle = getCellStyle(CellStyleAttributes.INTEGER_FORMAT, wb);
        CellStyle doubleFormatCellStyle = getCellStyle(CellStyleAttributes.DOUBLE_FORMAT, wb);
        CellStyle decimalFormatCellStyle = getCellStyle(CellStyleAttributes.DECIMAL_FORMAT, wb);

        ContainerDataGridItems<Object> dataGridSource = (ContainerDataGridItems) ((ListDataComponent<Object>) dataGrid).getItems();
        if (dataGridSource == null) {
            throw new IllegalStateException("DataGrid is not bound to data");
        }

        if (exportMode == ExportMode.SELECTED_ROWS && dataGrid.getSelectedItems().size() > 0) {
            Set<Object> selected = dataGrid.getSelectedItems();
            List<Object> ordered = dataGridSource.getContainer().getItems().stream()
                    .filter(selected::contains)
                    .collect(Collectors.toList());

            for (Object item : ordered) {
                if (checkIsRowNumberExceed(r)) {
                    break;
                }

                createDataGridRow(dataGrid, columns, 0, ++r, Id.of(item).getValue());
            }

        } else if (exportMode == ExportMode.CURRENT_PAGE) {
            if (dataGrid instanceof TreeDataGrid) {
                TreeDataGrid treeDataGrid = (TreeDataGrid) dataGrid;
                List<Object> items = dataGridSource.getContainer().getItems();

                for (Object item : items) {
                    if (checkIsRowNumberExceed(r)) {
                        break;
                    }

                    r = createDataGridHierarchicalRow(treeDataGrid, ((ContainerTreeDataGridItems) dataGridSource),
                            columns, 0, r, item);
                }
            } else {
                int count = 0;
                int totalRecords = dataGridSource.getContainer().getItems().size();
                for (Object itemId : dataGridSource.getContainer().getItems().stream()
                        .map(entity -> Id.of(entity).getValue())
                        .collect(Collectors.toList())
                ) {
                    if (checkIsRowNumberExceed(r)) {
                        break;
                    }
                    counter.incrementRowCount();
                    double progress = ((double) (count + 1) / totalRecords);

                    if ((count + 1) % 10 == 0) {
                        try {
                            taskLifeCycle.publish(progress);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    Object entityInstance = ((ContainerDataGridItems) ((ListDataComponent) dataGrid).getItems()).getItem(itemId);
                    createDataGridRowForEntityInstance(dataGrid, columns, 0, entityInstance, TRUE, wb, sizers, counter, timeFormatCellStyle, dateFormatCellStyle,
                            dateTimeFormatCellStyle, integerFormatCellStyle, doubleFormatCellStyle, decimalFormatCellStyle);
                    count++;
                }
            }

        } else if (exportMode == ExportMode.ALL_ROWS) {
            boolean addLevelPadding = !(dataGrid instanceof TreeDataGrid);

            CustomKeySetEntityLoader customKeySetEntityLoader = (CustomKeySetEntityLoader) allEntitiesLoaderFactory.getEntitiesLoader();

            customKeySetEntityLoader.customLoadAll(
                    ((ListDataComponent<?>) dataGrid).getItems(),
                    context -> {
                        createDataGridRowForEntityInstance(
                                dataGrid,
                                columns,
                                0,
                                context.getAuditLogEntity(),
                                addLevelPadding, wb, sizers, counter, timeFormatCellStyle, dateFormatCellStyle,
                                dateTimeFormatCellStyle, integerFormatCellStyle, doubleFormatCellStyle, decimalFormatCellStyle);
                        return true;
                    }, taskLifeCycle, from, to, counter);
        }

        IntStream.range(0, wb.getActiveSheetIndex()).forEach(si -> {
            Sheet wbSheet = wb.getSheetAt(si);
            IntStream.range(0, columns.size()).forEach(ci -> wbSheet.setColumnWidth(ci, sizers[ci].getWidth() * COL_WIDTH_MAGIC));
        });

        return wb;
    }

    enum CellStyleAttributes {
        TIME_FORMAT,
        DATE_FORMAT,
        DATE_TIME_FORMAT,
        INTEGER_FORMAT,
        DOUBLE_FORMAT,
        DECIMAL_FORMAT
    }

    private Workbook createWorkbook() {
        return this.gridExportProperties.getExcel().isUseSxssf() ? new SXSSFWorkbook() : new XSSFWorkbook();
    }

    private String getSheetName(int sheetIndex){
        return "Export" + "_" + sheetIndex;
    }

    private ExcelAutoColumnSizer[] createColumnSizers(int count) {
        return new ExcelAutoColumnSizer[count];
    }

    private void createDataGridRowForEntityInstance(Grid<?> dataGrid, List<Grid.Column<Object>> columns, int startColumn, Object entityInstance, boolean addLevelPadding, Workbook wb, ExcelAutoColumnSizer[] sizers,
                                                    ExcelRowTracker counter, CellStyle timeFormatCellStyle, CellStyle dateFormatCellStyle,
                                                    CellStyle dateTimeFormatCellStyle, CellStyle integerFormatCellStyle, CellStyle doubleFormatCellStyle, CellStyle decimalFormatCellStyle) {
        if (counter.getRowIndex() >= MAX_ROW_COUNT) {
            log.info("--- max row exceeded | row count: {}---", counter.getRowIndex());
            counter.incrementSheetCount();
            counter.resetRowCount();
            createSheetForWorkbook(wb, sizers, columns, counter.getSheetIndex());
            counter.updateRowCount(1);
        }

        if (startColumn < columns.size()) {
            Sheet sheet = wb.getSheetAt(counter.getSheetIndex());
            Row row = sheet.createRow(counter.getRowIndex());
            int level = 0;
            if (addLevelPadding && dataGrid instanceof TreeDataGrid) {
                HierarchicalDataProvider dataProvider = ((TreeDataGrid) dataGrid).getDataProvider();
                level = ((ContainerTreeDataGridItems) dataProvider).getLevel(entityInstance);
            }

            for (int c = startColumn; c < columns.size(); ++c) {
                Cell cell = row.createCell(c);
                Grid.Column<?> column = columns.get(c);
                MetaPropertyPath propertyPath = ((EnhancedDataGrid) dataGrid).getColumnMetaPropertyPath(column);
                Object cellValue = this.getColumnValue(dataGrid, columns.get(c), entityInstance);
                formatValueCell(cell, cellValue, propertyPath, c, counter.getRowIndex(), level, null, wb, sizers, timeFormatCellStyle, dateFormatCellStyle,
                        dateTimeFormatCellStyle, integerFormatCellStyle, doubleFormatCellStyle, decimalFormatCellStyle);
            }

        }
    }

    private void createSheetForWorkbook(Workbook wb, ExcelAutoColumnSizer[] sizers, List<Grid.Column<Object>> columns, int sheetIndex){
        wb.createSheet(getSheetName(sheetIndex));
        createColumnHeaders(wb, sizers, columns, sheetIndex);
    }

    private void createColumnHeaders(Workbook wb, ExcelAutoColumnSizer[] sizers, List<Grid.Column<Object>> columns, int sheetIndex){
        log.info("-- creating column header for sheet at index: {}", sheetIndex);
        Sheet sheet = wb.getSheetAt(sheetIndex);
        Row row = sheet.createRow(0);
        Font boldFont = wb.getFontAt(1);
        float maxHeight = sheet.getDefaultRowHeightInPoints();

        CellStyle headerCellStyle = wb.createCellStyle();
        headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        for (DataGrid.Column<?> column : columns) {
            String columnHeaderText = getColumnHeaderText(column);

            int countOfReturnSymbols = StringUtils.countMatches(columnHeaderText, "\n");
            if (countOfReturnSymbols > 0) {
                maxHeight = Math.max(maxHeight, (countOfReturnSymbols + 1) * sheet.getDefaultRowHeightInPoints());
                headerCellStyle.setWrapText(true);
            }
        }
        row.setHeightInPoints(maxHeight);

        for (int c = 0; c < columns.size(); c++) {
            DataGrid.Column<?> column = columns.get(c);
            String columnHeaderText = getColumnHeaderText(column);

            Cell cell = row.createCell(c);
            RichTextString richTextString = createStringCellValue(columnHeaderText);
            richTextString.applyFont(boldFont);
            cell.setCellValue(richTextString);

            ExcelAutoColumnSizer sizer = new ExcelAutoColumnSizer();
            sizer.notifyCellValue(columnHeaderText, boldFont);
            sizers[c] = sizer;

            cell.setCellStyle(headerCellStyle);
        }
    }
    private CellStyle getCellStyle(CellStyleAttributes styleAttributes, Workbook workbook) {
        return switch (styleAttributes) {
            case TIME_FORMAT -> {
                CellStyle timeFormatCellStyle = workbook.createCellStyle();
                DataFormat dataFormat = workbook.getCreationHelper().createDataFormat();
                String timeFormat = this.getMessage("excelExporter.timeFormat");
                short timeDataFormat = this.getBuiltinFormat(timeFormat) == -1 ? dataFormat.getFormat(timeFormat) : this.getBuiltinFormat(timeFormat);
                timeFormatCellStyle.setDataFormat(timeDataFormat);
                yield timeFormatCellStyle;
            }
            case DATE_FORMAT -> {
                CellStyle dateFormatCellStyle = workbook.createCellStyle();
                DataFormat dataFormat = workbook.getCreationHelper().createDataFormat();
                String dateFormat = this.getMessage("excelExporter.dateFormat");
                short dateDataFormat = this.getBuiltinFormat(dateFormat) == -1 ? dataFormat.getFormat(dateFormat) : this.getBuiltinFormat(dateFormat);
                dateFormatCellStyle.setDataFormat(dateDataFormat);
                yield dateFormatCellStyle;
            }
            case DATE_TIME_FORMAT -> {
                CellStyle dateTimeFormatCellStyle = workbook.createCellStyle();
                DataFormat dataFormat = workbook.getCreationHelper().createDataFormat();
                String dateTimeFormat = this.getMessage("excelExporter.dateTimeFormat");
                short dateTimeDataFormat = this.getBuiltinFormat(dateTimeFormat) == -1 ? dataFormat.getFormat(dateTimeFormat) : this.getBuiltinFormat(dateTimeFormat);
                dateTimeFormatCellStyle.setDataFormat(dateTimeDataFormat);
                yield dateTimeFormatCellStyle;
            }
            case INTEGER_FORMAT -> {
                CellStyle integerFormatCellStyle = workbook.createCellStyle();
                String integerFormat = this.getMessage("excelExporter.integerFormat");
                integerFormatCellStyle.setDataFormat(this.getBuiltinFormat(integerFormat));
                yield integerFormatCellStyle;
            }
            case DOUBLE_FORMAT -> {
                DataFormat doubleDataFormat = workbook.createDataFormat();
                CellStyle doubleFormatCellStyle = workbook.createCellStyle();
                String doubleFormat = this.getMessage("excelExporter.doubleFormat");
                doubleFormatCellStyle.setDataFormat(doubleDataFormat.getFormat(doubleFormat));
                yield doubleFormatCellStyle;
            }
            case DECIMAL_FORMAT -> {
                DataFormat decimalDataFormat = workbook.createDataFormat();
                CellStyle decimalFormatCellStyle = workbook.createCellStyle();
                String decimalFormat = this.getMessage("excelExporter.decimalFormat");
                decimalFormatCellStyle.setDataFormat(decimalDataFormat.getFormat(decimalFormat));
                yield decimalFormatCellStyle;
            }
            default -> {
                throw new IllegalArgumentException("Unexpected value: " + styleAttributes);
            }
        };
    }
    private void formatValueCell(Cell cell, Object cellValue, MetaPropertyPath metaPropertyPath, int sizersIndex, int notificationRequired, int level,
                                 Integer groupChildCount, Workbook wb, ExcelAutoColumnSizer[] sizers, CellStyle timeFormatCellStyle, CellStyle dateFormatCellStyle,
                                 CellStyle dateTimeFormatCellStyle, CellStyle integerFormatCellStyle, CellStyle doubleFormatCellStyle, CellStyle decimalFormatCellStyle) {
        Font stdFont = wb.getFontAt(0);
        if (cellValue == null) {
            if (metaPropertyPath != null
                    && metaPropertyPath.getRange().isDatatype()) {
                Class<?> javaClass = metaPropertyPath.getRange().asDatatype().getJavaClass();
                if (Boolean.class.equals(javaClass)) {
                    cellValue = false;
                }
            } else {
                return;
            }
        }

        String childCountValue = "";
        if (groupChildCount != null) {
            childCountValue = " (" + groupChildCount + ")";
        }

        if (cellValue instanceof Number) {
            Number n = (Number) cellValue;
            Datatype<?> datatype = null;
            if (metaPropertyPath != null) {
                Range range = metaPropertyPath.getMetaProperty().getRange();
                if (range.isDatatype()) {
                    datatype = range.asDatatype();
                }
            }

            datatype = datatype == null ? datatypeRegistry.get(n.getClass()) : datatype;
            String str;
            // level is used for TreeTable, so level with 0 doesn't create spacing
            // and we should skip it
            if (sizersIndex == 0 && level > 0) {
                str = createSpaceString(level) + datatype.format(n);
                cell.setCellValue(str);
            } else {
                try {
                    str = datatype.format(n);
                    Number result = (Number) datatype.parse(str);
                    if (result != null) {
                        if (n instanceof Integer || n instanceof Long || n instanceof Byte || n instanceof Short) {
                            cell.setCellValue(result.longValue());
                            cell.setCellStyle(integerFormatCellStyle);
                        } else {
                            cell.setCellValue(n.doubleValue());
                            cell.setCellStyle(n instanceof BigDecimal
                                    ? decimalFormatCellStyle
                                    : doubleFormatCellStyle);
                        }
                    }
                } catch (ParseException e) {
                    throw new RuntimeException("Unable to parse numeric value", e);
                }
                cell.setCellType(CellType.NUMERIC);
            }
            if (sizers[sizersIndex].isNotificationRequired(notificationRequired)) {
                sizers[sizersIndex].notifyCellValue(str, stdFont);
            }
        } else if (cellValue instanceof Date) {
            Class<?> javaClass = null;
            if (metaPropertyPath != null) {
                MetaProperty metaProperty = metaPropertyPath.getMetaProperty();
                if (metaProperty.getRange().isDatatype()) {
                    javaClass = metaProperty.getRange().asDatatype().getJavaClass();
                }
            }
            Date date = (Date) cellValue;

            cell.setCellValue(date);

            if (Objects.equals(Time.class, javaClass)) {
                cell.setCellStyle(timeFormatCellStyle);
            } else if (Objects.equals(java.sql.Date.class, javaClass)) {
                cell.setCellStyle(dateFormatCellStyle);
            } else {
                cell.setCellStyle(dateTimeFormatCellStyle);
            }
            if (sizers[sizersIndex].isNotificationRequired(notificationRequired)) {
                String str = datatypeRegistry.get(Date.class).format(date);
                sizers[sizersIndex].notifyCellValue(str, stdFont);
            }
        } else if (cellValue instanceof LocalTime) {
            LocalTime time = (LocalTime) cellValue;

            cell.setCellValue(Time.valueOf(time));
            cell.setCellStyle(timeFormatCellStyle);

            if (sizers[sizersIndex].isNotificationRequired(notificationRequired)) {
                String str = datatypeRegistry.get(LocalTime.class).format(time);
                sizers[sizersIndex].notifyCellValue(str, stdFont);
            }
        } else if (cellValue instanceof LocalDate) {
            LocalDate date = (LocalDate) cellValue;

            cell.setCellValue(date);
            cell.setCellStyle(dateFormatCellStyle);

            if (sizers[sizersIndex].isNotificationRequired(notificationRequired)) {
                String str = datatypeRegistry.get(LocalDate.class).format(date);
                sizers[sizersIndex].notifyCellValue(str, stdFont);
            }
        } else if (cellValue instanceof LocalDateTime) {
            LocalDateTime dateTime = (LocalDateTime) cellValue;

            cell.setCellValue(dateTime);
            cell.setCellStyle(dateTimeFormatCellStyle);

            if (sizers[sizersIndex].isNotificationRequired(notificationRequired)) {
                String str = datatypeRegistry.get(LocalDateTime.class).format(dateTime);
                sizers[sizersIndex].notifyCellValue(str, stdFont);
            }
        } else if (cellValue instanceof Boolean) {
            String str = "";
            if (sizersIndex == 0) {
                str += createSpaceString(level);
            }
            str += ((Boolean) cellValue) ? getMessage("excelExporter.true") : getMessage("excelExporter.false");
            cell.setCellValue(createStringCellValue(str));
            if (sizers[sizersIndex].isNotificationRequired(notificationRequired)) {
                sizers[sizersIndex].notifyCellValue(str, stdFont);
            }
        } else if (cellValue instanceof Enum) {
            String message = (sizersIndex == 0 ? createSpaceString(level) : "") +
                    messages.getMessage((Enum) cellValue);

            cell.setCellValue(message + childCountValue);
            if (sizers[sizersIndex].isNotificationRequired(notificationRequired)) {
                sizers[sizersIndex].notifyCellValue(message, stdFont);
            }
        } else if (cellValue instanceof Entity) {
            Object entityVal = cellValue;
            String instanceName = metadataTools.getInstanceName(entityVal);
            String str = sizersIndex == 0 ? createSpaceString(level) + instanceName : instanceName;
            str = str + childCountValue;
            cell.setCellValue(createStringCellValue(str));
            if (sizers[sizersIndex].isNotificationRequired(notificationRequired)) {
                sizers[sizersIndex].notifyCellValue(str, stdFont);
            }
        } else if (cellValue instanceof Collection) {
            String str = "";
            cell.setCellValue(createStringCellValue(str));
            if (sizers[sizersIndex].isNotificationRequired(notificationRequired)) {
                sizers[sizersIndex].notifyCellValue(str, stdFont);
            }
        } else if (cellValue instanceof byte[]) {
            String str = messages.getMessage("excelExporter.bytes");
            cell.setCellValue(createStringCellValue(str));
            if (sizers[sizersIndex].isNotificationRequired(notificationRequired)) {
                sizers[sizersIndex].notifyCellValue(str, stdFont);
            }
        } else {
            String strValue = cellValue == null ? "" : cellValue.toString();
            String str = sizersIndex == 0 ? createSpaceString(level) + strValue : strValue;
            str = str + childCountValue;
            cell.setCellValue(createStringCellValue(str));
            if (sizers[sizersIndex].isNotificationRequired(notificationRequired)) {
                sizers[sizersIndex].notifyCellValue(str, stdFont);
            }
        }
    }
}
