package faang.school.projectservice.service.presentation;


import faang.school.projectservice.dto.project.ProjectPresentationDto;
import faang.school.projectservice.dto.project.ProjectTeamMemberDto;
import faang.school.projectservice.model.TeamRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class PresentationServiceImplTest {

    @InjectMocks
    private PresentationServiceImpl presentationService;

    @Test
    void testCreateProjectPresentationShouldReturnNonEmptyInputStream() throws IOException {
        ProjectPresentationDto dto = new ProjectPresentationDto(
                "Super Project!",
                LocalDateTime.now(),
                "Tom",
                "ACTIVE",
                "Description",
                List.of("Task 1", "Task 2"),
                List.of(List.of(new ProjectTeamMemberDto("Alice", Arrays.asList(TeamRole.ANALYST))))
        );

        try (InputStream pdfStream = presentationService.createProjectPresentation(dto)) {
            assertThat(pdfStream).isNotNull();
            assertThat(pdfStream.readAllBytes().length).isGreaterThan(0);
        }
    }
}
