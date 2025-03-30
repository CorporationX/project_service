package faang.school.projectservice.service.presentation;

import faang.school.projectservice.model.Project;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class PresentationPdfGenerator {
    private final FileStorageService fileStorageService;

    public byte[] generatePdf(Project project) throws Exception {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float xStart = 50;
                float yPosition = 700;

                drawText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD),
                        26, xStart, yPosition, "Project presentation");
                yPosition -= 40;

                drawText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD),
                        18, xStart, yPosition, "Project Overview");
                yPosition -= 25;

                drawText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD),
                        12, xStart, yPosition, "Name: " + project.getName());
                yPosition -= 15;

                drawText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD),
                        12, xStart, yPosition, "Description: " + project.getDescription());
                yPosition -= 15;

                drawText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD),
                        12, xStart, yPosition, "Creation Date: " + project.getCreatedAt());
                yPosition -= 15;

                drawText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD),
                        12, xStart, yPosition, "Status: " + project.getStatus());
                yPosition -= 15;

                drawText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD),
                        12, xStart, yPosition, "Project Owner ID: " + project.getOwnerId());
                yPosition -= 25;

                if (project.getParentProject() != null) {
                    drawText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD),
                            18, xStart, yPosition, "Parent Project");
                    yPosition -= 15;

                    drawText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD),
                            12, xStart, yPosition, "Name: " + project.getParentProject().getName());
                }
            }

            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error generating presentation PDF: {}", e.getMessage());
            throw e;
        }
    }

    private void drawText(PDPageContentStream contentStream, PDType1Font font, float fontSize,
                          float x, float y, String text) throws IOException {
        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
    }
}