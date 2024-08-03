package faang.school.projectservice.service;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private ProjectService projectService;

    private List<Project> projectsFromDataBase;
    private List<Long> newProjectIds;
    private List<Long> userIds;

    @BeforeEach
    void setUp() {
        projectsFromDataBase = new ArrayList<>();
        newProjectIds = new ArrayList<>();
        userIds = new ArrayList<>();
    }

    @Test
    @DisplayName("Test findDifferentProjects with new projects not in database")
    void testFindDifferentProjectsWhenNewProjectIdsNotInDatabase() {
        projectsFromDataBase.add(Project.builder().id(1L).name("Existing Project").build());
        newProjectIds = new ArrayList<>(Arrays.asList(1L, 2L, 3L));

        when(projectRepository.getProjectById(2L)).thenReturn(new Project());
        when(projectRepository.getProjectById(3L)).thenReturn(new Project());

        List<Project> result = projectService.findDifferentProjects(projectsFromDataBase, newProjectIds);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Test findDifferentProjects with empty newProjectIds list")
    void testFindDifferentProjectsWhenNewProjectIdsIsEmpty() {
        projectsFromDataBase.add(Project.builder().id(1L).name("Existing Project").build());

        List<Project> result = projectService.findDifferentProjects(projectsFromDataBase, new ArrayList<>());

        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Test findDifferentProjects with empty projectsFromDataBase list")
    void testFindDifferentProjectsWhenProjectsFromDataBaseIsEmpty() {
        newProjectIds = Arrays.asList(1L, 2L, 3L);

        when(projectRepository.getProjectById(1L)).thenReturn(new Project());
        when(projectRepository.getProjectById(2L)).thenReturn(new Project());
        when(projectRepository.getProjectById(3L)).thenReturn(new Project());

        List<Project> result = projectService.findDifferentProjects(new ArrayList<>(), newProjectIds);

        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("Test findDifferentProjects with both lists empty")
    void testFindDifferentProjectsWhenBothListsAreEmpty() {
        List<Project> result = projectService.findDifferentProjects(new ArrayList<>(), new ArrayList<>());

        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Test getNewProjects with empty user IDs list")
    void testGetNewProjectsWhenUserIdsIsEmpty() {
        List<Project> result = projectService.getNewProjects(new ArrayList<>());

        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Test getNewProjects with user IDs having no team members")
    void testGetNewProjectsWhenNoneOfUserIdsHaveTeamMembers() {
        userIds = Arrays.asList(1L, 2L);

        when(teamMemberRepository.findByUserId(1L)).thenReturn(new ArrayList<>());
        when(teamMemberRepository.findByUserId(2L)).thenReturn(new ArrayList<>());

        List<Project> result = projectService.getNewProjects(userIds);

        assertEquals(0, result.size());
    }
}