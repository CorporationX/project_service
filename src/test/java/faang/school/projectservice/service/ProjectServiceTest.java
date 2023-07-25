package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.project_filter.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import org.hibernate.query.results.Builders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Resources;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;
    @InjectMocks
    private ProjectService projectService;
    @Spy
    private ProjectMapperImpl projectMapper;

    private TeamMember teamMember;
    private Project project;
    private ProjectDto projectDto;

    @BeforeEach
    public void toSet() {
        List<Resource> lists = List.of(Resource.builder().id(1L).build());
        List<Team> listsTeamId = List.of(Team.builder().id(1L).build());
        List<Task> listsTaskId = List.of(Task.builder().id(1L).build());
        List<Vacancy> listsVacancyId = List.of(Vacancy.builder().id(1L).build());
        List<Project> listsProjectId = List.of(Project.builder().id(1L).build());
        List<Long> listId = List.of(1L, 2L);
        List<Stage> list = List.of(Stage.builder().stageId(1L).build());
        teamMember = TeamMember.builder().id(1L).build();
        project = Project.builder().id(1L).name("test").stages(list).children(listsProjectId).vacancies(listsVacancyId).resources(lists).teams(listsTeamId).tasks(listsTaskId).status(ProjectStatus.CREATED).build();
        projectDto = ProjectDto.builder().id(1L).name("test").stagesId(listId).childrenId(listId).vacanciesId(listId).resourcesId(listId).teamsId(listId).tasksId(listId).tasksId(listId).status(ProjectStatus.CREATED).ownerId(1L).build();
    }


    @Test
    void testCreateProjectDataValidationException() {
        ProjectDto projectDto = ProjectDto.builder().id(1L).name("test").ownerId(1L).build();
        when(projectRepository.existsByOwnerUserIdAndName(anyLong(),anyString())).thenReturn(true);
        assertThrows(DataValidationException.class,
                () -> projectService.createProject(projectDto));
    }
    @Test
    void testCreateProject() {
        when(projectRepository.existsByOwnerUserIdAndName(anyLong(),anyString())).thenReturn(false);
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toEntity(projectDto)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectDto);
        projectService.createProject(projectDto);
        assertEquals(ProjectStatus.CREATED, projectService.createProject(projectDto).getStatus());
    }

    @Test
    void testUpdateProject() {

    }

    @Test
    void testGetAllProjectsByStatus() {
//        List<ProjectFilter> projectFilter = List.of(ProjectFilter.builder().build());
//        when(projectRepository.findAll()).thenReturn(List.of(project));
//        projectService = new ProjectService(projectRepository, projectMapper, projectFilter);


    }

    @Test
    void testGetAllProjects() {
        when(projectRepository.getProjectById(1L)).thenReturn(project);
        projectService.getAllProjects();
        assertEquals(projectMapper.toDto(project), projectService.getProjectById(1L));
    }

    @Test
    void testGetProjectById() {
        when(projectRepository.getProjectById(1L)).thenReturn(project);
        projectService.getProjectById(1L);
        assertEquals(projectMapper.toDto(project), projectService.getProjectById(1L));
    }
}