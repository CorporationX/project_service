package faang.school.projectservice.service.impl;

import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
        Long projectId = 222L;
        //Mockito.when(projectValidator.isUserParticipatedInProject(userId, projectId)).thenReturn(true);
        Mockito.when(projectServiceMock.getProject(projectId)).thenReturn(project);

        projectValidator.validateUserInProject(userId, projectId);

        //Mockito.when(projectValidator.isUserParticipatedInProject(userNotInProjectId, projectId)).thenReturn(false);
        Assert.assertThrows(IllegalArgumentException.class,
                () -> projectValidator.validateUserInProject(userNotInProjectId, projectId));
    }

    @Test
    @DisplayName("Test if user is in project")
    void testIsUserInProject() {
        long userId = 1L;
        long projectId = 222L;
        //Mockito.when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.ofNullable(project));
        Mockito.when(projectServiceMock.getProject(projectId)).thenReturn(project);
        Assertions.assertTrue(projectValidator.isUserParticipatedInProject(userId, projectId));
        userId = 33L;
        Assertions.assertFalse(projectValidator.isUserParticipatedInProject(userId, projectId));
    }

    @Test
    @DisplayName("Test is project public or not")
    void testIsProjectPublic() {
        long projectId = 1010L;
        long privateProjectId = 1011L;
        Project privateProject = Project.builder()
                .id(1011L)
                .name("test project 11")
                .visibility(ProjectVisibility.PRIVATE)
                .build();
        //Mockito.when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.ofNullable(project));
        Mockito.when(projectServiceMock.getProject(projectId)).thenReturn(project);
        Assertions.assertTrue(projectValidator.isProjectPublic(projectId));
        //Mockito.when(projectRepositoryMock.findById(privateProjectId)).thenReturn(Optional.ofNullable(privateProject));
        Mockito.when(projectServiceMock.getProject(privateProjectId)).thenReturn(privateProject);
        Assertions.assertFalse(projectValidator.isProjectPublic(privateProjectId));
    }
}