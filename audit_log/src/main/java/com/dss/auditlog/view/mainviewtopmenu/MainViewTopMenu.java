package com.dss.auditlog.view.mainviewtopmenu;

import com.dss.auditlog.elasticsearch.repository.AuditLogSearchRepository;
import com.dss.auditlog.entity.DashBoardDataAttributes;
import com.dss.auditlog.events.BackgroundTaskEvent;
import com.dss.auditlog.events.ProgressBarEvent;
import com.dss.auditlog.excel.CustomFileDownloader;
import com.dss.auditlog.utils.DashboardDataHolder;
import com.dss.auditlog.view.auditlogdoc.AuditLogDocListView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import io.jmix.chartsflowui.component.Chart;
import io.jmix.chartsflowui.data.item.MapDataItem;
import io.jmix.chartsflowui.kit.component.event.ChartClickEvent;
import io.jmix.chartsflowui.kit.component.model.DataSet;
import io.jmix.chartsflowui.kit.data.chart.ListChartItems;
import io.jmix.core.entity.KeyValueEntity;
import io.jmix.flowui.UiComponents;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.app.main.StandardMainView;
import io.jmix.flowui.backgroundtask.BackgroundTaskHandler;
import io.jmix.flowui.component.UiComponentUtils;
import io.jmix.flowui.component.datepicker.TypedDatePicker;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.kit.component.ComponentUtils;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.kit.component.dropdownbutton.DropdownButton;
import io.jmix.flowui.kit.component.dropdownbutton.DropdownButtonItem;
import io.jmix.flowui.model.DataComponents;
import io.jmix.flowui.model.KeyValueCollectionContainer;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewComponent;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;


@Route("")
@ViewController("AL_MainViewTopMenu")
@ViewDescriptor("main-view-top-menu.xml")
public class MainViewTopMenu extends StandardMainView {

    private static final String SUCCESS_THEME = "success";
    private static final String ERROR_THEME = "error";
    private static final Logger log = LoggerFactory.getLogger(MainViewTopMenu.class);
    @ViewComponent
    private DropdownButton progressTab;
    @Autowired
    private UiComponents uiComponents;
    private Map<String, List<Component>> progressBarMap = new ConcurrentHashMap<>();
    private Map<String, BackgroundTaskHandler> backgroundTaskHandlerMap = new ConcurrentHashMap<>();

    private final static String PROGRESS_BAR_PREFIX = "progressBar_Menu_";
    @Autowired
    private CustomFileDownloader downloader;

    @Autowired
    private AuditLogSearchRepository searchRepository;
    @ViewComponent
    private Chart typeChangePieChart;
    @ViewComponent
    protected Chart documentTypeBarChart;
    @Autowired
    protected DataComponents dataComponents;
    @ViewComponent
    private VerticalLayout contentPane;
    @ViewComponent
    private TypedDatePicker<Comparable> dateFrom;
    @ViewComponent
    private TypedDatePicker<Comparable> dateTo;
    @ViewComponent
    private JmixButton btApplyDateFilter;
    @Autowired
    private ViewNavigators viewNavigators;
    @Autowired
    private ObjectMapper objectMapper;
    @ViewComponent
    private Icon closeIcon;
    @ViewComponent
    private VerticalLayout docTypeHolder;

    @Subscribe
    public void onInit(final InitEvent event) {
        loadDashBoardData(fetchDashBoardResult(TRUE));
    }

    private DashBoardResult fetchDashBoardResult(boolean calculateFinancialYr){
        if(calculateFinancialYr) {
            calculateFinancialYear();
        }
        Map<String, Long> documentTypeRecords = searchRepository.getDocumentTypeRecords(dateFrom.getValue(), dateTo.getValue());
        Map<String, Long> typeOfChangeRecords = searchRepository.getTypeOfChangeRecords(dateFrom.getValue(), dateTo.getValue());
        Collection<DashboardDataHolder> usersByTypeOfChange = searchRepository.getUsersByTypeOfChange(dateFrom.getValue(), dateTo.getValue());
        Collection<DashboardDataHolder> tableCaptionByTypeOfChange = searchRepository.getTableCaptionByTypeOfChange(dateFrom.getValue(), dateTo.getValue());
        return new DashBoardResult(documentTypeRecords, typeOfChangeRecords, usersByTypeOfChange, tableCaptionByTypeOfChange);
    }

