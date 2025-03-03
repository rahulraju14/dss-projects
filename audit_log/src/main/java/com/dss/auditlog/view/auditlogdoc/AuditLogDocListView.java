package com.dss.auditlog.view.auditlogdoc;

import com.dss.auditlog.elasticsearch.documents.AuditLogDoc;
import com.dss.auditlog.elasticsearch.repository.AuditLogSearchRepository;
import com.dss.auditlog.entity.DashBoardDataAttributes;
import com.dss.auditlog.entity.DateRange;
import com.dss.auditlog.excel.CustomExcelExportAction;
import com.dss.auditlog.view.mainviewtopmenu.MainViewTopMenu;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.Route;
import io.jmix.core.LoadContext;
import io.jmix.core.querycondition.PropertyCondition;
import io.jmix.flowui.Dialogs;
import io.jmix.flowui.UiComponents;
import io.jmix.flowui.action.inputdialog.InputDialogAction;
import io.jmix.flowui.app.inputdialog.InputDialog;
import io.jmix.flowui.app.inputdialog.InputParameter;
import io.jmix.flowui.component.combobox.JmixComboBox;
import io.jmix.flowui.component.datepicker.TypedDatePicker;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.component.grid.DataGridColumn;
import io.jmix.flowui.component.grid.headerfilter.DataGridHeaderFilter;
import io.jmix.flowui.component.multiselectcombobox.JmixMultiSelectComboBox;
import io.jmix.flowui.component.pagination.SimplePagination;
import io.jmix.flowui.component.propertyfilter.PropertyFilter;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.component.validation.ValidationErrors;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.kit.action.ActionVariant;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.kit.component.dropdownbutton.DropdownButton;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Route(value = "auditLogDocs", layout = MainViewTopMenu.class)
@ViewController("AL_AuditLogDoc.list")
@ViewDescriptor("audit-log-doc-list-view.xml")
@LookupComponent("auditLogDocsDataGrid")
@DialogMode(width = "50em")
public class AuditLogDocListView extends StandardListView<AuditLogDoc> {
    private static final DateTimeFormatter CREATED_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss:SSS");

    private static final String ICON_ATTRIBUTE_NAME = "icon";
    private static final int MAX_COUNT = 10_000;

    private static final int AUDIT_RECORDS_MAX_YEAR = 8;
    @Autowired
    private AuditLogSearchRepository searchRepository;
    @ViewComponent
    private DataGrid<AuditLogDoc> auditLogDocsDataGrid;
    @Autowired
    private UiComponents uiComponents;
    @ViewComponent
    private CollectionLoader<AuditLogDoc> auditLogDocsDl;
    @ViewComponent
    private TypedDatePicker<LocalDate> dateFrom;
    @ViewComponent
    private TypedDatePicker<LocalDate> dateTo;
    @ViewComponent
    private SimplePagination pagination;
//    @ViewComponent
//    private JmixComboBox<Integer> yearFilter;
    @ViewComponent("auditLogDocsDataGrid.excelExport")
    private CustomExcelExportAction auditLogDocsDataGridExcelExport;
    @Autowired
    private Dialogs dialogs;
    @Autowired
    private ObjectMapper objectMapper;
    private boolean queryParamsPresent = false;

    @Subscribe
    public void onAttachEvent(final AttachEvent event) {
        if (! queryParamsPresent) {
            initHeaderFilters();
            calculateFinancialYear();
        }
//        initFinancialYearField();
        disablePagination();
    }

