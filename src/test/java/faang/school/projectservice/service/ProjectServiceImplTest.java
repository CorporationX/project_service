package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.OpenVacancyRequestDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Test
    public void shouldValidateAndGetProject_throw_whenProjectByIdIdIsNotPresented() {
        var projectId = 0L;
        var requestDto = createOpenVacancyRequestDto(projectId, 1);
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> projectService.validateAndGetProject(requestDto));
    }

    @Test
    public void shouldValidateAndGetProject_returnsProjectEntity_whenProjectByIdIdIsPresented() {
        var projectId = 10L;
        var requestDto = createOpenVacancyRequestDto(projectId, 1);
        var expectedResult = Project.builder().id(projectId).name("Test project").build();
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(expectedResult));

        var result = projectService.validateAndGetProject(requestDto);

        assertEquals(expectedResult, result);
    }

    private static OpenVacancyRequestDto createOpenVacancyRequestDto(long projectId, long authorId) {
        return OpenVacancyRequestDto.builder()
                .name("Test name")
                .description("Test description")
                .projectId(projectId)
                .position(TeamRole.ANALYST)
                .requiredCandidatesCount(1)
                .authorId(authorId)
                .build();
    }
}