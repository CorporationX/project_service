package faang.school.projectservice.service;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private UserService userService;

    private List<Project> projects;
    private List<Long> userIdsFromDataBase;
    private List<Long> newUserIds;

    @BeforeEach
    void setUp() {
        projects = new ArrayList<>();
        userIdsFromDataBase = new ArrayList<>();
        newUserIds = new ArrayList<>();
    }

    @Test
    @DisplayName("Test getNewMemberIds with projects having team members")
    void testGetNewMemberIdsWithProjectsHavingTeamMembers() {
        projects = Arrays.asList(
                Project.builder().id(1L).name("Project 1").build(),
                Project.builder().id(2L).name("Project 2").build()
        );

        TeamMember member1 = TeamMember.builder().id(1L).userId(1L).team(new Team()).build();
        TeamMember member2 = TeamMember.builder().id(2L).userId(2L).team(new Team()).build();

        when(teamMemberRepository.findByProjectId(1L)).thenReturn(Collections.singletonList(member1));
        when(teamMemberRepository.findByProjectId(2L)).thenReturn(Collections.singletonList(member2));

        List<Long> result = userService.getNewMemberIds(projects);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0));
        assertEquals(2L, result.get(1));
    }

    @Test
    @DisplayName("Test getNewMemberIds with empty projects list")
    void testGetNewMemberIdsWithEmptyProjectsList() {
        List<Long> result = userService.getNewMemberIds(new ArrayList<>());

        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Test getNewMemberIds with projects having no team members")
    void testGetNewMemberIdsWithProjectsHavingNoTeamMembers() {
        projects = Arrays.asList(
                Project.builder().id(1L).name("Project 1").build(),
                Project.builder().id(2L).name("Project 2").build()
        );

        when(teamMemberRepository.findByProjectId(1L)).thenReturn(new ArrayList<>());
        when(teamMemberRepository.findByProjectId(2L)).thenReturn(new ArrayList<>());

        List<Long> result = userService.getNewMemberIds(projects);

        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Test findDifferentMemberIds with new user IDs not in database")
    void testFindDifferentMemberIdsWithNewUserIdsNotInDatabase() {
        userIdsFromDataBase = Arrays.asList(1L, 2L, 3L);
        newUserIds = new ArrayList<>(Arrays.asList(2L, 3L, 4L, 5L));  // Convert to ArrayList for modification

        List<Long> result = userService.findDifferentMemberIds(userIdsFromDataBase, newUserIds);

        assertEquals(2, result.size());
        assertEquals(4L, result.get(0));
        assertEquals(5L, result.get(1));
    }

    @Test
    @DisplayName("Test findDifferentMemberIds with empty new user IDs list")
    void testFindDifferentMemberIdsWithEmptyNewUserIdsList() {
        userIdsFromDataBase = Arrays.asList(1L, 2L, 3L);

        List<Long> result = userService.findDifferentMemberIds(userIdsFromDataBase, new ArrayList<>());

        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Test findDifferentMemberIds with empty user IDs from database")
    void testFindDifferentMemberIdsWithEmptyUserIdsFromDataBase() {
        newUserIds = new ArrayList<>(Arrays.asList(1L, 2L, 3L));

        List<Long> result = userService.findDifferentMemberIds(new ArrayList<>(), newUserIds);

        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("Test findDifferentMemberIds with both lists empty")
    void testFindDifferentMemberIdsWithBothListsEmpty() {
        List<Long> result = userService.findDifferentMemberIds(new ArrayList<>(), new ArrayList<>());

        assertEquals(0, result.size());
    }
}