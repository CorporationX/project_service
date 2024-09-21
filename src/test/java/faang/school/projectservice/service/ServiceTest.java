package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.dto.client.ProjectFilterDto;
import faang.school.projectservice.dto.client.TeamMemberDto;
import faang.school.projectservice.filter.ProjectFilters;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.ProjectServiceImpl;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {
    @InjectMocks
    private ProjectServiceImpl projectService;
    @Mock
    private ValidatorProject validator;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private List<ProjectFilters> filters;
    @Spy
    private ProjectMapper mapper = Mappers.getMapper(ProjectMapper.class);

    @Test
    void testCreateProject() {
        ProjectDto projectDto = prepareListProjectDto().get(0);
        Project projectEntity = prepareListProjectEntity().get(0);

        when(mapper.toEntity(projectDto)).thenReturn(projectEntity);
        when(projectRepository.save(projectEntity)).thenReturn(projectEntity);

        projectService.createProject(projectDto);

        assertEquals(ProjectStatus.CREATED, projectEntity.getStatus());
        verify(projectRepository).save(projectEntity);
    }

    @Test
    void testUpdateStatus_existingProject() {
        Project project = prepareListProjectEntity().get(0);

        when(projectRepository.getProjectById(project.getId())).thenReturn(project);

        assertDoesNotThrow(() -> projectService.updateStatus(mapper.toDto(project), ProjectStatus.CANCELLED));
        assertEquals(ProjectStatus.CANCELLED, project.getStatus());
        verify(projectRepository).save(project);
    }

    @Test
    void testUpdateDescription() {
        Project project = prepareListProjectEntity().get(0);
        project.setDescription("Start description");

        when(projectRepository.getProjectById(project.getId())).thenReturn(project);

        assertDoesNotThrow(() -> projectService.updateDescription(mapper.toDto(project), "Finish description"));
        verify(projectRepository).save(project);
    }


    private List<ProjectDto> prepareListProjectDto() {
        List<ProjectDto> projectsDto = new ArrayList<>();
        ProjectDto firstProjectDto = new ProjectDto();
        ProjectDto secondProjectDto = new ProjectDto();
        projectsDto.add(firstProjectDto);
        projectsDto.add(secondProjectDto);

        return projectsDto;
    }

    private List<Project> prepareListProjectEntity() {
        List<Project> projects = new ArrayList<>();
        Project firstProject = new Project();
        Project secondProject = new Project();
        firstProject.setVisibility(ProjectVisibility.PUBLIC);
        secondProject.setVisibility(ProjectVisibility.PUBLIC);
        projects.add(firstProject);
        projects.add(secondProject);

        return projects;
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
        ProjectServiceImpl service = new ProjectServiceImpl(projectRepository, mapper, filters, validator);

        List<ProjectDto> result = service.getProjectsFilters(filterDto, requester);
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
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(id);

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

        boolean result = new ProjectServiceImpl(projectRepository, mapper, filters, validator)
                .checkUserByPrivateProject(project, requesterId);

        assertTrue(result);
    }
}
