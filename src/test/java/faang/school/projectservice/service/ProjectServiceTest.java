package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.project_filter.ProjectFilter;
import faang.school.projectservice.filter.project_filter.ProjectNameFilter;
import faang.school.projectservice.filter.project_filter.ProjectStatusFilter;

import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;
    @InjectMocks
    private ProjectService projectService;
   @Spy
    private ProjectMapper projectMapper;

    private TeamMember teamMember;
    private TeamMember teamMember2;
    private Project projectPublic;
    private Project projectPublic2;
    private Project projectPrivate;
    private Project projectPrivate2;
    private Project project;
    private ProjectDto projectDto;

    @BeforeEach
    public void toSet() {
        teamMember = TeamMember.builder().id(1L).build();
        teamMember2 = TeamMember.builder().id(2L).build();

        List<Resource> lists = new ArrayList<>(List.of(Resource.builder().id(1L).build()));
        List<Team> listsTeamId = new ArrayList<>(List.of(Team.builder().id(1L).teamMembers(List.of(teamMember, teamMember2)).build()));
        List<Task> listsTaskId = new ArrayList<>(List.of(Task.builder().id(1L).build()));
        List<Vacancy> listsVacancyId = new ArrayList<>(List.of(Vacancy.builder().id(1L).build()));
        List<Project> listsProjectId = new ArrayList<>(List.of(Project.builder().id(1L).build()));
        List<Long> listId = new ArrayList<>(List.of(1L, 2L));
        List<Stage> list = new ArrayList<>(List.of(Stage.builder().stageId(1L).build()));

        projectPublic = Project.builder().visibility(ProjectVisibility.PUBLIC).id(5L).name("test1").status(ProjectStatus.IN_PROGRESS).teams(listsTeamId).build();
        projectPublic2 = Project.builder().visibility(ProjectVisibility.PUBLIC).id(4L).name("test2").status(ProjectStatus.CREATED).teams(listsTeamId).build();
        projectPrivate = Project.builder().visibility(ProjectVisibility.PRIVATE).id(9L).name("test1").status(ProjectStatus.IN_PROGRESS).teams(listsTeamId).build();
        projectPrivate2 = Project.builder().visibility(ProjectVisibility.PRIVATE).id(7L).name("test2").status(ProjectStatus.CREATED).teams(listsTeamId).build();

        project = Project.builder().id(1L).name("test").stages(list).children(listsProjectId).vacancies(listsVacancyId).resources(lists).teams(listsTeamId).tasks(listsTaskId).status(ProjectStatus.CREATED).build();
        projectDto = ProjectDto.builder().id(1L).name("test").stagesId(listId).childrenId(listId).vacanciesId(listId).resourcesId(listId).teamsId(listId).tasksId(listId).status(ProjectStatus.CREATED).ownerId(1L).build();
    }


    @Test
    void testCreateProjectDataValidationException() {
        ProjectDto projectDto = ProjectDto.builder().id(1L).name("test").ownerId(1L).build();
        when(projectRepository.existsByOwnerUserIdAndName(anyLong(), anyString())).thenReturn(true);
        assertThrows(DataValidationException.class,
                () -> projectService.createProject(projectDto));
    }

    @Test
    void testCreateProject() {
        when(projectRepository.existsByOwnerUserIdAndName(anyLong(), anyString())).thenReturn(false);
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toEntity(projectDto)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectDto);
        projectService.createProject(projectDto);
        assertEquals(ProjectStatus.CREATED, projectService.createProject(projectDto).getStatus());
    }

    @Test
    void testUpdateProject() {
        when(projectRepository.existsById(1L)).thenReturn(true);
        when(projectRepository.getProjectById(1L)).thenReturn(project);
        projectService.updateProject(1L, projectDto);
        verify(projectMapper).update(projectDto, project);
        verify(projectRepository).save(project);
        assertEquals(projectMapper.toDto(project), projectService.getProjectById(1L));
    }

    @Test
    void testGetAllProjectsByStatus() {
        List<ProjectFilter> projectFilterList = List.of(new ProjectNameFilter(), new ProjectStatusFilter());
        when(projectRepository.existsById(1L)).thenReturn(true);
        when(projectRepository.findAll()).thenReturn(List.of(
                projectPublic, projectPrivate, projectPublic2, projectPrivate2));
        projectService = new ProjectService(projectRepository, projectMapper, projectFilterList);

        List<ProjectDto> projectsByStatus = projectService.getAllProjectsByStatus(
                1L, ProjectDto.builder().status(ProjectStatus.IN_PROGRESS).name("test1").build());
        assertEquals(2, projectsByStatus.size());
    }

    @Test
    void testGetAllProjects() {
        when(projectRepository.existsById(1L)).thenReturn(true);
        when(projectRepository.getProjectById(1L)).thenReturn(project);
        projectService.getAllProjects();
        assertEquals(projectMapper.toDto(project), projectService.getProjectById(1L));
    }

    @Test
    void testGetProjectByDataValidationException() {
        when(projectRepository.existsById(1L)).thenReturn(false);
        assertThrows(DataValidationException.class, () -> projectService.getProjectById(1L));
    }

    @Test
    void testGetProjectById() {
        when(projectRepository.existsById(1L)).thenReturn(true);
        when(projectRepository.getProjectById(1L)).thenReturn(project);
        projectService.getProjectById(1L);
        assertEquals(projectMapper.toDto(project), projectService.getProjectById(1L));
    }
}