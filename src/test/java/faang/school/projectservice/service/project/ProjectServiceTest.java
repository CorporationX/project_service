package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ForbiddenAccessException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validation.ProjectDtoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    private final static long PRIVATE_PROJECT_ID = 1;
    private final static long TEAM_MEMBER_USER_ID = 2;

    @Mock
    private ProjectRepository projectRepository;

    @Spy
    private ProjectDtoValidator projectDtoValidator;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Spy
    private ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);

    @Mock
    private List<ProjectFilter> projectFilters;

    private Project privateProject;

    @BeforeEach
    public void setUp() {
        privateProject = Project.builder()
                .id(PRIVATE_PROJECT_ID)
                .name("name")
                .description("description")
                .status(ProjectStatus.CREATED)
                .ownerId(5L)
                .visibility(ProjectVisibility.PRIVATE)
                .build();
    }

    @Test
    @DisplayName("Получение приватного проекта по id не участником команды")
    public void testGetPrivateProjectByNotTeamMember() {
        addTeamsToProject(privateProject);
        when(projectRepository.getProjectById(PRIVATE_PROJECT_ID))
                .thenReturn(privateProject);
        assertThrows(ForbiddenAccessException.class,
                () -> projectService.findProjectById(PRIVATE_PROJECT_ID, 1L));
    }

    @Test
    @DisplayName("Получение приватного проекта по id участником команды")
    public void testGetPrivateProjectByTeamMember() {
        addTeamsToProject(privateProject);
        when(projectRepository.getProjectById(PRIVATE_PROJECT_ID))
                .thenReturn(privateProject);
        assertEquals(PRIVATE_PROJECT_ID,
                projectService.findProjectById(PRIVATE_PROJECT_ID, TEAM_MEMBER_USER_ID).getId());
        verify(projectMapper).toProjectDto(privateProject);

    }

    @Test
    @DisplayName("Создание проекта")
    public void testSuccessCreateProject() {
        ProjectDto projectDto = projectMapper.toProjectDto(privateProject);
        when(projectRepository.save(privateProject)).thenReturn(privateProject);
        when(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName()))
                .thenReturn(false);
        assertEquals(PRIVATE_PROJECT_ID, projectService.createProject(projectDto).getId());
        verify(projectRepository).save(privateProject);
        verify(projectMapper).toProject(projectDto);
    }

    @Test
    @DisplayName("Создание проекта с именем, который уже был создан пользователем")
    public void testCreateExistProjectByNameAndUserId() {
        ProjectDto projectDto = projectMapper.toProjectDto(privateProject);
        when(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName()))
                .thenReturn(true);
        assertThrows(DataValidationException.class,
                () -> projectService.createProject(projectDto));
        verify(projectRepository)
                .existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName());
    }

    @Test
    @DisplayName("Обновление проекта")
    public void testSuccessUpdateProject() {
        privateProject.setUpdatedAt(LocalDateTime.now());
        ProjectDto projectDto = projectMapper.toProjectDto(privateProject);
        projectDto.setStatus(ProjectStatus.CANCELLED);
        projectDto.setDescription("updated description");
        LocalDateTime oldDateTime = privateProject.getUpdatedAt();
        when(projectRepository.getProjectById(projectDto.getId())).thenReturn(privateProject);
        when(projectRepository.save(privateProject)).thenReturn(privateProject);
        ProjectDto result = projectService.updateProject(projectDto);

        assertEquals(result.getId(), privateProject.getId());
        assertEquals(result.getDescription(), privateProject.getDescription());
        assertEquals(result.getStatus(), privateProject.getStatus());
        assertNotEquals(oldDateTime, privateProject.getUpdatedAt());

        verify(projectRepository).save(privateProject);
        verify(projectRepository).getProjectById(projectDto.getId());
    }

    @Test
    @DisplayName("Обновление проекта без описания")
    public void testUpdateProjectWithoutDescription() {
        privateProject.setUpdatedAt(LocalDateTime.now());
        ProjectDto projectDto = projectMapper.toProjectDto(privateProject);
        projectDto.setStatus(ProjectStatus.CANCELLED);
        projectDto.setDescription(null);
        LocalDateTime oldDateTime = privateProject.getUpdatedAt();
        String oldDescription = privateProject.getDescription();
        when(projectRepository.getProjectById(projectDto.getId())).thenReturn(privateProject);
        when(projectRepository.save(privateProject)).thenReturn(privateProject);

        ProjectDto result = projectService.updateProject(projectDto);

        assertEquals(result.getId(), privateProject.getId());
        assertEquals(oldDescription, privateProject.getDescription());
        assertEquals(result.getStatus(), privateProject.getStatus());
        assertNotEquals(oldDateTime, privateProject.getUpdatedAt());

        verify(projectRepository).save(privateProject);
        verify(projectRepository).getProjectById(projectDto.getId());
    }

    @Test
    @DisplayName("Обновление проекта без статуса")
    public void testUpdateProjectWithoutStatus() {
        privateProject.setUpdatedAt(LocalDateTime.now());
        ProjectDto projectDto = projectMapper.toProjectDto(privateProject);
        projectDto.setStatus(null);
        projectDto.setDescription("updated description");
        LocalDateTime oldDateTime = privateProject.getUpdatedAt();
        ProjectStatus oldStatus = privateProject.getStatus();
        when(projectRepository.getProjectById(projectDto.getId())).thenReturn(privateProject);
        when(projectRepository.save(privateProject)).thenReturn(privateProject);
        ProjectDto result = projectService.updateProject(projectDto);

        assertEquals(result.getId(), privateProject.getId());
        assertEquals(result.getDescription(), privateProject.getDescription());
        assertEquals(oldStatus, privateProject.getStatus());
        assertNotEquals(oldDateTime, privateProject.getUpdatedAt());

        verify(projectRepository).save(privateProject);
        verify(projectRepository).getProjectById(projectDto.getId());
    }

    @Test
    @DisplayName("Получение всех проектов участником команды")
    public void testFindAllProjectsWithoutFiltersByTeamMember() {
        List<Project> projects = prepareProjects();
        when(projectRepository.findAll()).thenReturn(projects);
        List<ProjectDto> result = projectService.findAllProjects(null, TEAM_MEMBER_USER_ID);
        assertEquals(4, result.size());
    }

    @Test
    @DisplayName("Получение всех проектов не участником команды")
    public void testFindAllProjectsWithoutFiltersByNotTeamMember() {
        List<Project> projects = prepareProjects();
        when(projectRepository.findAll()).thenReturn(projects);
        List<ProjectDto> result = projectService.findAllProjects(null, 5L);
        assertEquals(2, result.size());
    }

    private List<Project> prepareProjects() {
        long idCounter = 0;
        Project project1 = Project.builder()
                .id(++idCounter)
                .name("PROJECT")
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        Project project2 = Project.builder()
                .id(++idCounter)
                .name("Project sss")
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        Project project3 = Project.builder()
                .name("name")
                .id(++idCounter)
                .status(ProjectStatus.CANCELLED)
                .visibility(ProjectVisibility.PRIVATE)
                .build();
        Project project4 = Project.builder()
                .name("PrOJEcT")
                .id(++idCounter)
                .visibility(ProjectVisibility.PRIVATE)
                .status(ProjectStatus.ON_HOLD)
                .build();
        List<Project> projects = List.of(project1, project2, project3, project4);
        projects.forEach(this::addTeamsToProject);
        return projects;
    }

    private void addTeamsToProject(Project project) {
        Team team = Team.builder()
                .id(1L)
                .project(project)
                .build();
        TeamMember teamMember = TeamMember.builder()
                .id(1L)
                .userId(TEAM_MEMBER_USER_ID)
                .team(team)
                .build();

        List<TeamMember> teamMembers = List.of(teamMember);
        team.setTeamMembers(teamMembers);
        List<Team> teams = List.of(team);
        project.setTeams(teams);
    }
}
