package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.mapper.SubProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    @InjectMocks
    private ProjectService projectService;
    @Mock
    private ProjectServiceValidator validator;
    @Spy
    private SubProjectMapperImpl subProjectMapper;
    @Spy
    private ProjectMapperImpl projectMapper;
    @Mock
    private UserContext userContext;
    @Mock
    private ProjectRepository projectRepository;
    private Long parentProjectId = 1L;
    private Long ownerId = 100L;
    private String name = "SubProject name";
    private String description = "SubProject description";

    private Project parentProject = Project.builder()
            .id(parentProjectId)
            .build();
    private CreateSubProjectDto subProjectDto = CreateSubProjectDto.builder()
            .parentProjectId(parentProjectId)
            .name(name)
            .description(description)
            .build();
    private ProjectDto projectDto = ProjectDto.builder()
            .parentProjectId(parentProjectId)
            .name(name)
            .description(description)
            .status(ProjectStatus.CREATED)
            .build();

    @BeforeEach
    void setUp() {
        lenient().when(validator.getProjectAfterValidateId(parentProjectId)).thenReturn(parentProject);
    }

    @Test
    public void testCreateSubProject() {
        ProjectDto result = projectService.createSubProject(subProjectDto);

        assertEquals(projectDto, result);
        verify(projectRepository).save(any());
    }

    @Test
    public void testUpdateSubProject() {
        UpdateSubProjectDto updateDto = UpdateSubProjectDto.builder()
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        when(validator.getProjectAfterValidateId(parentProjectId)).thenReturn(parentProject);
        when(projectRepository.save(any())).thenReturn(parentProject);
        ProjectDto result = projectService.updateSubProject(parentProjectId, updateDto);

        assertEquals(ProjectStatus.IN_PROGRESS, result.getStatus());
    }
}