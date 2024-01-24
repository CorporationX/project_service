package faang.school.projectservice.serviсe;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.dto.ProjectUpDateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.filter.ProjectNameFilter;
import faang.school.projectservice.filter.ProjectStatusFilter;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static faang.school.projectservice.model.ProjectStatus.CREATED;
import static faang.school.projectservice.model.ProjectVisibility.PUBLIC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;
    @Spy
    private ProjectMapperImpl projectMapper;
    @Mock
    private List<ProjectFilter> projectFilters;
    @Mock
    private UserContext userContext;
    @Mock
    private ProjectValidator projectValidator;
    @InjectMocks
    private ProjectService projectService;
    @Captor
    private ArgumentCaptor<Project> captor; // класс из мокито
    private Project project1;

    private List<Project> projects;
    private List<ProjectDto> projectDtos;
    private ProjectDto projectDto;
    private ProjectUpDateDto projectUpDateDto;
    private Long id;

    @BeforeEach
    void setUp() {
        id = 1L;
        project1 = Project.builder()
                .id(id)
                .status(CREATED)
                .visibility(PUBLIC)
                .build();
        projectDto = projectMapper.toDto(project1);
        projects = List.of(project1);
        projectDtos = List.of(projectDto);
        projectUpDateDto = ProjectUpDateDto.builder()
                .status(ProjectStatus.COMPLETED)
                .build();
    }

    @Test
    void testCreateProjectWithExistsByOwnerUserIdAndName() {
        whenExistByOwnerIdAndName(true);
        assertThrows(DataValidationException.class, () -> {
            projectService.createProject(projectDto);
        });
    }

    @Test
    void testCreateProjectSavesProject() {
        whenExistByOwnerIdAndName(false);
        projectService.createProject(projectDto);
        verify(projectRepository, times(1)).save(captor.capture());
        Project project = captor.getValue();
        assertEquals(projectDto.getName(), project.getName());
    }

    @Test
    void testUpdateProjectSavesProject() {
        whenGetProjectById();
        projectService.updateProject(id, projectUpDateDto);
        verify(projectRepository, times(1)).save(captor.capture());
    }

    @Test
    void testUpdateProject_ShouldReturnUpdateProjectDto() {
        whenGetProjectById();
        assertEquals(projectDto, projectService.updateProject(id, projectUpDateDto));
    }

    @Test
    void testGetAllProjectsWithFilter() {
        ProjectFilterDto projectFilterDto = ProjectFilterDto.builder().status(CREATED).build();
        Stream<ProjectFilter> projectFiltersStream = Stream.of(new ProjectNameFilter(), new ProjectStatusFilter());
        when(projectRepository.findAll()).thenReturn(projects);
        whenValidateServiceGetProject();
        when(projectFilters.stream()).thenReturn(projectFiltersStream);
        assertEquals(projectDtos, projectService.getAllProjectsWithFilter(projectFilterDto));
    }


    @Test
    void testGetAllProjects_ShouldReturnAllProjects() {
        when(projectRepository.findAll()).thenReturn(projects);
        whenValidateServiceGetProject();
        assertEquals(projectDtos, projectService.getAllProjects());
    }

    @Test
    void testGetProjectById() {
        whenGetProjectById();
        whenValidateServiceGetProject();
        assertEquals(projectDto, projectService.getProjectById(id));
    }

    @Test
    void testDeleteProjectById() {
        projectService.deleteProjectById(1L);
        verify(projectRepository, times(1)).deleteById(id);
        verify(projectRepository, times(1)).getProjectById(id);
    }

    private void whenExistByOwnerIdAndName(boolean t) {
        when(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName()))
                .thenReturn(t);
    }

    private void whenValidateServiceGetProject() {
        when(projectValidator.validateServiceGetProject(userContext.getUserId(), project1))
                .thenReturn(project1);
    }

    private void whenGetProjectById() {
        when(projectRepository.getProjectById(id)).thenReturn(project1);
    }
}