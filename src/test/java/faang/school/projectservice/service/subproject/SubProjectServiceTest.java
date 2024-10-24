package faang.school.projectservice.service.subproject;

import faang.school.projectservice.model.dto.ProjectDto;
import faang.school.projectservice.mapper.subproject.SubProjectMapper;
import faang.school.projectservice.mapper.subproject.SubProjectMapperImpl;
import faang.school.projectservice.model.entity.Project;
import faang.school.projectservice.model.enums.ProjectStatus;
import faang.school.projectservice.model.enums.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.filter.SubProjectFilter;
import faang.school.projectservice.filter.impl.SubProjectNameFilter;
import faang.school.projectservice.filter.impl.SubProjectStatusFilter;
import faang.school.projectservice.service.impl.SubProjectServiceImpl;
import faang.school.projectservice.validator.ValidatorService;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RequiredArgsConstructor
@Component
@ExtendWith(MockitoExtension.class)
class SubProjectServiceTest {
    final List<SubProjectFilter> filters = initializeSubProjectFilters();

    @Spy
    private SubProjectMapper subProjectMapper = new SubProjectMapperImpl();
    @Mock
    private ValidatorService validatorService;
    @Mock
    private ProjectRepository projectRepository;

    @Captor
    private ArgumentCaptor<Project> projectCaptor;

    @InjectMocks
    private SubProjectServiceImpl subProjectService;

    private Long parentProjectId;
    private Project parentProject;
    private Project firstProject;
    private Project secondProject;
    private ProjectDto projectDto;

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

        ReflectionTestUtils.setField(subProjectService, "filters", filters);

        when(projectRepository.findAll()).thenReturn(List.of(firstProject, secondProject));

        List<ProjectDto> resultDtos = subProjectService.getFilteredSubProjects(projectDto);
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

        ReflectionTestUtils.setField(subProjectService, "filters", filters);

        when(projectRepository.findAll()).thenReturn(List.of(firstProject, secondProject));

        List<ProjectDto> resultDtos = subProjectService.getFilteredSubProjects(projectDto);
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

        subProjectService.create(projectDto);

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

        assertEquals(0, subProjectService.getFilteredSubProjects(projectDto).size());
    }

    @Test
    void testUpdatingSubProjectVisibility() {
        firstProject.setVisibility(ProjectVisibility.PRIVATE);
        secondProject.setVisibility(ProjectVisibility.PRIVATE);
        projectDto.setVisibility(ProjectVisibility.PRIVATE);
        parentProject.setVisibility(ProjectVisibility.PUBLIC);
        parentProject.setChildren(List.of(firstProject, secondProject));
        when(projectRepository.findById(parentProjectId)).thenReturn(parentProject);

        subProjectService.updatingSubProject(projectDto);

        verify(projectRepository, times(1)).save(projectCaptor.capture());
        Project savedProject = projectCaptor.getValue();
        assertEquals(projectDto.getVisibility(), savedProject.getVisibility());
    }

    @Test
    void testUpdatingSubProjectStatusUnSuccess() {
        firstProject.setStatus(ProjectStatus.COMPLETED);
        secondProject.setStatus(ProjectStatus.IN_PROGRESS);
        projectDto.setStatus(ProjectStatus.COMPLETED);
        parentProject.setStatus(ProjectStatus.IN_PROGRESS);
        parentProject.setVisibility(ProjectVisibility.PUBLIC);
        parentProject.setChildren(List.of(firstProject, secondProject));
        when(projectRepository.findById(parentProjectId)).thenReturn(parentProject);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> subProjectService.updatingSubProject(projectDto));

        assertEquals("Can't close project with id " + projectDto.getId() + ", because children project still open", exception.getMessage());
    }

    @Test
    void testUpdatingSubProjectStatusSuccess() {
        firstProject.setStatus(ProjectStatus.COMPLETED);
        secondProject.setStatus(ProjectStatus.COMPLETED);
        projectDto.setStatus(ProjectStatus.COMPLETED);
        parentProject.setStatus(ProjectStatus.IN_PROGRESS);
        parentProject.setVisibility(ProjectVisibility.PUBLIC);
        parentProject.setChildren(List.of(firstProject, secondProject));
        when(projectRepository.findById(parentProjectId)).thenReturn(parentProject);

        subProjectService.updatingSubProject(projectDto);

        verify(projectRepository, times(1)).save(projectCaptor.capture());
        Project savedProject = projectCaptor.getValue();
        assertEquals(projectDto.getStatus(), savedProject.getStatus());
    }

    List<SubProjectFilter> initializeSubProjectFilters() {
        return List.of(
                new SubProjectNameFilter(),
                new SubProjectStatusFilter()
        );
    }
}