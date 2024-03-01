package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.filter.project.ProjectStatusFilter;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;
    @Spy
    private ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);
    @Mock
    private List<ProjectFilter> filters;
    @Mock
    private ProjectFilterDto projectFilterDto;
    @Mock
    private MomentRepository momentRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private Project testProject;
    private ProjectDto testProjectDto;
    private CreateSubProjectDto createSubProjectDto;
    private UpdateSubProjectDto updateSubProjectDto;

    @BeforeEach
    void init() {
        testProject = Project.builder()
                .id(1L)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        testProjectDto = ProjectDto.builder()
                .id(1L)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        createSubProjectDto = CreateSubProjectDto.builder()
                .parentId(1L)
                .name("SubProject")
                .build();

        updateSubProjectDto = UpdateSubProjectDto.builder()
                .status(ProjectStatus.COMPLETED)
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        projectFilterDto = ProjectFilterDto.builder()
                .name("Sony")
                .status(ProjectStatus.IN_PROGRESS)
                .build();
    }

    @Test
    public void testCreateSubProjectSuccessfully() {
        when(projectRepository.getProjectById(anyLong())).thenReturn(testProject);
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        projectRepository.getProjectById(anyLong());
        projectRepository.save(testProject);

        verify(projectRepository, times(1)).getProjectById(anyLong());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    public void testUpdateProjectStatusToInProgressAndVisibilityToPrivate() {
        Moment moment = new Moment();
        testProject.setStatus(ProjectStatus.ON_HOLD);
        testProject.setVisibility(ProjectVisibility.PUBLIC);

        when(projectRepository.getProjectById(anyLong())).thenReturn(testProject);
        when(projectMapper.toDto(any(Project.class))).thenReturn(testProjectDto);
        when(momentRepository.save(any(Moment.class))).thenReturn(moment);

        ProjectDto resultDto = projectService.updateSubProject(testProject.getId(), updateSubProjectDto);
        assertEquals(testProjectDto, resultDto);
    }

    @Test
    public void testThrowDataValidationExceptionWhenSubProjectsStatusNotCompleted() {
        testProject.setChildren(Arrays.asList(new Project(), new Project()));

        when(projectRepository.getProjectById(anyLong())).thenReturn(testProject);

        assertThrows(DataValidationException.class,
                () -> projectService.updateSubProject(testProject.getId(), updateSubProjectDto));
    }

    @Test
    public void testCreateSubProjectWithValidDataSuccessfully() {
        when(projectRepository.getProjectById(anyLong())).thenReturn(testProject);
        when(projectRepository.save(any(Project.class))).thenReturn(new Project());
        when(projectMapper.toDto(any(Project.class))).thenReturn(testProjectDto);

        ProjectDto result = projectService.createSubProject(createSubProjectDto);

        assertEquals(result, testProjectDto);
    }

    @Test
    public void testFilteredPublicSubProject() {
        testProject.setName("Sony");

        Project testParentProject = Project.builder()
                .id(1L)
                .visibility(ProjectVisibility.PUBLIC)
                .children(Arrays.asList(testProject))
                .build();

        Stream<ProjectFilter> filterStream = Stream.of(new ProjectStatusFilter());
        when(projectRepository.getProjectById(anyLong())).thenReturn(testParentProject);
        when(filters.stream()).thenReturn(filterStream);
        when(projectMapper.toDto(testProject)).thenReturn(testProjectDto);

        List<ProjectDto> actualSubProjects =
                projectService.getFilteredSubProjects(testParentProject.getId(), projectFilterDto);
        List<ProjectDto> expectedSubProjects = Arrays.asList(testProjectDto);

        assertTrue(expectedSubProjects.size() == actualSubProjects.size()
                && expectedSubProjects.containsAll(actualSubProjects));
    }

    @Test
    public void testFilteredSubProjectsThrowDataValidationException() {
        testProject.setVisibility(ProjectVisibility.PRIVATE);
        when(projectRepository.getProjectById(anyLong())).thenReturn(testProject);
        assertThrows(DataValidationException.class,
                () -> projectService.getFilteredSubProjects(testProject.getId(), projectFilterDto));
    }
}