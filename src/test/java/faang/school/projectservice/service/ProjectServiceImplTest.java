package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.SubProjectValidation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import faang.school.projectservice.dto.client.ProjectFilterDto;
import faang.school.projectservice.dto.client.TeamMemberDto;
import faang.school.projectservice.filter.ProjectFilters;
import faang.school.projectservice.model.*;
import faang.school.projectservice.validator.ValidatorProject;
import org.assertj.core.api.Assertions;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceImplTest {
    @InjectMocks
    private ProjectServiceImpl projectService;
    @Mock
    private SubProjectValidation validation;
    @Mock
    private ValidatorProject validator;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private List<ProjectFilters> filters;
    @Spy
    private ProjectMapper mapper = Mappers.getMapper(ProjectMapper.class);

    long userId = 1L;
    long projectId = 2L;
    long ownerId = 1L;
    long parentId = 2L;

    @Test
    void testCreateProject() {
        ProjectDto projectDto = prepareListProjectDto().get(0);
        Project projectEntity = prepareListProjectEntity().get(0);

        when(mapper.toEntity(projectDto)).thenReturn(projectEntity);
        when(projectRepository.save(projectEntity)).thenReturn(projectEntity);
        when(projectRepository.getProjectById(projectDto.id())).thenReturn(projectEntity);

        projectService.createProject(projectDto);

        assertEquals(ProjectStatus.CREATED, projectEntity.getStatus());
        verify(projectRepository).save(projectEntity);
    }

    @Test
    void testUpdateStatus_existingProject() {
        Project project = prepareListProjectEntity().get(0);

        when(projectRepository.getProjectById(project.getId())).thenReturn(project);

        assertDoesNotThrow(() -> projectService.updateStatus(project.getId(), ProjectStatus.CANCELLED));
        assertEquals(ProjectStatus.CANCELLED, project.getStatus());
        verify(projectRepository).save(project);
    }

    @Test
    void testUpdateDescription() {
        Project project = prepareListProjectEntity().get(0);
        project.setDescription("Start description");

        when(projectRepository.getProjectById(project.getId())).thenReturn(project);

        assertDoesNotThrow(() -> projectService.updateDescription(project.getId(), "Finish description"));
        verify(projectRepository).save(project);
    }

    @Test
    void testGetProjectsFilters() {
        ProjectFilterDto filterDto = new ProjectFilterDto();
        filterDto.setName("Name");

        List<Project> projects = new ArrayList<>();

        Project firstProject = new Project();
        Project secondProject = new Project();
        TeamMemberDto requester = new TeamMemberDto();
        firstProject.setName("Name first");
        secondProject.setName("Name second");
        requester.setUserId(1L);

        when(projectRepository.findAll()).thenReturn(projects);

        List<ProjectDto> result = projectService.getProjectsFilters(filterDto, requester);
        assertThat(result).isEqualTo(projects);
    }

    @Test
    public void testGetProjects() {
        List<Project> projects = prepareListProjectEntity();
        List<ProjectDto> projectsDto = prepareListProjectDto();

        when(projectRepository.findAll()).thenReturn(projects);
        when(mapper.toDto(projects.get(0))).thenReturn(projectsDto.get(0));
        when(mapper.toDto(projects.get(1))).thenReturn(projectsDto.get(1));

        List<ProjectDto> result = projectService.getProjects();

        assertEquals(projectsDto, result);
    }

    @Test
    public void testFindById() {
        long id = 1L;
        Project project = new Project();
        project.setId(id);
        ProjectDto projectDto = ProjectDto.builder().id(id).build();

        when(projectService.findById(id)).thenReturn(projectDto);
        ProjectDto result = projectService.findById(id);

        assertEquals(result, projectDto);
    }

    @Test
    public void testCheck() {
        long requesterId = 123L;
        long otherId = 456L;

        TeamMember member1 = mock(TeamMember.class);
        TeamMember member2 = mock(TeamMember.class);
        Team team1 = mock(Team.class);
        Team team2 = mock(Team.class);
        Project project = mock(Project.class);

        when(member1.getUserId()).thenReturn(requesterId);
        when(member2.getUserId()).thenReturn(otherId);

        when(team1.getTeamMembers()).thenReturn(List.of(member1));
        when(team2.getTeamMembers()).thenReturn(List.of(member2));

        when(project.getTeams()).thenReturn(List.of(team1, team2));

        boolean result = projectService.checkUserByPrivateProject(project, requesterId);

        assertTrue(result);
    }

    ////////////////////////////////////////////////////////////////////////////////////
    @Test
    void createSubProject_validInput() {
        CreateSubProjectDto createSubProjectDto = new CreateSubProjectDto(null, "Test subproject", "Description", parentId);
        Project parentProject = Project.builder().id(parentId).build();
        Project subProject = Project.builder()
                .id(3L)
                .name("Test subproject")
                .description("Description")
                .ownerId(ownerId)
                .parentProject(Project.builder().id(parentId).build())
                .build();

        when(projectRepository.getProjectById(parentId)).thenReturn(parentProject);
        when(mapper.toEntity(createSubProjectDto, parentProject, ownerId)).thenReturn(subProject);
        when(projectRepository.save(subProject)).thenReturn(subProject);
        when(mapper.toDto(subProject)).thenReturn(ProjectDto.builder().id(3L)
                .name("Test subproject")
                .description("Description")
                .ownerId(ownerId)
                .parentId(parentId)
                .build());

        ProjectDto result = projectService.createSubProject(ownerId, createSubProjectDto);

        Assertions.assertThat(result.id()).isEqualTo(3L);
        Assertions.assertThat(result.name()).isEqualTo("Test subproject");
        Assertions.assertThat(result.description()).isEqualTo("Description");
        Assertions.assertThat(result.ownerId()).isEqualTo(ownerId);
        Assertions.assertThat(result.parentId()).isEqualTo(parentId);

        verify(projectRepository, times(1)).getProjectById(parentId);
        verify(projectRepository, times(1)).save(subProject);
    }

    @Test
    void updateSubProject_validInput() {
        ProjectStatus status = ProjectStatus.IN_PROGRESS;
        ProjectVisibility visibility = ProjectVisibility.PRIVATE;
        UpdateSubProjectDto updateSubProjectDto = new UpdateSubProjectDto(projectId, status, visibility);
        Project project = Project.builder()
                .id(projectId)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        Project updatedProject = Project
                .builder()
                .id(projectId)
                .status(status)
                .visibility(visibility)
                .build();

        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(projectRepository.save(any())).thenReturn(any());
        when(mapper.toDto(updatedProject)).thenReturn(ProjectDto.builder().id(projectId)
                .status(status)
                .build());
        ProjectDto result = projectService.updateSubProject(userId, updateSubProjectDto);

        Assertions.assertThat(result.id()).isEqualTo(projectId);
        Assertions.assertThat(result.status()).isEqualTo(status);

        verify(projectRepository, times(1)).getProjectById(projectId);
        verify(validation, times(1)).updateSubProject(userId, updateSubProjectDto, project);
        verify(projectRepository, times(1)).save(any());
    }

    private List<ProjectDto> prepareListProjectDto() {
        List<ProjectDto> projectsDto = new ArrayList<>();
        ProjectDto firstProjectDto = ProjectDto.builder().build();
        ProjectDto secondProjectDto = ProjectDto.builder().build();
        projectsDto.add(firstProjectDto);
        projectsDto.add(secondProjectDto);

        return projectsDto;
    }

    private List<Project> prepareListProjectEntity() {
        List<Project> projects = new ArrayList<>();
        Project firstProject = new Project();
        firstProject.setId(1L);
        Project secondProject = new Project();
        secondProject.setId(2L);
        firstProject.setVisibility(ProjectVisibility.PUBLIC);
        secondProject.setVisibility(ProjectVisibility.PUBLIC);
        projects.add(firstProject);
        projects.add(secondProject);

        return projects;
    }
}
