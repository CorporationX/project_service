package faang.school.projectservice.service.presentation;

import faang.school.projectservice.model.Project;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
@Slf4j
public class PresentationPdfGenerator {

    public byte[] generatePdf(Project project) throws Exception {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            float margin = 50;
            float yStart = page.getMediaBox().getHeight() - margin;
            float xStart = margin;
            float yPosition = yStart;

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 26);
                contentStream.newLineAtOffset(xStart, yPosition);
                contentStream.showText("Project presentation");
                contentStream.endText();
                yPosition -= 40;

//                contentStream.beginText();
//                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18);
//                contentStream.newLineAtOffset(xStart, yPosition);
//                contentStream.showText("Project Overview");
//                contentStream.endText();
//                yPosition -= 25;
//
//                contentStream.beginText();
//                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
//                contentStream.newLineAtOffset(xStart, yPosition);
//                contentStream.showText("Name: " + project.getName());
//                contentStream.endText();
//                yPosition -= 15;
//
//                contentStream.beginText();
//                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
//                contentStream.newLineAtOffset(xStart, yPosition);
//                contentStream.showText("Description: " + project.getDescription());
//                contentStream.endText();
//                yPosition -= 15;
//
//                contentStream.beginText();
//                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
//                contentStream.newLineAtOffset(xStart, yPosition);
//                contentStream.showText("Creation Date: " + project.getCreatedAt());
//                contentStream.endText();
//                yPosition -= 15;
//
//                contentStream.beginText();
//                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
//                contentStream.newLineAtOffset(xStart, yPosition);
//                contentStream.showText("Status: " + project.getStatus());
//                contentStream.endText();
//                yPosition -= 15;
//
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                contentStream.newLineAtOffset(xStart, yPosition);
                contentStream.showText("Project Owner ID: " + project.getOwnerId());
                contentStream.endText();
                yPosition -= 25;
//
                if (project.getParentProject() != null) {
                    contentStream.beginText();
                    contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18);
                    contentStream.newLineAtOffset(xStart, yPosition);
                    contentStream.showText("Parent Project:");
                    contentStream.endText();
                    yPosition -= 15;

                    contentStream.beginText();
                    contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                    contentStream.newLineAtOffset(xStart, yPosition);
                    contentStream.showText("Name: " + project.getParentProject().getName());
                    contentStream.endText();
                }
            }

            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error generating presentation PDF: {}", e.getMessage());
            throw e;
        }
    }
}