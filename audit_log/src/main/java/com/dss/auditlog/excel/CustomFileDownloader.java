package com.dss.auditlog.excel;

import com.dss.auditlog.utils.SerializableBiConsumer;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.server.RequestHandler;
import io.jmix.flowui.UiComponents;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class CustomFileDownloader {
    private SerializableConsumer<OutputStream> contentWriter;
    private RequestHandler requestHandler;
    private final UiComponents uiComponents;

    @Autowired
    public CustomFileDownloader(UiComponents uiComponents) {
        this.uiComponents = uiComponents;
    }

    public void download(Optional<UI> uiOptional, String docName, String contentType, final Workbook workbook) {
        try {
            initContentWriter(workbook);
            Anchor downloadLink = uiComponents.create(Anchor.class);
            uiOptional.ifPresent(ui -> ui.add(downloadLink));
            runBeforeClientResponse(this::beforeClientResponseDownloadHandler, downloadLink, new RequestHandlerAttributes(downloadLink, docName, contentType));

            downloadLink.getElement().executeJs("this.click()");
        } catch (Exception ex) {
            log.error("Error while downloading data: {}", ex);
        }
    }

    private void runBeforeClientResponse(SerializableBiConsumer<UI, RequestHandlerAttributes> command, Anchor downloadLink, RequestHandlerAttributes attributes) {
        downloadLink.getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(
                        downloadLink,
                        context -> command.accept(ui, attributes)
                )
        );
    }

    private void beforeClientResponseDownloadHandler(UI ui, RequestHandlerAttributes attributes) {
        String identifier = "download/" + UUID.randomUUID();
        Anchor downloadLink = attributes.downloadLink();

        requestHandler = (session, request, response) -> {
            if (request.getPathInfo().endsWith(identifier)) {
                try {
                    response.setStatus(200);
                    response.setContentType(attributes.contentType());
                    response.setHeader(
                            "Content-Disposition",
                            ContentDisposition.builder("attachment")
                                    .filename(attributes.docName(), StandardCharsets.UTF_8)
                                    .build()
                                    .toString());
                    contentWriter.andThen(x -> {
                                log.info("Detaching anchor tag from dom...");
                                ui.access(() -> ui.remove(attributes.downloadLink()));
                            })
                            .accept(response.getOutputStream());
                } catch (Exception ex) {
                    log.error("Error while streaming data to browser: {}", ex);
                } finally {
                    response.getOutputStream().close();
                }
                return true;
            }
            return false;
        };

        ui.getSession().addRequestHandler(requestHandler);

        downloadLink.getElement().setAttribute("download", attributes.docName() + ".xlsx");
        downloadLink.setHref("./" + identifier);

        ui.add(downloadLink);

        downloadLink.addDetachListener(x -> {
            log.info("Removing handler from session...");
            ui.getSession().removeRequestHandler(requestHandler);
        });
    }

    private void initContentWriter(final Workbook workbook) {
        contentWriter = (stream) -> {
            try {
                workbook.write(stream);
                stream.flush();
            } catch (IOException io) {
                throw new RuntimeException(io);
            } finally {
                try {
                    workbook.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
    private record RequestHandlerAttributes(Anchor downloadLink, String docName, String contentType) {}
}

