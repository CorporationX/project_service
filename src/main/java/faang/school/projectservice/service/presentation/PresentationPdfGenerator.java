package faang.school.projectservice.service.presentation;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PresentationPdfGenerator {

    private final FileStorageService fileStorageService;
    private final UserServiceClient userServiceClient;

    public byte[] generatePdf(Project project) throws Exception {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDType1Font boldFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float xStart = 250;
                float yPosition = 750;

                byte[] image = fileStorageService.downloadImageFromMinio(project.getCoverImageId());
                PDImageXObject pdImage =
                        PDImageXObject.createFromByteArray(document, image, project.getCoverImageId());

                float imageX = 50;
                float imageY = 625;
                float imageWidth = 150;
                float imageHeight = 150;

                contentStream.drawImage(pdImage, imageX, imageY, imageWidth, imageHeight);

                yPosition = drawText(contentStream, boldFont, 26,
                        xStart, yPosition, "Project presentation", 40);
                yPosition = drawText(contentStream, boldFont, 18,
                        xStart, yPosition, "Project Overview", 25);
                yPosition = drawText(contentStream, font, 12,
                        xStart, yPosition, "Name: " + project.getName(), 15);
                yPosition = drawText(contentStream, font, 12,
                        xStart, yPosition, "Description: " + project.getDescription(), 15);
                yPosition = drawText(contentStream, font, 12, xStart, yPosition, "Creation Date: " +
                        formatCreatedAt(project.getCreatedAt()), 15);
                yPosition = drawText(contentStream, font, 12,
                        xStart, yPosition, "Status: " + project.getStatus().getName(), 32);

                yPosition = drawText(contentStream, font, 12, xStart, yPosition,
                        "Project Owner ID: " + userServiceClient.getUser(project.getOwnerId()), 25);

                yPosition = drawText(contentStream, boldFont, 16,
                        xStart, yPosition, "Task amount: " + project.getTasks().size(), 32);

                if (!project.getTasks().isEmpty()) {
                    yPosition = drawText(contentStream, boldFont, 16,
                            xStart, yPosition, "Tasks: ", 32);

                    for (Task task : project.getTasks()) {
                        yPosition = drawText(contentStream, font, 12,
                                xStart, yPosition, " - " + task.getName(), 25);
                    }
                }

                yPosition = drawText(contentStream, boldFont, 16,
                        xStart, yPosition, "Team amount: " + project.getTeams().size(), 32);

                if (!project.getTeams().isEmpty()) {
                    for (Team team : project.getTeams()) {
                        yPosition = drawText(contentStream, boldFont, 12,
                                xStart, yPosition, "Team with ID: " + team.getId(), 25);
                        for (TeamMember teamMember : team.getTeamMembers()) {
                            yPosition = drawText(contentStream, boldFont, 12, xStart, yPosition, " - "
                                    + teamMember.getNickname() + ", " + teamMember.getRoles().toString(), 25);
                        }
                    }
                }
                if (project.getParentProject() != null) {
                    yPosition = drawText(contentStream, boldFont, 18,
                            xStart, yPosition, "Parent Project", 15);
                    drawText(contentStream, font, 12,
                            xStart, yPosition, "Name: " + project.getParentProject().getName(), 0);
                }
            }

            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("Error generating presentation PDF: {}", e.getMessage());
            throw e;
        }
    }

    private float drawText(PDPageContentStream contentStream, PDType1Font font, float fontSize,
                           float x, float y, String text, float yDecrement) throws IOException {
        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
        return y - yDecrement;
    }

    private String formatCreatedAt(LocalDateTime createdAt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return createdAt.format(formatter);
    }
}