    @Subscribe
    public void onQueryParametersChange(final QueryParametersChangeEvent event) {
        queryParamsPresent = false;
        if (event.getQueryParameters() != null && !event.getQueryParameters().getParameters().isEmpty() &&
                event.getQueryParameters().getParameters().containsKey("rangeFilter")) {
            Optional<String> dashBoardAttributes = event.getQueryParameters().getParameters().get("rangeFilter").stream().findFirst();
            if (dashBoardAttributes.isPresent()) {
                queryParamsPresent = true;
                try {
                    DashBoardDataAttributes dashBoardDataAttributes = objectMapper.readValue(dashBoardAttributes.get(), DashBoardDataAttributes.class);
                    initHeaderFilters();
                    invokePropertyFilter(dashBoardDataAttributes);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Install(to = "auditLogDocsDl", target = Target.DATA_LOADER)
    public List<AuditLogDoc> auditLogDocsDlLoadDelegate(LoadContext<AuditLogDoc> loadContext) {
        return searchRepository.searchAuditLogDocs(loadContext, dateFrom.getValue(), dateTo.getValue());
    }

    @Install(to = "pagination", subject = "totalCountDelegate")
    private Integer paginationTotalCountDelegate(final LoadContext<AuditLogDoc> loadContext) {
        return searchRepository.getCount(loadContext, dateFrom.getValue(), dateTo.getValue()).intValue();
    }

    @Subscribe("dateFilter")
    public void onDateFilterComponentValueChange(final AbstractField.ComponentValueChangeEvent<JmixComboBox<DateRange>, DateRange> event) {
        if (event.isFromClient()) {
            applyDateFilter(event.getValue());
        }
    }

    private void applyDateFilter(DateRange dateRange) {
        if (dateRange == null) {
            updateFromAndToDate(null, null, true, true);
            return;
        }
        final LocalDate today = LocalDate.now();
        boolean editable = false;
        boolean autoApply = true;
        switch (dateRange) {
            case TODAY -> updateFromAndToDate(today, null, editable, autoApply);
            case CURRENT_WEEK ->
                    updateFromAndToDate(today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)), null, editable, autoApply);
            case LAST_WEEK -> {
                LocalDate lastWeek = today.minusDays(7);
                updateFromAndToDate(lastWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)), lastWeek.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)), editable, autoApply);
            }
            case CURRENT_MONTH ->
                    updateFromAndToDate(today.with(TemporalAdjusters.firstDayOfMonth()), null, editable, autoApply);
            case LAST_MONTH -> {
                LocalDate lastMonth = today.minusMonths(1);
                updateFromAndToDate(lastMonth.with(TemporalAdjusters.firstDayOfMonth()), lastMonth.with(TemporalAdjusters.lastDayOfMonth()), editable, autoApply);
            }
            case CURRENT_FY_YEAR -> {
                int year = today.getMonthValue() > 3 ? today.getYear() : today.getYear() - 1;
                updateFromAndToDate(LocalDate.of(year, 4, 1), null, editable, autoApply);
            }
            default -> updateFromAndToDate(null, null, true, true);
        }
    }

    private void updateFromAndToDate(LocalDate from, LocalDate to, boolean editable, boolean autoApply) {
        dateFrom.setValue(from);
        dateTo.setValue(to);
        dateFrom.setReadOnly(!editable);
        dateTo.setReadOnly(!editable);
        if (autoApply) {
            auditLogDocsDl.load();
        }
    }

    @Supply(to = "auditLogDocsDataGrid.createdDate", subject = "renderer")
    private Renderer<AuditLogDoc> auditLogDocsDataGridCreatedDateRenderer() {
        return new TextRenderer<>(item -> item.getCreatedDate() != null ? CREATED_DATE_FORMATTER.format(item.getCreatedDate()) : "");
    }

    @Subscribe(id = "btApplyDateFilter", subject = "clickListener")
    public void onBtApplyDateFilterClick(final ClickEvent<JmixButton> event) {
        if(Objects.isNull(dateFrom.getValue()) && Objects.isNull(dateTo.getValue())) {
            calculateFinancialYear();
        } else {
            auditLogDocsDl.load();
        }
    }

    @Subscribe("dateFrom")
    public void onDateFromComponentValueChange(final AbstractField.ComponentValueChangeEvent<TypedDatePicker<Comparable>, Comparable<?>> event) {
        dateTo.setMin(dateFrom.getValue());
        if (dateTo.getValue() != null && dateFrom.getValue() != null) {
            if (dateTo.getValue().isBefore(dateFrom.getValue())) {
                dateTo.setValue(null);
            }
        }
    }

    private void initHeaderFilters() {
        List<String> fields = Arrays.asList("typeOfChange", "documentType", "tableCaption", "tableNo", "fieldCaption");
        Map<String, List<String>> refValuesMap = searchRepository.fetchUniqueValues(fields);
        fields.forEach(field -> {
            DataGridColumn<AuditLogDoc> column = auditLogDocsDataGrid.getColumnByKey(field);
            DataGridHeaderFilter headerFilter = (DataGridHeaderFilter) Objects.requireNonNull(column).getHeaderComponent();
            PropertyFilter<String> propertyFilter = (PropertyFilter<String>) headerFilter.getPropertyFilter();
            propertyFilter.setOperationEditable(false);
            if(StringUtils.equalsIgnoreCase(field, "documentType")) {
                propertyFilter.setOperation(PropertyFilter.Operation.IN_LIST);
                JmixMultiSelectComboBox<String> multiSelectComboBox = uiComponents.create(JmixMultiSelectComboBox.class);
                multiSelectComboBox.setItems(refValuesMap.get(field));
                multiSelectComboBox.setItemLabelGenerator(r -> StringUtils.isBlank(r) ? "BLANK" : r);
                propertyFilter.setValueComponent((HasValueAndElement) multiSelectComboBox);
            } else {
                propertyFilter.setOperation(PropertyFilter.Operation.EQUAL);
                propertyFilter.setOperationEditable(false);
                ComboBox<String> comboBox = uiComponents.create(ComboBox.class);
                comboBox.setItems(refValuesMap.get(field));
                propertyFilter.setValueComponent(comboBox);
            }
        });

        auditLogDocsDataGrid.getColumns().forEach(column -> {
            Optional<DropdownButton> dropdownButton = Optional.ofNullable(column.getHeaderComponent())
                    .filter(DataGridHeaderFilter.class::isInstance)
                    .map(DataGridHeaderFilter.class::cast)
                    .map(DataGridHeaderFilter::getPropertyFilter).flatMap(c -> c.getChildren().findFirst())
                    .map(Component::getChildren).flatMap(children -> children
                            .filter(DropdownButton.class::isInstance)
                            .map(DropdownButton.class::cast)
                            .findFirst());
            dropdownButton.ifPresent(dropdown -> {
                dropdown.remove(PropertyFilter.Operation.IS_SET.getId());
                dropdown.remove(PropertyFilter.Operation.IN_LIST.getId());
                dropdown.remove(PropertyFilter.Operation.NOT_IN_LIST.getId());
            });
        });
    }

    public void invokePropertyFilter(DashBoardDataAttributes dashBoardDataAttributes) {
        LocalDate parsedFromDate = parseDate(dashBoardDataAttributes.getFromDate());
        LocalDate parsedToDate = parseDate(dashBoardDataAttributes.getToDate());

        boolean isFromDateValid = parsedFromDate != null;
        boolean isToDateValid = parsedToDate != null;

        if (isFromDateValid && isToDateValid) {
            updateFromAndToDate(parsedFromDate, parsedToDate, true, false);
        } else if (isFromDateValid) {
            updateFromAndToDate(parsedFromDate, null, true, false);
        } else if (isToDateValid) {
            updateFromAndToDate(null, parsedToDate, true, false);
        }

        if(! dashBoardDataAttributes.getPropertyFilterMap().isEmpty()) {
            AtomicReference<DataGridHeaderFilter> headerFilter = new AtomicReference<>();
            dashBoardDataAttributes.getPropertyFilterMap().forEach((colKey, filterValue) -> {
                DataGridColumn<AuditLogDoc> column = auditLogDocsDataGrid.getColumnByKey(colKey);
                headerFilter.set((DataGridHeaderFilter) Objects.requireNonNull(column).getHeaderComponent());
                PropertyFilter<String> propertyFilter = (PropertyFilter<String>) headerFilter.get().getPropertyFilter();
                switch (dashBoardDataAttributes.getComponentType()) {
                    case PIE_CHART, BAR_CHART -> {
                        log.info("=== Entered component case colKey: {} | FilterValue: {}", colKey, filterValue);
                        if (propertyFilter.getValueComponent() instanceof ComboBox) {
                            setComboBoxValue((ComboBox) propertyFilter.getValueComponent(), filterValue);
                        } else if (propertyFilter.getValueComponent() instanceof JmixMultiSelectComboBox) {
                            setMultiSelectComboBoxValue((JmixMultiSelectComboBox) propertyFilter.getValueComponent(),
                                    StringUtils.equalsIgnoreCase(filterValue, "BLANK") ? "" : filterValue);
                        }
                    }
                    case USER_TABLE -> {
                        log.info("=== Entered UserTable component case with colKey: {} | FilterValue: {}", colKey, filterValue);
                        if(StringUtils.equalsIgnoreCase(colKey, "createdBy")) {
                            propertyFilter.setOperation(PropertyFilter.Operation.EQUAL);
                            if (propertyFilter.getValueComponent() instanceof TypedTextField<?>) {
                                setTextBoxValue((TypedTextField) propertyFilter.getValueComponent(), filterValue);
                            }
                        } else {
                            if (propertyFilter.getValueComponent() instanceof ComboBox) {
                                setComboBoxValue((ComboBox) propertyFilter.getValueComponent(), filterValue);
                            }
                        }
                    }
                    case TABLE_CAPTION -> {
                        log.info("=== Entered TableCaption component case with colKey: {} | FilterValue: {}", colKey, filterValue);
                        if (propertyFilter.getValueComponent() instanceof ComboBox) {
                            setComboBoxValue((ComboBox) propertyFilter.getValueComponent(), filterValue);
                        }
                    }
                    default -> throw new IllegalArgumentException("Unknown component type");
                };
                updateFilterSelectionHandler(headerFilter.get(), Boolean.TRUE);
            });
            headerFilter.get().apply();
        }
    }

    private void initFinancialYearField(){
        int currentYear = Year.now().getValue();
        List<Integer> financialYearList = IntStream.rangeClosed(currentYear - AUDIT_RECORDS_MAX_YEAR, currentYear)
                .map(index -> currentYear - (index - (currentYear - AUDIT_RECORDS_MAX_YEAR)))
                .boxed()
                .collect(Collectors.toList());

//        yearFilter.setItems(DataProvider.ofCollection(financialYearList));
//        yearFilter.setItemLabelGenerator(year -> String.valueOf(year));
//        yearFilter.setValue(currentYear);
    }

    @Subscribe(id = "clearHeaderFilter", subject = "clickListener")
    public void onClearHeaderFilterClick(final ClickEvent<JmixButton> event) {
        long filterColCount = auditLogDocsDataGrid.getColumns().stream()
                .map(DataGridColumn.class::cast)
                .map(DataGridColumn::getHeaderComponent)
                .filter(Objects::nonNull)
                .filter(DataGridHeaderFilter.class::isInstance)
                .map(DataGridHeaderFilter.class::cast)
                .filter(headerFilter -> Optional.ofNullable(headerFilter.getPropertyFilter().getQueryCondition())
                        .map(PropertyCondition::getParameterValue)
                        .isPresent())
                .peek(this::clearFilter)
                .count();

        if (filterColCount > 0) {
            auditLogDocsDl.load();
            initHeaderFilters();
        }
    }

    private void clearFilter(DataGridHeaderFilter headerFilter) {
        headerFilter.getPropertyFilter().clear();
        updateFilterSelectionHandler(headerFilter, Boolean.FALSE);
    }

    private void updateFilterSelectionHandler(DataGridHeaderFilter headerFilter, boolean activateFilterIcon) {
        headerFilter.getChildren()
                .findFirst()
                .flatMap(parentComp -> parentComp.getChildren()
                        .filter(JmixButton.class::isInstance)
                        .map(JmixButton.class::cast)
                        .findFirst())
                .ifPresent(filterBtn -> filterBtn.getElement().setAttribute(
                        DataGridHeaderFilter.COLUMN_FILTER_BUTTON_ACTIVATED_ATTRIBUTE_NAME, activateFilterIcon));
    }

