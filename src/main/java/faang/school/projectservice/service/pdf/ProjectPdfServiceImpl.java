package faang.school.projectservice.service.pdf;


import faang.school.projectservice.dto.project.ProjectPresentationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectPdfServiceImpl implements  ProjectPdfService{

    @Override
    public InputStream createProjectPresentation(ProjectPresentationDto dto) {
        return null;
    }

//    public InputStream generateProjectPresentation(ProjectPresentationDto dto) {
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//
////        PdfWriter pdfWriter = new PdfWriter(outputStream);
////        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
////        pdfDocument.setDefaultPageSize(PageSize.A4);
////        Document document = new Document(pdfDocument);
////
////        createHeader(document, dto);
////        document.add(addDivider());
////        createDescription(document, dto.getDescription());
////        document.add(addDivider());
////        createAchievements(document, dto.getCompletedTasks());
////        document.add(addDivider());
////        createTeamProject(document, dto.getTeams());
////
////        document.close();
//        return new ByteArrayInputStream(outputStream.toByteArray());
//    }

//
//    private void createHeader(Document document, ProjectPresentationDto dto) {
//        float firstColumnSize = 435;
//        float secondColumnSize = 285f;
//        Table table = createTableWithFullWidth(firstColumnSize, secondColumnSize);
//        table.addCell(createCellWithoutBorder(dto.getTitle()).setBold().setFontSize(24f));
//        String createdDateFormated = dto.getCreatedDate()
//                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
//        String participantsCount = String.valueOf(dto.getTeams().stream()
//                .mapToInt(java.util.List::size)
//                .sum());
//        String completedTasksCount = String.valueOf(dto.getCompletedTasks().size());
//
//        Table nestedTable = createTableWithFullWidth(secondColumnSize / 2, secondColumnSize / 2);
//        createRowForHeader(nestedTable, "Created Date", createdDateFormated);
//        createRowForHeader(nestedTable, "Owner", dto.getOwnerName());
//        createRowForHeader(nestedTable, "Status", dto.getStatus());
//        createRowForHeader(nestedTable, "Participants count", participantsCount);
//        createRowForHeader(nestedTable, "Completed tasks", completedTasksCount);
//        table.addCell(new Cell().add(nestedTable).setBorder(Border.NO_BORDER));
//        document.add(table);
//    }
//
//    private void createDescription(Document document, String description) {
//        Paragraph descriptionTitle = createTitle("Description");
//        Paragraph descriptionContent = new Paragraph(description);
//        document.add(descriptionTitle);
//        document.add(descriptionContent);
//    }
//
//    private void createAchievements(Document document, java.util.List<String> achievements) {
//        Paragraph achievementTitle = createTitle("Achievements");
//        List achievementsList = new List()
//                .setSymbolIndent(12)
//                .setListSymbol("•");
//        achievements.forEach(achievementsList::add);
//        document.add(achievementTitle);
//        document.add(achievementsList);
//    }
//
//    private void createTeamProject(
//            Document document,
//            java.util.List<java.util.List<TeamMemberDto>> teams
//    ) {
//        Paragraph teamTitle = new Paragraph("Team project")
//                .setBold().setFontSize(20f).setMarginBottom(10f);
//
//        DeviceRgb lightGray = new DeviceRgb(245, 245, 245);
//        DeviceRgb white = new DeviceRgb(255, 255, 255);
//        DeviceRgb currentRowColor = lightGray;
//
//        Table table = createTableWithFullWidth(400, 200);
//
//        table.addCell(createLeftCellWithBorder("Name", white));
//        table.addCell(createRightCellWithBorder("Role", white));
//
//        for (var team : teams) {
//            for (var member : team) {
//                table.addCell(createLeftCellWithBorder(member.getName(), currentRowColor));
//                table.addCell(createRightCellWithBorder(
//                        member.getRoles().stream()
//                                .map(Enum::toString)
//                                .collect(Collectors.joining(", ")),
//                        currentRowColor
//                ));
//                currentRowColor = currentRowColor == lightGray ? white : lightGray;
//            }
//
//            table.addCell(createTableDivider(currentRowColor));
//            currentRowColor = currentRowColor == lightGray ? white : lightGray;
//        }
//
//        document.add(teamTitle);
//        document.add(table);
//    }
//
//    private Cell createTableDivider(Color backgroundColor) {
//        Border outerBorder = new SolidBorder(new DeviceRgb(236, 236, 236), 1f);
//        return new Cell(1, 2).add(new Paragraph("\n"))
//                .setBackgroundColor(backgroundColor)
//                .setBorder(outerBorder);
//    }
//
//    private Cell createLeftCellWithBorder(String value, Color backgroundColor) {
//        Border outerBorder = new SolidBorder(new DeviceRgb(236, 236, 236), 1f);
//        Border innerBorder = new SolidBorder(new DeviceRgb(255, 255, 255), 1f);
//
//        return new Cell().add(new Paragraph(value))
//                .setBorder(outerBorder)
//                .setBorderRight(innerBorder)
//                .setPaddingLeft(10f)
//                .setBackgroundColor(backgroundColor);
//    }
//
//    private Cell createRightCellWithBorder(String value, Color backgroundColor) {
//        Border outerBorder = new SolidBorder(new DeviceRgb(236, 236, 236), 1f);
//        Border innerBorder = new SolidBorder(new DeviceRgb(255, 255, 255), 1f);
//
//        return new Cell().add(new Paragraph(value))
//                .setBorder(outerBorder)
//                .setBorderLeft(innerBorder)
//                .setPaddingLeft(10f)
//                .setBackgroundColor(backgroundColor);
//    }
//
//    private Paragraph createTitle(String value) {
//        return new Paragraph(value)
//                .setBold().setFontSize(20f).setMarginBottom(10f);
//    }
//
//    private Table addDivider() {
//        Border border = new SolidBorder(new DeviceGray(0.5f), 1f);
//        Table divider = createTableWithFullWidth(1);
//        divider.setMarginTop(20f);
//        divider.setMarginBottom(20f);
//        divider.setBorder(border);
//        return divider;
//    }
//
//    private Cell createCellWithoutBorder(String text) {
//        return new Cell().add(new Paragraph(text)).setBorder(Border.NO_BORDER);
//    }
//
//    private Table createTableWithFullWidth(float... args) {
//        return new Table(args).useAllAvailableWidth();
//    }
//
//    private void createRowForHeader(Table table, String key, String value) {
//        table.addCell(createCellWithoutBorder(key).setBold());
//        table.addCell(createCellWithoutBorder(value));
//    }


}
