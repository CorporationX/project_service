package faang.school.projectservice.validator.resource;

import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.PermissionDeniedDataAccessException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourcePermissionValidatorTest {

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private ResourcePermissionValidator resourcePermissionValidator;

    @Test
    void shouldReturnResourceIfUserIsCreator() {
        Long resourceId = 1L;
        Long userId = 2L;

        Resource resource = new Resource();
        Project project = new Project();
        resource.setProject(project);

        TeamMember creator = new TeamMember();
        creator.setUserId(userId);
        resource.setCreatedBy(creator);

        when(resourceRepository.getReferenceById(resourceId)).thenReturn(resource);

        Resource result = resourcePermissionValidator.getResourceWithPermission(resourceId, userId);

        assertEquals(resource, result);
        verify(resourceRepository, times(1)).getReferenceById(resourceId);
    }

    @Test
    void shouldReturnResourceIfUserIsManager() {
        Long resourceId = 1L;
        Long userId = 2L;

        Resource resource = new Resource();
        Project project = new Project();
        resource.setProject(project);

        TeamMember creator = new TeamMember();
        creator.setUserId(3L);
        resource.setCreatedBy(creator);

        TeamMember manager = new TeamMember();
        manager.setUserId(userId);
        List<TeamRole> roles = List.of(TeamRole.MANAGER);
        manager.setRoles(roles);

        Team team = new Team();
        team.setTeamMembers(List.of(manager));
        project.setTeams(List.of(team));

        when(resourceRepository.getReferenceById(resourceId)).thenReturn(resource);

        Resource result = resourcePermissionValidator.getResourceWithPermission(resourceId, userId);

        assertEquals(resource, result);
        verify(resourceRepository, times(1)).getReferenceById(resourceId);
    }

    @Test
    void shouldThrowExceptionIfUserHasNoPermission() {
        Long resourceId = 1L;
        Long userId = 2L;

        Resource resource = new Resource();
        Project project = new Project();
        resource.setProject(project);

        TeamMember creator = new TeamMember();
        creator.setUserId(3L);
        resource.setCreatedBy(creator);

        TeamMember nonManager = new TeamMember();
        nonManager.setUserId(userId);
        nonManager.setRoles(List.of(TeamRole.DEVELOPER));

        Team team = new Team();
        team.setTeamMembers(List.of(nonManager));
        project.setTeams(List.of(team));

        when(resourceRepository.getReferenceById(resourceId)).thenReturn(resource);

        assertThrows(PermissionDeniedDataAccessException.class, () -> resourcePermissionValidator.getResourceWithPermission(resourceId, userId));

        verify(resourceRepository, times(1)).getReferenceById(resourceId);
    }

}


