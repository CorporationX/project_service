package faang.school.project_service.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.filter.project.ProjectNameFilter;
import faang.school.projectservice.filter.project.ProjectStatusFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.ProjectServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceImplTest {

    private final ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);

    private final TeamMember teamMember = TeamMember.builder().id(234L).build();

    private final Team teamMemberTeam = Team.builder().teamMembers(new ArrayList<>(List.of(teamMember))).build();
    private final Team team = Team.builder()
            .teamMembers(new ArrayList<>(List.of(TeamMember.builder().id(teamMember.getId() + 33).build())))
            .build();

    private final Project publicProjectNotTeamMember = Project.builder()
            .id(1L)
            .teams(new ArrayList<>(List.of(team)))
            .visibility(ProjectVisibility.PUBLIC)
            .build();
    private final Project teamMemeberPrivetProject = Project.builder()
            .id(12L)
            .teams(new ArrayList<>(List.of(teamMemberTeam)))
            .visibility(ProjectVisibility.PRIVATE)
            .build();
    private final Project privateProjectNotTeamMember = Project.builder()
            .id(345L)
            .teams(new ArrayList<>(List.of(team)))
            .visibility(ProjectVisibility.PRIVATE)
            .build();

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserContext userContext;
    @Captor
    private ArgumentCaptor<Project> projectArgumentCaptor;

    private ProjectServiceImpl projectService;

    @BeforeEach
    void setup() {
        projectService = new ProjectServiceImpl(projectRepository, userContext, projectMapper,
                List.of(new ProjectNameFilter(), new ProjectStatusFilter()));
    }

    @Test
    void testCreateThrowsExceptionIfUserAlreadyHasSameNameProject() {
        long ownerId = 1L;
        CreateProjectDto createProjectDto = CreateProjectDto.builder()
                .name("project name")
                .build();

        when(userContext.getUserId()).thenReturn(ownerId);
        when(projectRepository.existsByOwnerIdAndName(ownerId, createProjectDto.name()))
                .thenReturn(true);

        DataValidationException dataValidationException = Assertions.assertThrows(DataValidationException.class,
                () -> projectService.create(createProjectDto));
        assertEquals("Rejected to create project. User %d already has project by name %s"
                .formatted(ownerId, createProjectDto.name()), dataValidationException.getMessage());
    }

    @Test
    void testCreatePositive() {
        long userId = 1L;
        CreateProjectDto createProjectDto = CreateProjectDto.builder()
                .name("project name")
                .parentProjectId(2L)
                .build();

        Project parentProject = Project.builder().id(createProjectDto.parentProjectId()).build();

        when(userContext.getUserId()).thenReturn(userId);
        when(projectRepository.existsByOwnerIdAndName(userId, createProjectDto.name())).thenReturn(false);
        when(projectRepository.getByIdOrThrow(parentProject.getId())).thenReturn(parentProject);
        when(projectRepository.save(any(Project.class))).thenReturn(projectMapper.toProject(createProjectDto));

        ProjectDto projectDto = projectService.create(createProjectDto);

        verify(projectRepository).save(projectArgumentCaptor.capture());

        Project projectToCreate = projectArgumentCaptor.getValue();

        assertEquals(userId, projectToCreate.getOwnerId());
        assertEquals(ProjectStatus.CREATED, projectToCreate.getStatus());
        assertEquals(parentProject.getId(), projectToCreate.getParentProject().getId());
        assertEquals(createProjectDto.name(), projectDto.name());
    }

    @Test
    void testUpdateThrowsIllegalArgumentExceptionIfUpdateDataIsNull() {
        IllegalArgumentException illegalArgumentException = Assertions.assertThrows(IllegalArgumentException.class,
                () -> projectService.update(1L, UpdateProjectDto.builder().build()));
        assertEquals("Values not provided. Nothing to update", illegalArgumentException.getMessage());
    }

    @Test
    void testUpdatePositive() {
        UpdateProjectDto updateProjectDto = UpdateProjectDto.builder()
                .description("Updated desc")
                .status(ProjectStatus.ON_HOLD)
                .build();

        List<ProjectStatus> statusList = Arrays.stream(ProjectStatus.values())
                .filter(status -> !status.equals(updateProjectDto.status())).toList();

        Project projectToUpdate = Project.builder()
                .id(1L)
                .description("Project description")
                .status(statusList.get(new Random().nextInt(statusList.size())))
                .build();

        Project projectToReturn = Project.builder().id(projectToUpdate.getId()).build();
        projectMapper.update(updateProjectDto, projectToReturn);

        when(projectRepository.getByIdOrThrow(projectToUpdate.getId())).thenReturn(projectToUpdate);
        when(projectRepository.save(any(Project.class))).thenReturn(projectToReturn);

        ProjectDto savedProject = projectService.update(projectToUpdate.getId(), updateProjectDto);

        verify(projectRepository).save(projectArgumentCaptor.capture());

        Project projectToSave = projectArgumentCaptor.getValue();

        assertEquals(projectToUpdate.getId(), projectToSave.getId());
        assertEquals(updateProjectDto.status(), projectToSave.getStatus());
        assertEquals(updateProjectDto.description(), projectToSave.getDescription());

        assertEquals(projectToUpdate.getId(), savedProject.id());
        assertEquals(updateProjectDto.status(), savedProject.status());
        assertEquals(updateProjectDto.description(), savedProject.description());
    }

    @Test
    void getAllProjects() {
        when(projectRepository.findAll()).thenReturn(new ArrayList<>(List
                .of(teamMemeberPrivetProject, privateProjectNotTeamMember, publicProjectNotTeamMember)));
        when(userContext.getUserId()).thenReturn(teamMember.getId());

        List<ProjectDto> allProjects = projectService.getAllProjects();

        assertEquals(2, allProjects.size());
        assertTrue(allProjects.stream().map(ProjectDto::id).toList()
                .containsAll(List.of(publicProjectNotTeamMember.getId(), teamMemeberPrivetProject.getId())));
    }

    @Test
    void testGetProjectByIdThrowsExceptionIfProjectNotFound() {
        long projectId = 1L;

        when(projectRepository.getByIdOrThrow(projectId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> projectService.getProjectById(projectId));
    }

    @Test
    void testGetProjectByIdThrowsExceptionIfNotAllowedToGetProjectInformation() {
        getGetProjectByIdCustomMocks(privateProjectNotTeamMember);

        ForbiddenException forbiddenException = assertThrows(ForbiddenException.class,
                () -> projectService.getProjectById(privateProjectNotTeamMember.getId()));
        assertEquals("Project is private. U are not allowed to get this project information",
                forbiddenException.getMessage());
    }

    @Test
    void testGetProjectByIdPositive() {
        getGetProjectByIdCustomMocks(teamMemeberPrivetProject);

        ProjectDto projectById = projectService.getProjectById(teamMemeberPrivetProject.getId());

        assertEquals(teamMemeberPrivetProject.getId(), projectById.id());
    }

    @Test
    void testGetByFiltersPositive() {
        ProjectFilterDto projectFilterDto = ProjectFilterDto.builder()
                .name("correct name")
                .status(ProjectStatus.ON_HOLD)
                .build();

        when(userContext.getUserId()).thenReturn(teamMember.getId());

        teamMemeberPrivetProject.setName(projectFilterDto.name());
        teamMemeberPrivetProject.setStatus(projectFilterDto.status());

        publicProjectNotTeamMember.setName(projectFilterDto.name() + "something");
        publicProjectNotTeamMember.setStatus(projectFilterDto.status());

        privateProjectNotTeamMember.setName(projectFilterDto.name());
        privateProjectNotTeamMember.setStatus(projectFilterDto.status());

        List<ProjectStatus> statusList = Arrays.stream(ProjectStatus.values())
                .filter(status -> !status.equals(projectFilterDto.status())).toList();

        Project projectWithWrongStatus = Project.builder()
                .id(teamMemeberPrivetProject.getId() + 233)
                .teams(new ArrayList<>(List.of(team)))
                .visibility(ProjectVisibility.PUBLIC)
                .status(statusList.get(new Random().nextInt(statusList.size())))
                .name(projectFilterDto.name())
                .build();

        Project projectWithWrongName = Project.builder()
                .id(teamMemeberPrivetProject.getId() + 231)
                .teams(new ArrayList<>(List.of(team)))
                .visibility(ProjectVisibility.PUBLIC)
                .status(projectFilterDto.status())
                .name("anything")
                .build();

        when(projectRepository.findAll()).thenReturn(new ArrayList<>(List.of(teamMemeberPrivetProject,
                publicProjectNotTeamMember, privateProjectNotTeamMember, projectWithWrongStatus, projectWithWrongName)));

        List<ProjectDto> projectsByFilters = projectService.getByFilters(projectFilterDto);

        assertEquals(2, projectsByFilters.size());
        assertTrue(projectsByFilters.stream().map(ProjectDto::id).toList()
                .containsAll(List.of(teamMemeberPrivetProject.getId(), publicProjectNotTeamMember.getId())));
    }

    private void getGetProjectByIdCustomMocks(Project project) {
        when(userContext.getUserId()).thenReturn(teamMember.getId());
        when(projectRepository.getByIdOrThrow(project.getId()))
                .thenReturn(project);
    }
}