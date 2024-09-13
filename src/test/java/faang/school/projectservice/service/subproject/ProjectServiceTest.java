package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.client.subproject.ProjectDto;
import faang.school.projectservice.mapper.subproject.SubProjectMapper;
import faang.school.projectservice.mapper.subproject.SubProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.subproject.filters.SubProjectFilter;
import faang.school.projectservice.service.subproject.filters.SubProjectNameFilter;
import faang.school.projectservice.service.subproject.filters.SubProjectStatusFilter;
import faang.school.projectservice.validator.subproject.ValidatorService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.stereotype.Component;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RequiredArgsConstructor
@Component
@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    //TODO create tests

    private List<SubProjectFilter> filters = initializeSubProjectFilters();

    @Spy
    private SubProjectMapper subProjectMapper = new SubProjectMapperImpl();
    @Mock
    private ValidatorService validatorService;
    @Mock
    private ProjectRepository projectRepository;

    @Captor
    ArgumentCaptor<Project> projectCaptor;

    @InjectMocks
    ProjectService projectService;

    Long parentProjectId;
    private Project parentProject;
    Project firstProject;
    Project secondProject;
    ProjectDto projectDto;

    @BeforeEach
    void setUp() {
        parentProjectId = 10L;
        parentProject = new Project();
        parentProject.setId(parentProjectId);
        firstProject = new Project();
        secondProject = new Project();
        firstProject.setParentProject(parentProject);
        secondProject.setParentProject(parentProject);
        projectDto = new ProjectDto();
        projectDto.setId(parentProjectId);
    }


    @Test
    void testGetFilteredNameSubProjects() {
        firstProject.setName("Name1");
        secondProject.setName("Project2");
        projectDto.setName("name");

        ReflectionTestUtils.setField(projectService, "filters", filters);

        when(projectRepository.findAll()).thenReturn(List.of(firstProject, secondProject));

        List<ProjectDto> resultDtos = projectService.getFilteredSubProjects(projectDto);
        assertAll(
                () -> assertEquals(1, resultDtos.size()),
                () -> assertEquals(firstProject.getName(), resultDtos.get(0).getName())
        );
    }

    @Test
    void testGetFilteredStatusSubProjects() {
        firstProject.setStatus(ProjectStatus.CANCELLED);
        secondProject.setStatus(ProjectStatus.CREATED);
        projectDto.setStatus(ProjectStatus.CREATED);

        ReflectionTestUtils.setField(projectService, "filters", filters);

        when(projectRepository.findAll()).thenReturn(List.of(firstProject, secondProject));

        List<ProjectDto> resultDtos = projectService.getFilteredSubProjects(projectDto);
        assertAll(
                () -> assertEquals(1, resultDtos.size()),
                () -> assertEquals(secondProject.getStatus(), resultDtos.get(0).getStatus())
        );
    }

    @Test
    void testCreate() {
        Project project = new Project();
        project.setId(parentProjectId);
        project.setVisibility(ProjectVisibility.PUBLIC);
        ProjectDto projectDto = new ProjectDto();
        projectDto.setParentProjectId(parentProjectId);
        projectDto.setVisibility(ProjectVisibility.PUBLIC);
        when(projectRepository.findById(projectDto.getParentProjectId())).thenReturn(project);

        projectService.create(projectDto);

        verify(projectRepository, times(1)).save(projectCaptor.capture());
        Project savedProject = projectCaptor.getValue();
        assertAll(
                () -> assertEquals(projectDto.getVisibility(), savedProject.getVisibility()),
                () -> assertEquals(projectDto.getParentProjectId(), savedProject.getParentProject().getId())
        );
    }

    @Test
    void testGetFilteredSubProjectsEmptyList() {
        firstProject.setStatus(ProjectStatus.CANCELLED);
        secondProject.setStatus(ProjectStatus.IN_PROGRESS);

        ProjectDto projectDto = new ProjectDto();
        projectDto.setStatus(ProjectStatus.CREATED);
        projectDto.setParentProjectId(parentProjectId);

        when(projectRepository.findAll()).thenReturn(List.of(firstProject, secondProject));

        assertEquals(0, projectService.getFilteredSubProjects(projectDto).size());
    }

    @Test
    void testUpdateSubProject() {
    }

    List<SubProjectFilter> initializeSubProjectFilters() {
        return List.of(
                new SubProjectNameFilter(),
                new SubProjectStatusFilter()
        );
    }

}