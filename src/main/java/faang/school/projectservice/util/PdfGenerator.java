package faang.school.projectservice.util;

import faang.school.projectservice.exception.PdfCreateException;
import faang.school.projectservice.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class PdfGenerator {

    public byte[] generateProjectPresentationNew(Project project, List<Team> teams, List<Task> tasks) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDType0Font fontBold = PDType0Font.load(document, new File("src/main/resources/font/poppins-bold.ttf"));
            PDType0Font fontRegular = PDType0Font.load(document, new File("src/main/resources/font/poppins-regular.ttf"));

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                int startY = 750;
                int margin = 50;

                // 1️⃣ Общие сведения о проекте
                contentStream.setFont(fontBold, 16);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, startY);
                contentStream.showText("Project Overview");
                contentStream.endText();

                startY -= 30;
                contentStream.setFont(fontRegular, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, startY);
                contentStream.showText("Name: " + project.getName());
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Description: " + project.getDescription());
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Created Date: " + project.getCreatedAt());
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Status: " + project.getStatus());
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Owner ID: " + project.getOwnerId());
                contentStream.newLineAtOffset(0, -15);
                contentStream.endText();

                // 2️⃣ Команда проекта
                startY -= 90;
                contentStream.setFont(fontBold, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, startY);
                contentStream.showText("Project Team");
                contentStream.endText();

                startY -= 20;
                contentStream.setFont(fontRegular, 12);
                for (Team team : teams) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, startY);
                    contentStream.showText("Team ID: " + team.getId() + " (Avatar: " + team.getAvatarKey() + ")");
                    contentStream.endText();
                    startY -= 15;
                    for (TeamMember member : team.getTeamMembers()) {
                        contentStream.beginText();
                        contentStream.newLineAtOffset(margin + 20, startY);
                        contentStream.showText("- " + member.getNickname() + " (" + member.getRoles() + ")");
                        contentStream.endText();
                        startY -= 15;
                    }
                    startY -= 10;
                }

                // 3️⃣ Достижения
                startY -= 30;
                contentStream.setFont(fontBold, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, startY);
                contentStream.showText("Achievements (Completed Tasks)");
                contentStream.endText();

                startY -= 20;
                contentStream.setFont(fontRegular, 12);
                for (Task task : tasks) {
                    if (task.getStatus() == TaskStatus.DONE) {
                        contentStream.beginText();
                        contentStream.newLineAtOffset(margin, startY);
                        contentStream.showText("- " + task.getName());
                        contentStream.endText();
                        startY -= 15;
                    }
                }

                // 4️⃣ Статистика
                long completedTasks = tasks.stream().filter(task -> task.getStatus() == TaskStatus.DONE).count();
                long totalTeamMembers = teams.stream().mapToLong(team -> team.getTeamMembers().size()).sum();

                startY -= 30;
                contentStream.setFont(fontBold, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, startY);
                contentStream.showText("Project Statistics");
                contentStream.endText();

                startY -= 20;
                contentStream.setFont(fontRegular, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, startY);
                contentStream.showText("Total Completed Tasks: " + completedTasks);
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Total Team Members: " + totalTeamMembers);
                contentStream.endText();
            }

            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new PdfCreateException("PDF create error!");
        }
    }

}
