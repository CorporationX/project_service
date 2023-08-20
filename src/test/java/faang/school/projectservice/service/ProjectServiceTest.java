package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectByFilterDto;
import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.exception.CheckIfProjectExists;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.filter.project.ProjectNameFilter;
import faang.school.projectservice.filter.project.ProjectStatusFilter;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.project.Project;
import faang.school.projectservice.model.project.ProjectStatus;
import faang.school.projectservice.model.project.ProjectVisibility;
import faang.school.projectservice.model.resource.Resource;
import faang.school.projectservice.model.task.Task;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.Vacancy.Vacancy;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.ProjectService;
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
    private ProjectMapperImpl projectMapper;

    private TeamMember teamMember;
    private TeamMember teamMember2;
    private Project projectPublic;
    private Project projectPublic2;
    private Project projectPrivate;
    private Project projectPrivate2;
    private Project projectPrivate3;
    private Project projectPrivate4;
    private Project project;
    private ProjectDto projectDto;
    private ProjectByFilterDto projectByFilterDto;
    private ProjectCreateDto projectCreateDto;
    private ProjectUpdateDto projectUpdateDto;

    @BeforeEach
    public void toBeforeEach() {
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
        projectPrivate3 = Project.builder().visibility(ProjectVisibility.PRIVATE).id(7L).name("test1").status(ProjectStatus.IN_PROGRESS).teams(listsTeamId).build();
        projectPrivate4 = Project.builder().visibility(ProjectVisibility.PRIVATE).id(7L).name("test1").status(ProjectStatus.IN_PROGRESS).teams(listsTeamId).build();

        project = Project.builder().id(1L).name("test").stages(list).children(listsProjectId).vacancies(listsVacancyId).resources(lists).teams(listsTeamId).tasks(listsTaskId).status(ProjectStatus.CREATED).build();
        projectDto = ProjectDto.builder().id(1L).name("test").stagesId(listId).childrenId(listId).vacanciesId(listId).resourcesId(listId).teamsId(listId).tasksId(listId).status(ProjectStatus.CREATED).ownerId(1L).build();
        projectUpdateDto = ProjectUpdateDto.builder().name("test").stagesId(listId).childrenId(listId).vacanciesId(listId).resourcesId(listId).teamsId(listId).tasksId(listId).status(ProjectStatus.CREATED).ownerId(1L).build();
        projectCreateDto = ProjectCreateDto.builder().name("test").childrenId(listId).resourcesId(listId).teamsId(listId).status(ProjectStatus.CREATED).ownerId(1L).build();
        projectByFilterDto = ProjectByFilterDto.builder().name("test").status(ProjectStatus.CREATED).build();
    }

    @Test
    void testCreateProjectDataValidationException() {
        when(projectRepository.existsByOwnerUserIdAndName(anyLong(), anyString())).thenReturn(true);
        assertThrows(DataValidationException.class,
                () -> projectService.createProject(projectCreateDto));
    }

    @Test
    void testCreateProject() {
        when(projectRepository.existsByOwnerUserIdAndName(anyLong(), anyString())).thenReturn(false);
        when(projectRepository.save(any())).thenReturn(project);
        when(projectMapper.createDtoToProject(projectCreateDto)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectDto);
        assertEquals(ProjectStatus.CREATED, projectService.createProject(projectCreateDto).getStatus());
    }

    @Test
    void testUpdateProject() {
        when(projectRepository.existsById(1L)).thenReturn(true);
        when(projectRepository.getProjectById(1L)).thenReturn(project);
        projectService.updateProject(1L, projectUpdateDto);
        verify(projectMapper).update(projectUpdateDto, project);
        verify(projectRepository).save(project);
        assertEquals(projectMapper.toDto(project), projectService.getProjectById(1L));
    }

    @Test
    void testGetAllProjectsByFilter() {
        List<ProjectFilter> projectFilterList = List.of(new ProjectNameFilter(), new ProjectStatusFilter());
        when(projectRepository.existsById(1L)).thenReturn(true);
        when(projectRepository.findAll()).thenReturn(List.of(
                projectPublic, projectPrivate, projectPublic2, projectPrivate2, projectPrivate3, projectPrivate4));
        projectService = new ProjectService(projectRepository, projectMapper, projectFilterList);

        List<ProjectDto> projectsByStatus = projectService.getAllProjectsByFilter(
                1L, ProjectByFilterDto.builder().status(ProjectStatus.IN_PROGRESS).name("test1").build());
        assertEquals(4, projectsByStatus.size());
    }

    @Test
    void testGetAllProjectsIsEmpty() {
        assertEquals(new ArrayList<>(), projectService.getAllProjects());
    }

    @Test
    void testGetProjectByDataValidationException() {
        when(projectRepository.existsById(1L)).thenReturn(false);
        assertThrows(CheckIfProjectExists.class, () -> projectService.getProjectById(1L));
    }

    @Test
    void testGetProjectById() {
        when(projectRepository.existsById(1L)).thenReturn(true);
        when(projectRepository.getProjectById(1L)).thenReturn(project);
        projectService.getProjectById(1L);
        assertEquals(projectMapper.toDto(project), projectService.getProjectById(1L));
    }
}