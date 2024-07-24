package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.mapper.SubProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.project.ProjectServiceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
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
    private Project parentProject = Project.builder()
            .id(parentProjectId)
            .visibility(ProjectVisibility.PUBLIC)
            .children(new ArrayList<>())
            .build();
    private CreateSubProjectDto subProjectDto = new CreateSubProjectDto();

    {
        subProjectDto.setParentProjectId(parentProjectId);
        subProjectDto.setName("SubProject name");
        subProjectDto.setDescription("SubProject description");
        subProjectDto.setVisibility(ProjectVisibility.PUBLIC);
    }

    @BeforeEach
    void setUp() {
        when(validator.getParentAfterValidateSubProject(subProjectDto)).thenReturn(parentProject);
    }

    @Test
    public void testCreateSubProject() {
        ProjectDto result = projectService.createSubProject(subProjectDto);

        ProjectDto expected = new ProjectDto();
        expected.setParentProjectId(parentProjectId);
        expected.setName("SubProject name");
        expected.setDescription("SubProject description");
        expected.setVisibility(ProjectVisibility.PUBLIC);
        expected.setStatus(ProjectStatus.CREATED);

        assertEquals(expected, result);
        verify(projectRepository, times(2)).save(any());

        assertEquals(parentProject.getChildren().size(), 1);
        assertEquals(parentProject.getChildren().get(0).getName(), subProjectDto.getName());
    }
}