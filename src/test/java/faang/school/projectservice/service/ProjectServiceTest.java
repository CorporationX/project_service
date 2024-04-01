package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectDtoFilter;
import faang.school.projectservice.handler.exceptions.EntityNotFoundException;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.project.filter.ProjectNameFilter;
import faang.school.projectservice.service.project.filter.ProjectStatusFilter;
import faang.school.projectservice.validation.project.ProjectValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    private ProjectMapper projectMapper;
    private ProjectService projectService;
    private ProjectValidation projectValidation;
    private ProjectRepository projectRepository;
    private ProjectJpaRepository projectJpaRepository;
    private ProjectNameFilter projectNameFilter;
    private ProjectStatusFilter projectStatusFilter;

    @BeforeEach
    void setUp() {
        projectValidation = mock(ProjectValidation.class);
        projectRepository = mock(ProjectRepository.class);
        projectJpaRepository = mock(ProjectJpaRepository.class);
        projectNameFilter = mock(ProjectNameFilter.class);
        projectStatusFilter = mock(ProjectStatusFilter.class);
        projectMapper = mock(ProjectMapper.class);
        projectService = new ProjectService(projectValidation, projectRepository, projectMapper, projectJpaRepository, projectNameFilter, projectStatusFilter);
    }

    @Test
    void testCreateProjectSavingRepository() {
        ProjectDto projectDto = new ProjectDto();
        Project project = new Project();

        when(projectMapper.toEntity(projectDto)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectDto);

        assertDoesNotThrow(() -> projectService.createProject(projectDto));

        verify(projectValidation, times(1)).validationCreate(projectDto);
        verify(projectMapper, times(1)).toEntity(projectDto);
        verify(projectRepository, times(1)).save(project);
        verify(projectMapper, times(1)).toDto(project);
    }

    @Test
    void testUpdateProjectSavingChanges() {
        ProjectDto projectDto = new ProjectDto();
        Project project = new Project();

        when(projectMapper.toEntity(projectDto)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectDto);

        assertDoesNotThrow(() -> projectService.updateProject(projectDto));

        verify(projectValidation, times(1)).validationUpdate(projectDto);
        verify(projectMapper, times(1)).toEntity(projectDto);
        verify(projectRepository, times(1)).save(project);
        verify(projectMapper, times(1)).toDto(project);
    }

    @Test
    void testFindProjectByFiltersReturnsFilteredListDto() {
        Long userId = 1L;
        ProjectDtoFilter projectDtoFilter = getDtoFilter();
        List<Project> projects = getProjects();
        List<Project> projectsCopy = new ArrayList<>(getProjects());

        when(projectRepository.findAll()).thenReturn(projects);
        when(projectNameFilter.isApplicable(projectDtoFilter)).thenReturn(true);
        when(projectStatusFilter.isApplicable(projectDtoFilter)).thenReturn(true);
        projects.removeIf(project -> Boolean.FALSE);

        assertDoesNotThrow(() -> projectService.findProjectByFilters(userId, projectDtoFilter));
        assertEquals(projects, projectsCopy);

        verify(projectRepository, times(1)).findAll();
        verify(projectNameFilter, times(1)).isApplicable(projectDtoFilter);
        verify(projectNameFilter, times(1)).apply(anyList(), any(ProjectDtoFilter.class));
        verify(projectStatusFilter, times(1)).isApplicable(projectDtoFilter);
        verify(projectStatusFilter, times(1)).apply(anyList(), any(ProjectDtoFilter.class));
    }

    @Test
    void testFindAllProjects_ReturnsListOfProjectDto() {
        List<Project> projects = List.of(new Project(), new Project());
        List<ProjectDto> projectDtoList = List.of(new ProjectDto(), new ProjectDto());

        when(projectRepository.findAll()).thenReturn(projects);
        when(projectMapper.toDto(projects)).thenReturn(projectDtoList);

        assertDoesNotThrow(() -> projectService.findAllProjects());

        verify(projectRepository, times(1)).findAll();
        verify(projectMapper, times(1)).toDto(projects);
    }

    @Test
    void testFindProjectByIdThrowExceptionNotFound() {
        Long projectId = 1L;

        when(projectJpaRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> projectService.findProjectById(projectId));

        verify(projectJpaRepository, times(1)).findById(projectId);
    }

    @Test
    void testFindProjectByIdReturnProject() {
        Long projectId = 1L;
        Project project = Project.builder().id(1L).build();
        ProjectDto projectDto = ProjectDto.builder().id(1L).build();

        when(projectJpaRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectMapper.toDto(project)).thenReturn(projectDto);

        assertDoesNotThrow(() -> projectService.findProjectById(projectId));

        verify(projectJpaRepository, times(1)).findById(projectId);
        verify(projectMapper, times(1)).toDto(project);
    }

    private List<Project> getProjects() {
        return new ArrayList<>(List.of(
                Project.builder().id(1L).name("Бизнес Фича").description("Проект ДОМ.РФ")
                        .status(ProjectStatus.CREATED).visibility(ProjectVisibility.PUBLIC).build(),
                Project.builder().id(2L).name("Аналитика Погоды").description("Проект ДепТранс'а")
                        .status(ProjectStatus.IN_PROGRESS).visibility(ProjectVisibility.PUBLIC).build()
        ));
    }

    private ProjectDtoFilter getDtoFilter() {
        return ProjectDtoFilter.builder().titlePattern("Погоды").statusPattern(ProjectStatus.IN_PROGRESS).build();
    }
}
