package faang.school.projectservice.service.presentation;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ProjectValidatorTest {
    @Mock
    private ProjectService projectServiceMock;
    @Mock
    ProjectRepository projectRepositoryMock;
    @InjectMocks
    ProjectValidator projectValidator;
    private Project project;

    @BeforeEach
    void setUp() {
        TeamMember teamMember1 = TeamMember.builder()
                .userId(1L)
                .build();
        TeamMember teamMember2 = TeamMember.builder()
                .userId(2L)
                .build();

        Team team = new Team();
        team.setTeamMembers(List.of(teamMember1, teamMember2));

        List<Team> teams = new ArrayList<>();
        teams.add(team);

        List<Resource> resources = new ArrayList<>();
        Resource resource1 = Resource.builder()
                .id(10001L)
                .key("key_1")
                .name("file_1")
                .build();
        Resource resource2 = Resource.builder()
                .id(10002L)
                .key("key_2")
                .name("file_2")
                .build();
        resources.add(resource1);
        resources.add(resource2);

        project = Project.builder()
                .id(1010L)
                .name("test project 10")
                .teams(teams)
                .resources(resources)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }

    @Test
    @DisplayName("Test user is in project")
    void validateUserInProject() {
        Long userId = 1L;
        Long userNotInProjectId = 11L;

        projectValidator.validateUserInProject(userId, project);
        Assert.assertThrows(IllegalArgumentException.class,
                () -> projectValidator.validateUserInProject(userNotInProjectId, project));
    }

    @Test
    @DisplayName("Test if user is in project")
    void testIsUserInProject() {
        long userId = 1L;

        Assertions.assertTrue(projectValidator.isUserParticipatedInProject(userId, project));
        userId = 33L;
        Assertions.assertFalse(projectValidator.isUserParticipatedInProject(userId, project));
    }

    @Test
    @DisplayName("Test is project public or not")
    void testIsProjectPublic() {

        Project privateProject = Project.builder()
                .id(1011L)
                .name("test project 11")
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        Assertions.assertTrue(projectValidator.isProjectPublic(project));
        Assertions.assertFalse(projectValidator.isProjectPublic(privateProject));
    }
}