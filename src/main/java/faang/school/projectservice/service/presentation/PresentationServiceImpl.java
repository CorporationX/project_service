package faang.school.projectservice.service.presentation;


import faang.school.projectservice.dto.project.ProjectPresentationDto;
import faang.school.projectservice.dto.project.ProjectTeamMemberDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PresentationServiceImpl implements PresentationService {

    public static final String DATE_MASK_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String FONT_REGULAR = "fontRegular";
    public static final String FONT_BOLD = "fontBold";
    public static final String FONTS_ARIAL_TTF = "/fonts/arial.ttf";
    public static final String FONTS_ARIAL_BOLD_TTF = "/fonts/arial-bold.ttf";

    @Override
    public InputStream createProjectPresentation(ProjectPresentationDto dto) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            Map<String, PDType0Font> fonts = getFonts(document);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                headerDraw(dto, contentStream, fonts);
                bodyDraw(dto, contentStream, fonts);
                footerDraw(dto, contentStream, fonts);
                contentStream.endText();
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());

        } catch (IOException ex) {
            throw new RuntimeException("Error creating PDF document", ex);
        }
    }

    private Map<String, PDType0Font> getFonts(PDDocument document) throws IOException {
        Map<String, PDType0Font> mapFonts = new HashMap<>();
        try (InputStream fontStreamRegular = getClass().getResourceAsStream(FONTS_ARIAL_TTF);
             InputStream fontStreamBold = getClass().getResourceAsStream(FONTS_ARIAL_BOLD_TTF)) {

            PDType0Font fontRegular = PDType0Font.load(document, fontStreamRegular);
            PDType0Font fontBold = PDType0Font.load(document, fontStreamBold);

            mapFonts.put(FONT_REGULAR, fontRegular);
            mapFonts.put(FONT_BOLD, fontBold);
        }
        return mapFonts;
    }

    protected void bodyDraw(ProjectPresentationDto dto, PDPageContentStream contentStream,
                            Map<String, PDType0Font> fonts) throws IOException {
        contentStream.setFont(fonts.get(FONT_BOLD), 14);
        spaceLinesDraw(contentStream);
        contentStream.showText("Список задач:");
        contentStream.setFont(fonts.get(FONT_REGULAR), 12);
        for (String task : dto.completedTasks()) {
            spaceLinesDraw(contentStream);
            contentStream.showText("- " + task);
        }

        contentStream.setFont(fonts.get(FONT_BOLD), 14);
        spaceLinesDraw(contentStream, 2);
        contentStream.showText("Команды в проекте:");
        contentStream.setFont(fonts.get(FONT_REGULAR), 12);

        int teamCount = 1;
        for (List<ProjectTeamMemberDto> team : dto.teams()) {
            spaceLinesDraw(contentStream);
            contentStream.showText("Команда " + teamCount++ + ":");
            for (ProjectTeamMemberDto member : team) {
                spaceLinesDraw(contentStream);
                contentStream.showText("  - " + member.name() + ", Роль в команде: " + member.roles());
            }
        }
    }

    private void spaceLinesDraw(PDPageContentStream contentStream, int countLine) throws IOException {
        for (int i = 0; i < countLine; i++) {
            contentStream.newLine();
        }
    }

    private void spaceLinesDraw(PDPageContentStream contentStream) throws IOException {
        spaceLinesDraw(contentStream, 1);
    }

    private void footerDraw(ProjectPresentationDto dto, PDPageContentStream contentStream,
                            Map<String, PDType0Font> fonts) throws IOException {
        spaceLinesDraw(contentStream);
        contentStream.setFont(fonts.get(FONT_BOLD), 14);
        spaceLinesDraw(contentStream);
        contentStream.showText("Статистика:");
        contentStream.setFont(fonts.get(FONT_REGULAR), 12);
        spaceLinesDraw(contentStream);
        contentStream.showText("Всего выполненных заданий: " + dto.completedTasks().size());
        spaceLinesDraw(contentStream);
        contentStream.showText("Всего участников проекта: " + dto.teams().stream().mapToInt(List::size).sum());
    }

    private void headerDraw(ProjectPresentationDto dto, PDPageContentStream contentStream,
                            Map<String, PDType0Font> fonts) throws IOException {
        contentStream.setLeading(14.5f);
        contentStream.beginText();
        contentStream.setFont(fonts.get(FONT_BOLD), 18);
        contentStream.newLineAtOffset(50, 750);
        contentStream.showText("Презентация проекта");
        spaceLinesDraw(contentStream, 2);
        spaceLinesDraw(contentStream);
        contentStream.setFont(fonts.get(FONT_REGULAR), 12);

        Map<String, String> projectDetails = getMapHeader(dto);
        for (Map.Entry<String, String> entry : projectDetails.entrySet()) {
            contentStream.showText(entry.getKey() + ": " + entry.getValue());
            contentStream.newLine();
        }
    }

    private Map<String, String> getMapHeader(ProjectPresentationDto dto) {
        Map<String, String> projectDetails = new LinkedHashMap<>();
        projectDetails.put("Название проекта", dto.title());
        projectDetails.put("Описание проекта", dto.description());
        projectDetails.put("Дата создания",
                dto.createdDate().format(DateTimeFormatter.ofPattern(DATE_MASK_FORMAT)));
        projectDetails.put("Владелец проекта", dto.ownerName());
        projectDetails.put("Статус проекта", dto.status());
        return projectDetails;
    }
}
