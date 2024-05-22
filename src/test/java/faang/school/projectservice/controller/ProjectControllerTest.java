package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProjectControllerTest {

    @InjectMocks
    private ProjectController projectController;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private ProjectValidator projectValidator;

    private ProjectDto projectDtoFirst;
    private ProjectDto projectDtoSecond;
    private ProjectFilterDto projectFilterDto;

    @BeforeEach
    public void setUp() {
        projectDtoFirst = new ProjectDto();
        projectDtoSecond = ProjectDto.builder().ownerId(1L).name("Name").description("Description").build();
        projectFilterDto = new ProjectFilterDto();
    }

    @Test
    public void testCreate_IsRunCreate() {
        projectController.create(projectDtoFirst);
        verify(projectService, times(1)).create(projectDtoFirst);
    }

    @Test
    public void testUpdate_IsRunUpdate() {
        projectController.update(projectDtoFirst);
        verify(projectService, times(1)).update(projectDtoFirst);
    }

    @Test
    public void testGetProjectByFilter_IsRunGetProjectByFilter() {
        projectController.getProjectsByFilter(projectFilterDto);
        verify(projectService, times(1)).getProjectsByFilter(projectFilterDto);
    }

    @Test
    public void testGetAllProjects_IsRunGetAllProjects() {
        projectController.getAllProjects();
        verify(projectService, times(1)).getAllProjects();
    }

    @Test
    public void testGetProjectById_IsRunGetProjectById() {
        projectController.getProjectById(1L);
        verify(projectService, times(1)).getProjectById(1L);
    }
}