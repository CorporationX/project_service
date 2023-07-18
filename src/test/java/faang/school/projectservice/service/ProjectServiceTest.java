package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ProjectMapperImpl;
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

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @InjectMocks
    private ProjectService projectService;
    @Spy
    private ProjectMapper mockProjectMapper = new ProjectMapperImpl();
    @Mock
    private ProjectRepository projectRepository;

    ProjectDto projectDto;

    @BeforeEach
    void setUp() {
        projectDto = ProjectDto.builder()
                .name("Project")
                .description("new Project")
                .ownerId(1L)
                .build();
    }

    @Test
    void testCreateProject() {
        Mockito.when(projectRepository
                .existsByOwnerUserIdAndName(Mockito.anyLong(), Mockito.anyString())).thenReturn(false);
        Assertions.assertEquals(ProjectStatus.CREATED, projectService.create(projectDto).getStatus());
        Mockito.verify(projectRepository).save(any());
    }

    @Test
    void testCreateProjectThrowsException() {
        Mockito.when(projectRepository
                .existsByOwnerUserIdAndName(Mockito.anyLong(), Mockito.anyString())).thenReturn(true);
        DataValidationException dataValidationException = Assertions
                .assertThrows(DataValidationException.class, () -> projectService.create(projectDto));
        Assertions.assertEquals(String
                .format("Project %s already exist",projectDto.getName()),dataValidationException.getMessage());
    }
}