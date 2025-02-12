package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PdfReportGeneratorServiceTest {
    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private PdfReportGeneratorService pdfReportGeneratorService;

    @Test
    void testGenerateProjectReport() throws Exception {

        UserDto dummyUser = new UserDto(1L,"Oleg","king@gmail.com");
        when(userServiceClient.getUser(1L)).thenReturn(dummyUser);

        Project project = Project.builder()
                .id(1L)
                .name("Test Project")
                .description("This is a test project")
                .createdAt(LocalDateTime.now())
                .status(ProjectStatus.CREATED)
                .ownerId(1L)
                .teams(Collections.emptyList())
                .tasks(Collections.emptyList())
                .build();


        Resource pdfResource = pdfReportGeneratorService.generateProjectReport(project);
        assertNotNull(pdfResource, "PDF resource should not be null");


        try (PDDocument document = PDDocument.load(pdfResource.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String pdfText = stripper.getText(document);

            assertTrue(pdfText.contains("Test Project"), "PDF should contain project name");

        }
    }
}
