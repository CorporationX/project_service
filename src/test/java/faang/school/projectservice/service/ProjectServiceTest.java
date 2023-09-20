package faang.school.projectservice.service;


import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @InjectMocks
    private ProjectService projectService;
    @Mock
    private ProjectRepository projectRepository;

    @Spy
    private ProjectMapperImpl projectMapper;

    @Mock
    private ProjectValidator projectValidator;
    ProjectDto projectDto;
    Project project;

    @BeforeEach
    void setUp() {
        projectDto = ProjectDto.builder()
                .name("Project1")
                .description("Description1")
                .ownerId(1L)
                .visibility("PUBLIC")
                .build();

        project = Project.builder()
                .id(1L)
                .name("Project1")
                .description("Description1")
                .ownerId(1L)
                .visibility(ProjectVisibility.valueOf("PUBLIC"))
                .build();

        ProjectFilter projectFilter = Mockito.mock(ProjectFilter.class);
        List<ProjectFilter> projectFilters = List.of(projectFilter);
    }

    @Test
    void testCreateNewProject() {
        projectService.createProject(projectDto);
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void testGetProjectById() {
        when(projectRepository.getProjectById(1L)).thenReturn(project);
        projectService.findProjectById(1L);
        verify(projectRepository).getProjectById(1L);
    }

    @Test
    void testGetAllProject() {
        projectService.getAllProject();
        verify(projectRepository).findAll();
    }
}