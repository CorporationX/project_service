package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.dto.client.ProjectFilterDto;
import faang.school.projectservice.dto.client.TeamMemberDto;
import faang.school.projectservice.filter.ProjectFilters;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project_service.ProjectServiceImpl;
import faang.school.projectservice.validator.ValidatorProject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {
    @InjectMocks
    private ProjectServiceImpl projectService;
    @Mock
    private ValidatorProject validation;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private List<ProjectFilters> filters;
    @Spy
    private ProjectMapper mapper = Mappers.getMapper(ProjectMapper.class);

    @Test
    public void testUpdateStatusGetException() {
        ProjectDto projectDto = new ProjectDto();
        Project project = new Project();
        project.setStatus(ProjectStatus.COMPLETED);
        ProjectStatus status = ProjectStatus.CANCELLED;

        when(validation.getEntity(projectDto)).thenReturn(project);
        assertThrows(NoSuchElementException.class, () -> projectService.updateStatus(projectDto, status));
    }

    @Test
    public void testUpdateStatus() {
        ProjectDto projectDto = new ProjectDto();
        Project projectEntity = new Project();
        ProjectStatus status = ProjectStatus.CANCELLED;
        when(validation.getEntity(projectDto)).thenReturn(projectEntity);
        when(projectRepository.existsById(projectEntity.getId())).thenReturn(true);

        assertDoesNotThrow(() -> projectService.updateStatus(projectDto, status));
    }

    @Test
    public void testUpdateDescriptionGetException() {
        ProjectDto projectDto = new ProjectDto();
        Project projectEntity = new Project();
        when(validation.getEntity(projectDto)).thenReturn(projectEntity);

        assertThrows(NoSuchElementException.class,
                () -> projectService.updateDescription(projectDto, "description"));
    }

    @Test
    public void testUpdateDescription() {
        ProjectDto projectDto = new ProjectDto();
        Project projectEntity = new Project();
        projectEntity.setDescription("Start description");
        when(validation.getEntity(projectDto)).thenReturn(projectEntity);
        when(projectRepository.existsById(projectEntity.getId())).thenReturn(true);

        assertDoesNotThrow(
                () -> projectService.updateDescription(projectDto, "Finish description"));
    }

    @Test
    public void testGetProjectsFilters() {
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
        ProjectServiceImpl service = new ProjectServiceImpl(projectRepository, mapper, filters, validation);

        List<ProjectDto> result = service.getProjectsFilters(filterDto, requester);
        assertThat(result).isEqualTo(projects);
    }

    @Test
    public void testGetProjects() {
        List<ProjectDto> projectsDto = new ArrayList<>();
        List<Project> projects = new ArrayList<>();
        ProjectDto firstProjectDto = new ProjectDto();
        ProjectDto secondProjectDto = new ProjectDto();
        Project firstProject = new Project();
        Project secondProject = new Project();
        projects.add(firstProject);
        projects.add(secondProject);
        projectsDto.add(firstProjectDto);
        projectsDto.add(secondProjectDto);

        when(projectRepository.findAll()).thenReturn(projects);
        when(mapper.toDto(firstProject)).thenReturn(firstProjectDto);
        when(mapper.toDto(secondProject)).thenReturn(secondProjectDto);

        List<ProjectDto> result = projectService.getProjects();

        assertEquals(projectsDto, result);
    }

    @Test
    public void testFindById() {
        long id = 1L;
        Project project = new Project();
        project.setId(id);
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(id);

        when(validation.findById(id)).thenReturn(project);
        when(mapper.toDto(project)).thenReturn(projectDto);
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

        boolean result = new ProjectServiceImpl(projectRepository, mapper, filters, validation).check(project, requesterId);

        assertTrue(result);
    }
}
