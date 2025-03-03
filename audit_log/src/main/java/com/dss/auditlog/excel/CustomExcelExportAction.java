package com.dss.auditlog.excel;

import com.vaadin.flow.component.grid.Grid;
import io.jmix.core.common.util.Preconditions;
import io.jmix.flowui.action.ActionType;
import io.jmix.flowui.action.DialogAction;
import io.jmix.flowui.action.SecuredBaseAction;
import io.jmix.flowui.kit.action.Action;
import io.jmix.flowui.kit.action.ActionVariant;
import io.jmix.gridexportflowui.action.ExportAction;
import io.jmix.gridexportflowui.exporter.ExportMode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ActionType(CustomExcelExportAction.ID)
@Slf4j
public class CustomExcelExportAction extends ExportAction {

    public static final String ID = "grdexp_custom_excelExport";

    public CustomExcelExportAction() {
        this(ID);
    }

    public CustomExcelExportAction(String id) {
        super(id);
    }

    public void executeExportOperation(LocalDate from, LocalDate to, String fileName) {
        log.info("-- custom executeExportOperation ---");
        Preconditions.checkNotNullArgument(dataGridExporter,
                Grid.class.getSimpleName() + " exporter is not defined");

        Action exportAllAction = customCreateExportAllAction(from, to, fileName);
        Action exportCurrentPageAction = customCreateCurrentPageAction(from, to, fileName);
        Action exportSelectedAction = createExportSelectedAction();

        List<Action> actions = new ArrayList<>();

        if (isDataLoaderExist(target)) {
            actions.add(exportAllAction);
        }

        actions.add(exportCurrentPageAction);

        if (!target.getSelectedItems().isEmpty()) {
            actions.add(exportSelectedAction);
        }

        actions.add(new DialogAction(DialogAction.Type.CANCEL));

        if (actions.contains(exportAllAction)) {
            exportAllAction.setVariant(ActionVariant.PRIMARY);
        } else {
            exportCurrentPageAction.setVariant(ActionVariant.PRIMARY);
        }

        dialogs.createOptionDialog()
                .withHeader(getMessage("exportConfirmationDialog.header"))
                .withText(getMessage("exportConfirmationDialog.message"))
                .withActions(actions.toArray(new Action[0]))
                .withWidth("32em")
                .open();
    }

    private Action customCreateExportAllAction(LocalDate from, LocalDate to, String fileName) {
        return new SecuredBaseAction("ExportMode.CURRENT_PAGE")
                .withText(messages.getMessage(ExportMode.ALL_ROWS))
                .withHandler(event -> customDoExport(ExportMode.ALL_ROWS, from, to, fileName));
    }

    private Action customCreateCurrentPageAction(LocalDate from, LocalDate to, String fileName) {
        return new SecuredBaseAction("ExportMode.CURRENT_PAGE")
                .withText(messages.getMessage(ExportMode.CURRENT_PAGE))
                .withHandler(event -> customDoExport(ExportMode.CURRENT_PAGE, from, to, fileName));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        super.setApplicationContext(applicationContext);
        withExporter(AuditLogExcelExporter.class);
    }

    public void customDoExport(ExportMode exportMode, LocalDate from, LocalDate to, String fileName) {
        if (getTarget() instanceof Grid) {
            ((AuditLogExcelExporter) dataGridExporter).customExportDataGrid(downloader, (Grid) getTarget(), exportMode, from, to, fileName);
        } else {
            throw new UnsupportedOperationException("Unsupported component for export");
        }
    }

    @Override
    protected String getMessage(String id) {
        return super.getMessage(getAuditLogExportDialogId(id));
    }

    private String getAuditLogExportDialogId(String id) {
        return switch (id) {
            case "exportConfirmationDialog.header" -> "CustomExportConfirmationDialog.header";
            case "exportConfirmationDialog.message" -> "CustomExportConfirmationDialog.message";
            default -> id;
        };
    }
}