//    @Subscribe("yearFilter")
//    public void onYearFilterComponentValueChange(final AbstractField.ComponentValueChangeEvent<JmixComboBox<Integer>, Integer> event) {
//        if(! queryParamsPresent) {
//            calculateFinancialYear();
//        }
//    }

    private void calculateFinancialYear() {
        int currentYear = Year.now().getValue();
        if (Objects.nonNull(currentYear)) {
            LocalDate fromDate = null;
            LocalDate toDate = null;
            if (currentYear < Year.now().getValue()) {
                fromDate = LocalDate.of(currentYear, Month.APRIL, 01);
                toDate = LocalDate.of(currentYear + 1, Month.MARCH, 31);
            } else {
                LocalDate currentDate = LocalDate.now();
                if (currentDate.isBefore(LocalDate.of(currentDate.getYear(), Month.APRIL, 01))) {
                    fromDate = LocalDate.of(currentYear - 1, Month.APRIL, 01);
                    toDate = LocalDate.of(currentDate.getYear(), Month.MARCH, 31);
                } else {
                    fromDate = LocalDate.of(currentDate.getYear(), Month.APRIL, 01);
                    toDate = LocalDate.of(currentDate.getYear() + 1, Month.MARCH, 31);
                }
            }

            if(Objects.nonNull(fromDate) && Objects.nonNull(toDate)) {
                updateDateRange(fromDate, toDate);
            }
        }

    }

    private void updateDateRange(LocalDate fromDate, LocalDate toDate) {
        clearDateFilter();
        dateFrom.setValue(fromDate);
        dateTo.setValue(toDate);

//        dateFrom.setMin(fromDate);
//        dateFrom.setMax(toDate);

//        dateTo.setMin(fromDate);
//        dateTo.setMax(toDate);

        auditLogDocsDl.load();
    }

    private void clearDateFilter() {
        dateFrom.clear();
        dateTo.clear();

        dateFrom.setMin(null);
        dateFrom.setMax(null);
        dateTo.setMin(null);
        dateTo.setMax(null);
    }

    @Subscribe("auditLogDocsDataGrid.excelExport")
    public void onAuditLogDocsDataGridExcelExport(final ActionPerformedEvent event) {
        log.info("=== Excel Export event invoked ==");
        promptForDocumentName();
    }

    private void promptForDocumentName() {
        String defaultFileName = "AuditLog";
        dialogs.createInputDialog(this)
                .withHeader("Enter File Name")
                .withLabelsPosition(Dialogs.InputDialogBuilder.LabelsPosition.TOP)
                .withParameters(
                        InputParameter.stringParameter("docName").withRequired(true)
                                .withLabel("Name").withDefaultValue(defaultFileName)
                )
                .withValidator(context -> {
                    String docName = context.getValue("docName");
                    if (StringUtils.isBlank(docName)) {
                        return ValidationErrors.of("File name should be filled.");
                    }
                    return ValidationErrors.none();
                })
                .withActions(
                        InputDialogAction.action("Rename")
                                .withText("Rename")
                                .withVariant(ActionVariant.PRIMARY)
                                .withValidationRequired(true)
                                .withHandler(actionEvent -> {
                                    InputDialogAction inputDialogAction = ((InputDialogAction) actionEvent.getSource());
                                    InputDialog inputDialog = inputDialogAction.getInputDialog();
                                    String fileName = inputDialog.getValue("docName");
                                    inputDialog.closeWithDefaultAction();
                                    auditLogDocsDataGridExcelExport.executeExportOperation(dateFrom.getValue(), dateTo.getValue(), fileName);
                                }),
                        InputDialogAction.action("Cancel")
                                .withText("Cancel")
                                .withValidationRequired(true)
                                .withHandler(actionEvent -> {
                                    InputDialogAction inputDialogAction = ((InputDialogAction) actionEvent.getSource());
                                    InputDialog inputDialog = inputDialogAction.getInputDialog();
                                    inputDialog.closeWithDefaultAction();
                                    auditLogDocsDataGridExcelExport.executeExportOperation(dateFrom.getValue(), dateTo.getValue(), defaultFileName);
                                })
                )
                .open();
    }
    private void disablePagination() {
        pagination.getChildren()
                .map(Div.class::cast)
                .flatMap(f -> f.getChildren())
                .filter(Button.class::isInstance)
                .map(Button.class::cast)
                .filter(r -> StringUtils.contains(r.getIcon().getElement().getAttribute(ICON_ATTRIBUTE_NAME), VaadinIcon.ANGLE_DOUBLE_LEFT.name().toLowerCase().replace("_", "-")) ||
                        StringUtils.contains(r.getIcon().getElement().getAttribute(ICON_ATTRIBUTE_NAME), VaadinIcon.ANGLE_DOUBLE_RIGHT.name().toLowerCase().replace("_", "-")))
                .forEach(e -> e.setVisible(Boolean.FALSE));
    }

    private LocalDate parseDate(String dateStr) {
        if (StringUtils.isNotBlank(dateStr)) {
            return LocalDate.parse(dateStr, AuditLogSearchRepository.DATE_TIME_FORMATTER);
        }
        return null;
    }

    private void setTextBoxValue(TypedTextField textField, String filterValue) {
        textField.setValue(filterValue);
    }
    private void setComboBoxValue(ComboBox comboBox, String filterValue) {
        comboBox.setValue(filterValue);
    }

    private void setMultiSelectComboBoxValue(JmixMultiSelectComboBox multiSelectComboBox, String filterValue) {
        multiSelectComboBox.setValue(Arrays.asList(filterValue));
    }
}
