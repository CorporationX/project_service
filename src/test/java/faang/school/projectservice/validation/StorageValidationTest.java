package faang.school.projectservice.validation;

import faang.school.projectservice.exception.StorageException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class StorageValidationTest {

    @Test
    public void testStorageSizeNotExceededValidationThrows() {
        BigInteger newSize = BigInteger.valueOf(101L);
        BigInteger maxSize = BigInteger.valueOf(100L);

        assertThrows(StorageException.class,
                ()-> StorageValidation.storageSizeNotExceededValidation(newSize, maxSize));
    }

    @Test
    public void testStorageFileAccessPermissionCheckThrows() {
        Resource resource = Resource.builder().allowedRoles(List.of(TeamRole.OWNER)).build();
        TeamMember member = TeamMember.builder().roles(List.of(TeamRole.TESTER)).build();

        assertThrows(StorageException.class,
                ()-> StorageValidation.storageFileAccessPermissionCheck(resource, member));
    }

    @Test
    public void testProjectResourcesAccessPermissionCheckThrows() {
        Project project = Project.builder().id(1L).build();
        Team team = Team.builder().project(Project.builder().id(2L).build()).build();
        TeamMember member = TeamMember.builder().team(team).build();

        assertThrows(StorageException.class,
                ()-> StorageValidation.projectResourcesAccessPermissionCheck(project, member));
    }

}
