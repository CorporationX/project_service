package faang.school.projectservice.service.presentation.generator;

import com.lowagie.text.DocumentException;
import faang.school.projectservice.dto.presentation.ProjectPresentationDto;
import faang.school.projectservice.exeption.PdfGenerationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfGeneratorImpl implements PdfGenerator {

    private static final String TEMPLATE_NAME = "presentation-template.html";
    private static final String TEMP_FILE_PREFIX = "project_presentation_";
    private static final String TEMP_FILE_SUFFIX = ".pdf";

    private final TemplateEngine templateEngine;

    @Override
    public File generateToFile(ProjectPresentationDto presentationDto) {
        try {
            String html = renderHtml(presentationDto);
            return generatePdfFromHtml(html);
        } catch (IOException | DocumentException e) {
            log.error("Failed to generate PDF for project {}", presentationDto.project().name(), e);
            throw new PdfGenerationException(
                    "Failed to generate PDF for project: " + presentationDto.project().name()
            );
        }
    }

    private String renderHtml(ProjectPresentationDto presentationDto) {
        Context context = new Context();
        context.setVariable("project", presentationDto);
        return templateEngine.process(TEMPLATE_NAME, context);
    }

    private File generatePdfFromHtml(String html) throws IOException, DocumentException {
        File tempFile = File.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX);
        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);
        }
        return tempFile;
    }
}
