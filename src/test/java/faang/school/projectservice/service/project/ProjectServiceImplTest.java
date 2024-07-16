package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.exception.ExceptionMessages;
import faang.school.projectservice.filter.ProjectNameFilter;
import faang.school.projectservice.filter.ProjectOwnerFilter;
import faang.school.projectservice.filter.ProjectStatusFilter;
import faang.school.projectservice.filter.ProjectTeamMemberFilter;
import faang.school.projectservice.filter.PublicProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    private static final int APPROX_AMOUNT_OF_SECONDS_TO_ENSURE_THE_CHANGE_WAS_MADE_RECENTLY = 30;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper mapper;

    @Captor
    private ArgumentCaptor<Project> projectArgumentCaptor;

    @Captor
    private ArgumentCaptor<List<Project>> projectListArgumentCaptor;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private ProjectServiceImpl sut;

    @Test
    void createProject_throws_exception_if_project_already_exists() {
        var projectDto = ProjectDto.builder()
                .name("Test Project")
                .description("Test Project Description")
                .ownerId(1L)
                .build();

        when(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> sut.createProject(projectDto));

        assertEquals(ExceptionMessages.PROJECT_ALREADY_EXISTS_FOR_OWNER_ID, exception.getMessage());
    }

    @Test
    void createProject_throws_exception_if_database_constraints_fail() {
        var projectDto = ProjectDto.builder()
                .name("Test Project")
                .description("Test Project Description")
                .ownerId(1L)
                .build();

        var project = Project.builder()
                .name("Test Project")
                .description("Test Project Description")
                .ownerId(1L)
                .build();

        when(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())).thenReturn(false);
        when(mapper.toEntity(projectDto)).thenReturn(project);
        when(projectRepository.save(project)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(PersistenceException.class, () -> sut.createProject(projectDto));
    }

    @Test
    void createProject_persists_project_successfully_with_no_parent_project() {
        var projectDto = ProjectDto.builder()
                .name("Test Project")
                .description("Test Project Description")
                .ownerId(1L)
                .build();

        var project = Project.builder()
                .name("Test Project")
                .description("Test Project Description")
                .ownerId(1L)
                .visibility(ProjectVisibility.PUBLIC)
                .status(ProjectStatus.CREATED)
                .build();

        when(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())).thenReturn(false);
        when(mapper.toEntity(projectDto)).thenReturn(project);
        when(mapper.toDto(project)).thenReturn(projectDto);
        when(projectRepository.save(project)).thenReturn(project);

        ProjectDto createdProjectDto = sut.createProject(projectDto);

        verify(projectRepository, times(1)).save(projectArgumentCaptor.capture());
        assertEquals(projectDto, createdProjectDto);
        assertEquals(ProjectStatus.CREATED, projectArgumentCaptor.getValue().getStatus());
    }

    @Test
    void createProject_persists_project_successfully_with_parent_project() {
        var projectDto = ProjectDto.builder()
                .name("Test Project")
                .description("Test Project Description")
                .ownerId(1L)
                .parentProjectId(2L)
                .build();

        var parentProject = Project.builder()
                .id(2L)
                .name("Parent project")
                .build();

        var project = Project.builder()
                .name("Test Project")
                .description("Test Project Description")
                .ownerId(1L)
                .parentProject(parentProject)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        when(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())).thenReturn(false);
        when(mapper.toEntity(projectDto)).thenReturn(project);
        when(mapper.toDto(project)).thenReturn(projectDto);
        when(projectRepository.getProjectById(projectDto.getParentProjectId())).thenReturn(parentProject);
        when(projectRepository.save(project)).thenReturn(project);

        ProjectDto createdProjectDto = sut.createProject(projectDto);

        verify(projectRepository, times(1)).save(projectArgumentCaptor.capture());
        assertEquals(projectDto, createdProjectDto);
        assertEquals(ProjectStatus.CREATED, projectArgumentCaptor.getValue().getStatus());
        assertEquals(parentProject.getId(), createdProjectDto.getParentProjectId());
    }

    @Test
    void updateProject_updates_project_successfully() {
        var projectUpdateDto = ProjectUpdateDto.builder()
                .name("Updated Project")
                .description("Updated Project Description")
                .visibility(ProjectVisibility.PUBLIC)
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        var project = Project.builder()
                .id(1L)
                .name("Test Project")
                .description("Test Project Description")
                .visibility(ProjectVisibility.PRIVATE)
                .status(ProjectStatus.CREATED)
                .build();

        var updatedProject = Project.builder()
                .id(1L)
                .name("Updated Project")
                .description("Updated Project Description")
                .visibility(ProjectVisibility.PUBLIC)
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        when(projectRepository.getProjectById(1L)).thenReturn(project);
        when(mapper.update(any(ProjectUpdateDto.class), any(Project.class))).thenReturn(updatedProject);

        sut.updateProject(1L, projectUpdateDto);

        verify(mapper, times(1)).toDto(projectArgumentCaptor.capture());
        verify(projectRepository, times(1)).save(updatedProject);
        assertEquals(projectUpdateDto.getName(), projectArgumentCaptor.getValue().getName());
        assertEquals(projectUpdateDto.getDescription(), projectArgumentCaptor.getValue().getDescription());
        assertEquals(projectUpdateDto.getVisibility(), projectArgumentCaptor.getValue().getVisibility());
        assertEquals(projectUpdateDto.getStatus(), projectArgumentCaptor.getValue().getStatus());
        assertTrue(ChronoUnit.SECONDS.between(projectArgumentCaptor.getValue().getUpdatedAt(),
                LocalDateTime.now()) < APPROX_AMOUNT_OF_SECONDS_TO_ENSURE_THE_CHANGE_WAS_MADE_RECENTLY);
    }

    @Test
    void updateProject_should_throw_exception_when_project_not_found() {
        var projectUpdateDto = ProjectUpdateDto.builder()
                .name("Updated Project")
                .description("Updated Project Description")
                .visibility(ProjectVisibility.PUBLIC)
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        when(projectRepository.getProjectById(1L)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> sut.updateProject(1L, projectUpdateDto));
    }

    @Test
    void getProject_retrieves_project_successfully() {
        var project = Project.builder()
                .id(1L)
                .name("Test Project")
                .description("Test Project Description")
                .visibility(ProjectVisibility.PRIVATE)
                .status(ProjectStatus.CREATED)
                .build();

        var projectDto = ProjectDto.builder()
                .id(1L)
                .name("Test Project")
                .description("Test Project Description")
                .visibility(ProjectVisibility.PRIVATE)
                .status(ProjectStatus.CREATED)
                .build();

        when(projectRepository.getProjectById(1L)).thenReturn(project);
        when(mapper.toDto(project)).thenReturn(projectDto);

        ProjectDto retrievedProjectDto = sut.retrieveProject(1L);

        assertEquals(projectDto, retrievedProjectDto);
    }

    @Test
    void getProject_should_throw_exception_when_project_not_found() {
        when(projectRepository.getProjectById(1L)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> sut.retrieveProject(1L));
    }

    @Test
    void getAllProjects_retrieves_all_projects_successfully() {
        var project1 = Project.builder()
                .id(1L)
                .name("Test Project 1")
                .description("Test Project Description 1")
                .visibility(ProjectVisibility.PRIVATE)
                .status(ProjectStatus.CREATED)
                .build();

        var project2 = Project.builder()
                .id(2L)
                .name("Test Project 2")
                .description("Test Project Description 2")
                .visibility(ProjectVisibility.PUBLIC)
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        var projectDto1 = ProjectDto.builder()
                .id(1L)
                .name("Test Project 1")
                .description("Test Project Description 1")
                .visibility(ProjectVisibility.PRIVATE)
                .status(ProjectStatus.CREATED)
                .build();

        var projectDto2 = ProjectDto.builder()
                .id(2L)
                .name("Test Project 2")
                .description("Test Project Description 2")
                .visibility(ProjectVisibility.PUBLIC)
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        when(projectRepository.findAll()).thenReturn(List.of(project1, project2));
        when(mapper.toDto(List.of(project1, project2))).thenReturn(List.of(projectDto1, projectDto2));

        var retrievedProjects = sut.getAllProjects();

        assertEquals(2, retrievedProjects.size());
        assertEquals(projectDto1, retrievedProjects.get(0));
        assertEquals(projectDto2, retrievedProjects.get(1));
    }

    @Test
    void getAllProjects_returns_empty_list_when_no_projects_exist() {
        when(projectRepository.findAll()).thenReturn(List.of());

        var retrievedProjects = sut.getAllProjects();

        assertTrue(retrievedProjects.isEmpty());
    }

    @Test
    void getAllProjects_should_throw_exception_when_some_database_error_occurs() {
        when(projectRepository.findAll()).thenThrow(RuntimeException.class);

        assertThrows(PersistenceException.class, () -> sut.getAllProjects());
    }

    @Test
    void filterProjects_should_throw_exception_when_some_database_error_occurs() {
        when(projectRepository.findAll()).thenThrow(RuntimeException.class);

        assertThrows(PersistenceException.class, () -> sut.filterProjects(null));
    }

    @Test
    void filterProjects_with_filters_applied() {
        var projectFilterDto = new ProjectFilterDto("proj", null);

        when(userContext.getUserId()).thenReturn(1L);

        var projectNameFilter = Mockito.spy(new ProjectNameFilter());
        var projectStatusFilter = Mockito.spy(new ProjectStatusFilter());
        var publicProjectFilter = Mockito.spy(new PublicProjectFilter());
        var projectOwnerFilter = Mockito.spy(new ProjectOwnerFilter(userContext));
        var projectTeamMemberFilter = Mockito.spy(new ProjectTeamMemberFilter(userContext));

        ReflectionTestUtils.setField(sut, "defaultProjectFilters", List.of(publicProjectFilter, projectOwnerFilter, projectTeamMemberFilter));
        ReflectionTestUtils.setField(sut, "userDefinedProjectFilters", List.of(projectNameFilter, projectStatusFilter));

        var allProjects = List.of(
                Project.builder().id(1L).name("new project").visibility(ProjectVisibility.PUBLIC).build(),
                Project.builder().id(2L).name("the most awesome project").visibility(ProjectVisibility.PRIVATE).ownerId(1L).build(),
                Project.builder().id(22L).name("the most awesome").visibility(ProjectVisibility.PRIVATE).ownerId(1L).build(),
                Project.builder().id(3L).name("lol").visibility(ProjectVisibility.PRIVATE).teams(
                        List.of(Team.builder().teamMembers(
                                List.of(TeamMember.builder().userId(1L).build()))
                                .build()))
                        .build(),
                Project.builder().id(33L).name("project extra").visibility(ProjectVisibility.PRIVATE).teams(
                                List.of(Team.builder().teamMembers(
                                                List.of(TeamMember.builder().userId(1L).build()))
                                        .build()))
                        .build(),
                Project.builder().id(4L).name("null").visibility(ProjectVisibility.PRIVATE).build()
        );

        var expectedFilteredProjects = List.of(allProjects.get(0), allProjects.get(1), allProjects.get(4));

        when(projectRepository.findAll()).thenReturn(allProjects);

        sut.filterProjects(projectFilterDto);

        verify(mapper, times(1)).toDto(projectListArgumentCaptor.capture());

        assertEquals(expectedFilteredProjects, projectListArgumentCaptor.getValue());
    }
}