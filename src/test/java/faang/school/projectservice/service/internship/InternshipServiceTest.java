package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Collections;

import static faang.school.projectservice.model.InternshipStatus.IN_PROGRESS;
import static faang.school.projectservice.model.TeamRole.OWNER;

class InternshipServiceTest {

    @InjectMocks
    private InternshipService internshipService;

    @Mock
    ProjectRepository projectRepository;

    @Test
    void testProjectFound_Success() {
        CreateInternshipDto dto = new CreateInternshipDto(
                "Yandex",
                "I'm gay",
                IN_PROGRESS,
                OWNER,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                1L,
                2L,
                Collections.singletonList(3L)
        );

    }
}