package faang.school.projectservice.validator;

import faang.school.projectservice.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class ResourceValidatorTest {

    @InjectMocks
    private ResourceValidator resourceValidator;

    BigInteger newStorageSize;
    BigInteger projectMaxStorageSize;
    Resource resource;
    Project project;
    long userId = 1L;
    Team team;
    TeamMember teamMember;

    @BeforeEach
    void setUp() {
        newStorageSize = new BigInteger("10");
        projectMaxStorageSize = new BigInteger("20");
        teamMember = TeamMember.builder()
                .userId(userId)
                .roles(List.of(TeamRole.MANAGER))
                .build();
        resource = Resource.builder()
                .createdBy(teamMember)
                .build();
        team = Team.builder()
                .teamMembers(List.of(teamMember))
                .build();
        project = Project.builder()
                .resources(List.of(resource))
                .teams(List.of(team))
                .build();
    }

    @Test
    void testCheckStorageSizeExceeded() {
        resourceValidator.checkStorageSizeExceeded(newStorageSize, projectMaxStorageSize);
        assertDoesNotThrow(() -> resourceValidator.checkStorageSizeExceeded(newStorageSize, projectMaxStorageSize));
    }

    @Test
    void testCheckingAccessRights() {
        resourceValidator.checkingAccessRights(userId, resource, project);
        assertDoesNotThrow(() -> resourceValidator.checkingAccessRights(userId, resource, project));
    }
}