    @Subscribe("typeChangePieChart")
    public void onTypeChangePieChartChartClick(final ChartClickEvent event) {
        try {
            Map<String, Object> pieCharDataMap = objectMapper.readValue(event.getValue(), Map.class);
            Map<String, String> propertyFilterMap = Map.of("typeOfChange", pieCharDataMap.get("description").toString());
            navigateToAuditLogView(propertyFilterMap, DashBoardDataAttributes.DashBoardComponentType.PIE_CHART);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Subscribe("documentTypeBarChart")
    public void onDocumentTypeBarChartChartClick(final ChartClickEvent event) {
        try {
            Map<String, Object> pieCharDataMap = objectMapper.readValue(event.getValue(), Map.class);
            Map<String, String> propertyFilterMap = Map.of("documentType", pieCharDataMap.get("description").toString());
            navigateToAuditLogView(propertyFilterMap, DashBoardDataAttributes.DashBoardComponentType.BAR_CHART);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void navigateToAuditLogView(Map<String, String> propertyFilterMap, DashBoardDataAttributes.DashBoardComponentType dashBoardComponentType){
        try {
            log.info("=== Navigating to auditLogListView...... : {}", propertyFilterMap);
            DashBoardDataAttributes dashBoardDataAttributes = new DashBoardDataAttributes();
            dashBoardDataAttributes.setFromDate(Objects.nonNull(dateFrom.getValue()) ? dateFrom.getValue().toString() : null);
            dashBoardDataAttributes.setToDate(Objects.nonNull(dateTo.getValue()) ? dateTo.getValue().toString() : null);
            dashBoardDataAttributes.setComponentType(dashBoardComponentType);
            dashBoardDataAttributes.setPropertyFilterMap(propertyFilterMap);

            String dashBoardDataAttributesJson = objectMapper.writeValueAsString(dashBoardDataAttributes);
            Map<String, String> parameters = new HashMap<>();
            parameters.put("rangeFilter", dashBoardDataAttributesJson);

            viewNavigators.view(this, AuditLogDocListView.class)
                    .withQueryParameters(QueryParameters.simple(parameters))
                    .navigate();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Subscribe(id = "btApplyDateFilter", subject = "clickListener")
    public void onBtApplyDateFilterClick(final ClickEvent<JmixButton> event) {
        if(contentPane.getComponentCount() > 1 && docTypeHolder.getComponentCount() > 1) {
            contentPane.remove(contentPane.getComponentAt(1));
            docTypeHolder.remove(docTypeHolder.getComponentAt(1));
        }
        boolean calculateFinancialYr = Objects.isNull(dateFrom.getValue()) && Objects.isNull(dateTo.getValue());
        loadDashBoardData(fetchDashBoardResult(calculateFinancialYr));
    }

    @Subscribe(id = "closeIcon", subject = "clickListener")
    public void onCloseIconClick(final ClickEvent<Icon> event) {
        if(contentPane.getComponentCount() > 1 && docTypeHolder.getComponentCount() > 1) {
            contentPane.remove(contentPane.getComponentAt(1));
            docTypeHolder.remove(docTypeHolder.getComponentAt(1));
        }
        loadDashBoardData(fetchDashBoardResult(TRUE));
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

    private void loadDashBoardData(DashBoardResult dashBoardResult){
        populateChartData(documentTypeBarChart, dashBoardResult.getDocumentTypeRecords(), "value");
        populateChartData(typeChangePieChart, dashBoardResult.getTypeOfChangeRecords(), "Change Type");
        populateTableData(dashBoardResult.getUsersByTypeOfChange(), TRUE);
        populateTableData(dashBoardResult.getTableCaptionByTypeOfChange(), FALSE);

        dateFrom.setEnabled(TRUE);
        dateTo.setEnabled(TRUE);
        btApplyDateFilter.setEnabled(TRUE);
        closeIcon.setVisible(TRUE);
        closeIcon.setTooltipText("Clear Filter");
    }

    private void populateChartData(Chart chartComponent, Map<String, Long> chartData, String key) {
        chartComponent.setVisible(FALSE);
        if (!chartData.isEmpty()) {
            chartComponent.setVisible(TRUE);
            ListChartItems<MapDataItem> chartItems = new ListChartItems<>();
            chartData.forEach((docType, docCount) -> {
                chartItems.addItem(new MapDataItem(Map.of(key, docCount, "description", StringUtils.isEmpty(docType) ? "BLANK" : docType)));
            });

            chartComponent.withDataSet(
                    new DataSet().withSource(
                            new DataSet.Source<MapDataItem>()
                                    .withDataProvider(chartItems)
                                    .withCategoryField("description")
                                    .withValueField(key)
                    )
            );
        }
    }

    private void populateTableData(Collection<DashboardDataHolder> tableData, boolean isUserTable) {
        if (CollectionUtils.isNotEmpty(tableData)) {
            String userTableKey = "userTable";
            String tableCaptionKey = "tableCaption";

            DataGrid<KeyValueEntity> grid = uiComponents.create(DataGrid.class);
            grid.setId(isUserTable ? userTableKey : tableCaptionKey);
            grid.setWidthFull();
            grid.setHeightFull();
            grid.setSelectionMode(Grid.SelectionMode.SINGLE);

            Collection<KeyValueEntity> containerList = new ArrayList<>();

            tableData.forEach(result -> {
                KeyValueEntity entity = new KeyValueEntity();
                entity.setIdName(UUID.randomUUID().toString());
                entity.setId(UUID.randomUUID());
                entity.setValue("displayName", result.getTableEntry());
                entity.setValue("Modification", result.getModificationCount());
                entity.setValue("Deletion", result.getDeletionCount());
                containerList.add(entity);
            });

            KeyValueCollectionContainer container = dataComponents.createKeyValueCollectionContainer();
            container.addProperty("displayName", String.class);
            container.addProperty("Modification", Long.class);
            container.addProperty("Deletion", Long.class);
            container.setItems(containerList);

            grid.addColumn("tableEntry", Objects.requireNonNull(container.getEntityMetaClass().getPropertyPath("displayName")))
                    .setHeader(isUserTable ? "Users" : "Table Caption").setResizable(TRUE);

            DashBoardDataAttributes.DashBoardComponentType dashBoardComponentType = isUserTable ? DashBoardDataAttributes.DashBoardComponentType.USER_TABLE :
                    DashBoardDataAttributes.DashBoardComponentType.TABLE_CAPTION;

            grid.addComponentColumn((ValueProvider<KeyValueEntity, ? extends Component>) entity ->
                            createComponentColumn(entity, (keyValueEntity) -> {
                                Map<String, String> propertyFilterMap = Map.of(isUserTable ? "createdBy" : "tableCaption", entity.getValue("displayName"),
                                        "typeOfChange", "Modification");
                                navigateToAuditLogView(propertyFilterMap, dashBoardComponentType);
                            }, "Modification"))
                    .setKey("modificationCol").setHeader("Modification")
                    .setSortable(TRUE)
                    .setSortProperty("Modification")
                    .setComparator((entity1, entity2) -> {
                        Long modification1 = entity1.getValue("Modification");
                        Long modification2 = entity2.getValue("Modification");
                        return modification1.compareTo(modification2);
                    });

            grid.addComponentColumn((ValueProvider<KeyValueEntity, ? extends Component>) entity ->
                            createComponentColumn(entity, (keyValueEntity) -> {
                                Map<String, String> propertyFilterMap = Map.of(isUserTable ? "createdBy" : "tableCaption", entity.getValue("displayName"),
                                        "typeOfChange", "Deletion");
                                navigateToAuditLogView(propertyFilterMap, dashBoardComponentType);
                            }, "Deletion")
                    ).setKey("deletionCol")
                    .setHeader("Deletion")
                    .setSortable(TRUE)
                    .setSortProperty("Deletion")
                    .setComparator((entity1, entity2) -> {
                        Long deletion1 = entity1.getValue("Deletion");
                        Long deletion2 = entity2.getValue("Deletion");
                        return deletion1.compareTo(deletion2);
                    });

            grid.setItems(containerList);

            if(isUserTable) {
                docTypeHolder.add(grid);
            } else {
                contentPane.add(grid);
            }

            typeChangePieChart.setMinHeight("40%");
            documentTypeBarChart.setMinHeight("40%");
        }
    }

    private Component createComponentColumn(KeyValueEntity keyValueEntity, Consumer<KeyValueEntity> action, String type){
        Span comp = uiComponents.create(Span.class);
        comp.setId("dashboardCompColumn");
        Long count = keyValueEntity.getValue(type);
        comp.setText(count.toString());

        comp.getElement().getStyle().set("color", "blue");
        comp.getElement().getStyle().set("text-decoration", "underline");

        comp.addClickListener(event -> action.accept(keyValueEntity));
        return comp;
    }

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
    }

    private void clearDateFilter() {
        dateFrom.clear();
        dateTo.clear();

        dateFrom.setMin(null);
        dateFrom.setMax(null);
        dateTo.setMin(null);
        dateTo.setMax(null);
    }

    @Override
    protected void updateTitle() {
        super.updateTitle();
        String viewTitle = getTitleFromOpenedView();
        UiComponentUtils.findComponent(getContent(), "viewHeaderBox")
                .ifPresent(component -> component.setVisible(!Strings.isNullOrEmpty(viewTitle)));
    }

    @EventListener({ProgressBarEvent.class, BackgroundTaskEvent.class})
    public void updateProgressBar(Object object) {
        if (object instanceof ProgressBarEvent event) {
            if (event.isTaskCompleted() || event.isErrorOccurred() && ! progressBarMap.isEmpty()) {
                if(Objects.nonNull(event.getResult())) {
                    downloader.download(getUI(), event.getFileName(), event.getContentType(), event.getResult());
                }

                boolean hasError = event.isErrorOccurred();
                String theme = hasError ? ERROR_THEME : SUCCESS_THEME;

                Optional<ProgressBar> progressBarOptional = getProgressBar(event.getProgressId());
                progressBarOptional.ifPresent(p -> p.setThemeName(theme));

                progressBarMap.remove(event.getProgressId());
                backgroundTaskHandlerMap.remove(event.getProgressId());
                return;
            }

            if (! progressBarMap.containsKey(event.getProgressId())) {
                Component component = buildProgressBarComponent(event.getProgressId(), event.getFileName());
                progressTab.addItem(PROGRESS_BAR_PREFIX + event.getProgressId(), component);

                if(Objects.isNull(progressTab.getItem("clearBtnComp"))) {
                    Button clearBtn = uiComponents.create(Button.class);
                    clearBtn.setText("Clear");
                    clearBtn.setId("mainViewTopClearBtn");
                    clearBtn.addClickListener(clear -> {
                        log.info("clear notification invoked...");
                        clearNotification();
                        updateProgressMenu();
                    });
                    progressTab.addItem("clearBtnComp", clearBtn, 0);
                }
            }

            if (!progressBarMap.isEmpty()) {
                if (Objects.nonNull(event.getProgressId()) && Objects.nonNull(event.getProgressValue())) {
                    getProgressBar(event.getProgressId()).ifPresent(progress -> progress.setValue(event.getProgressValue()));

                    getPercentComponent(event.getProgressId()).ifPresent(percentComp -> {
                        BigDecimal progressValue = BigDecimal.valueOf(event.getProgressValue());
                        BigDecimal percentageCalc = progressValue.multiply(BigDecimal.valueOf(100));
                        percentageCalc = percentageCalc.setScale(0, RoundingMode.HALF_UP);
                        Double result = percentageCalc.doubleValue();
                        percentComp.setText(String.format("%.0f%%", result));
                    });
                }
            }
        } else {
            BackgroundTaskEvent backgroundTaskEvent = (BackgroundTaskEvent) object;
            backgroundTaskHandlerMap.put(backgroundTaskEvent.getProgressId(), backgroundTaskEvent.getBackgroundTaskHandler());
        }
    }

    private void updateProgressMenu(){
        if (progressTab.getItems().size() <= 1) {
            progressTab.remove("clearBtnComp");
        }
    }
    private void clearNotification(){
        progressTab.getContent().getItems().stream().findFirst().ifPresent(content -> content.getSubMenu().getItems()
                .forEach(item -> {
                    boolean isTaskComplete = Stream.of(item)
                            .filter(r -> r.getId().isPresent() && ! StringUtils.equalsIgnoreCase("clearBtn", r.getId().get()))
                            .flatMap(f -> f.getChildren()).flatMap(r -> r.getChildren()).flatMap(t -> t.getChildren())
                            .filter(ProgressBar.class::isInstance)
                            .map(ProgressBar.class::cast)
                            .map(ProgressBar::getThemeName)
                            .anyMatch(m -> StringUtils.equalsIgnoreCase("success", m));

                    if(isTaskComplete) {
                        item.getId().ifPresent(id -> progressTab.remove(id));
                    }
                }));
    }

    private HorizontalLayout buildProgressBarComponent(String id, String docName) {
        ProgressBar progressBar = uiComponents.create(ProgressBar.class);
        progressBar.setId(id + "_progress_bar");
        progressBar.setMin(0.0);
        progressBar.setMax(1.0);
        progressBar.setWidth(20, Unit.EM);

        HorizontalLayout progressBarHolder = uiComponents.create(HorizontalLayout.class);
        progressBarHolder.setId(id + "_parent_layout");
        progressBarHolder.getElement().setAttribute("style", "display: flex; flex-direction: column;");

        HorizontalLayout captionHolder = uiComponents.create(HorizontalLayout.class);
        captionHolder.setId(id + "_status_holder");
        captionHolder.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        Span caption = new Span(docName);
        caption.setId(id + "_caption");
        Span percentageValue = new Span();
        percentageValue.setText(progressBar.getValue() * 100 + "%");
        percentageValue.setId(id + "_percent");
        captionHolder.add(caption, percentageValue);

        Icon deleteIcon = ComponentUtils.parseIcon("lumo:cross");
        deleteIcon.setId(id + "_icon");
        deleteIcon.setColor("red");
        deleteIcon.getElement().getStyle().set("cursor", "pointer");
        deleteIcon.setVisible(TRUE);

        deleteIcon.addClickListener(icon -> {
            DropdownButtonItem item = progressTab.getItem(PROGRESS_BAR_PREFIX + id);
            if (Objects.nonNull(item) && backgroundTaskHandlerMap.containsKey(id)) {
                backgroundTaskHandlerMap.get(id).cancel();
                progressTab.remove(item);
                updateProgressMenu();
            }
        });

        List<Component> components = new ArrayList<>();
        components.add(progressBar);
        components.add(caption);
        components.add(percentageValue);
        components.add(deleteIcon);

        progressBarMap.put(id, components);

        HorizontalLayout iconHolder = uiComponents.create(HorizontalLayout.class);
        iconHolder.setId(id + "_icon_holder");
        iconHolder.add(progressBar, deleteIcon);

        progressBarHolder.add(captionHolder, iconHolder);
        return progressBarHolder;
    }

    private Optional<?> getComponentById(String progressId, ComponentAttributes id, Class<?> type) {
        return progressBarMap.get(progressId).stream().filter(type::isInstance)
                .map(type::cast)
                .filter(comp -> ((Component) comp).getId().isPresent() && ((Component) comp).getId().get().contains(id.getAttribute()))
                .findFirst();
    }

    private Optional<ProgressBar> getProgressBar(String id) {
        return getComponentById(id, ComponentAttributes.PROGRESS_BAR, ProgressBar.class)
                .filter(ProgressBar.class::isInstance)
                .map(ProgressBar.class::cast);
    }

    private Optional<Span> getCaptionComponent(String id) {
        return getComponentById(id, ComponentAttributes.CAPTION, Span.class)
                .filter(Span.class::isInstance)
                .map(Span.class::cast);
    }

    private Optional<Span> getPercentComponent(String id) {
        return getComponentById(id, ComponentAttributes.PERCENTAGE, Span.class)
                .filter(Span.class::isInstance)
                .map(Span.class::cast);
    }

    private Optional<Icon> getIconComponent(String id) {
        return getComponentById(id, ComponentAttributes.ICON, Icon.class)
                .filter(Icon.class::isInstance)
                .map(Icon.class::cast);
    }


    enum ComponentAttributes {
        PROGRESS_BAR("_progress_bar"),
        CAPTION("_caption"),
        PERCENTAGE("_percent"),
        ICON("_icon");

        @Getter
        private String attribute;

        ComponentAttributes(String compId) {
            this.attribute = compId;
        }
    }

    enum ProgressStatus {
        IN_PROGRESS("In progress"),
        COMPLETED("Completed"),

        FAILED("Failed");

        @Getter
        private String status;

        ProgressStatus(String status) {
            this.status = status;
        }
    }

    @Getter @Setter @AllArgsConstructor
    class DashBoardResult {
        Map<String, Long> documentTypeRecords;
        Map<String, Long> typeOfChangeRecords;
        Collection<DashboardDataHolder> usersByTypeOfChange;
        Collection<DashboardDataHolder> tableCaptionByTypeOfChange;
    }
}