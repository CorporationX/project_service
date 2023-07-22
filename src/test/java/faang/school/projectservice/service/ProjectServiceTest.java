package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;
    @Spy
    private ProjectMapper projectMapper = new ProjectMapperImpl();
    @InjectMocks
    private ProjectService projectService;

    private Project desiredProject;
    private final long userId = 1L;

    @BeforeEach
    public void initProject() {
        desiredProject = Project.builder()
                .id(1L)
                .name("Project")
                .description("Cool")
                .ownerId(userId)
                .status(ProjectStatus.CREATED)
                .build();
    }

    @Test
    public void shouldCreatedAndReturnNewProject() {
        Mockito.when(projectRepository.existsByOwnerUserIdAndName(userId, "Project"))
                .thenReturn(false);

        ProjectDto desiredProjectDto = projectMapper.toDto(desiredProject);

        Mockito.lenient().when(projectRepository.save(desiredProject))
                .thenReturn(desiredProject);

        ProjectDto receivedProject = projectService.createProject(desiredProjectDto);

        Assertions.assertEquals(desiredProjectDto, receivedProject);
        Mockito.verify(projectRepository).save(desiredProject);
    }

    @Test
    public void shouldThrowExceptionWhenProjectExists() {
        ProjectDto testProjectDto = projectMapper.toDto(desiredProject);

        Mockito.when(projectRepository.existsByOwnerUserIdAndName(testProjectDto.getId(), testProjectDto.getName()))
                .thenReturn(true);

        Assertions.assertThrows(DataValidationException.class, () -> projectService.createProject(testProjectDto));
        Mockito.verify(projectRepository, Mockito.times(0)).save(desiredProject);
    }
}
