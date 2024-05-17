package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private ProjectValidator projectValidator;

    private ProjectDto projectDtoFirst;
    private Project project;
    private ProjectDto projectDtoSecond;

    @BeforeEach
    public void setUp() {
        projectDtoFirst = new ProjectDto();
        project = Project.builder().status(ProjectStatus.CREATED).build();
        projectDtoSecond = ProjectDto.builder().ownerId(1L).name("Name").description("Description").build();
    }

    @Test
    public void testCreate_IsRunSave() {
        when(projectMapper.toEntity(projectDtoFirst)).thenReturn(project);
        projectService.create(projectDtoFirst);
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    public void testUpdate_IsRunSave() {
        when(projectMapper.toEntity(projectDtoFirst)).thenReturn(project);
        projectService.create(projectDtoFirst);
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    public void testGetAllProjects() {
        projectService.getAllProjects();
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    public void testGetProjectById() {
        projectService.getProjectById(1L);
        verify(projectRepository, times(1)).getProjectById(1L);
    }
}