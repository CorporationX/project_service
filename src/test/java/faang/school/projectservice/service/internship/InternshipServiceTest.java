package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.mapper.internship.InternshipDtoMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;

import static faang.school.projectservice.model.InternshipStatus.IN_PROGRESS;
import static faang.school.projectservice.model.TeamRole.OWNER;

@ExtendWith(MockitoExtension.class)
class InternshipServiceTest {

    @InjectMocks
    private InternshipService internshipService;

    @Mock
    private InternshipRepository internshipRepository;

    @Mock
    private InternshipDtoMapper internshipDtoMapper;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Test
    public void testProjectFound_Success() {
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

        Mockito.when(projectRepository.findByIdOrThrow(1L)).thenReturn(new Project());
        internshipService.createInternship(dto);
    }


}