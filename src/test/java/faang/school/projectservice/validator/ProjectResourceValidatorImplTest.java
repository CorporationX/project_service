package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.NoAccessException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.property.AmazonS3Properties;
import faang.school.projectservice.validator.resource.ProjectResourceValidatorImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectResourceValidatorImplTest {

    @Mock
    private AmazonS3Properties amazonS3Properties;

    @InjectMocks
    private ProjectResourceValidatorImpl projectResourceValidator;

    @Test
    void validateMaxStorageSizeGoodSize() {
        Project project = Project.builder()
                .id(1L)
                .storageSize(new BigInteger("1", 32))
                .build();

        when(amazonS3Properties.getMaxFreeStorageSizeBytes()).thenReturn(new BigInteger("3", 32));

        assertDoesNotThrow(() -> projectResourceValidator.validateMaxStorageSize(project, new BigInteger("1", 32).longValue()));
    }

    @Test
    void validateMaxStorageSize() {
        Project project = Project.builder()
                .id(1L)
                .storageSize(new BigInteger("1", 32))
                .build();

        when(amazonS3Properties.getMaxFreeStorageSizeBytes()).thenReturn(new BigInteger("2", 32));

        DataValidationException e = assertThrows(DataValidationException.class,
                () -> projectResourceValidator.validateMaxStorageSize(project, 1024 * 1024 * 1024));

        assertEquals(String.format("Project with ID %s. Limit reached. Storage is full.", project.getId()), e.getMessage());
    }

    @Test
    void validateDeletePermissionIsCreatedBy() {
        TeamMember teamMember = TeamMember.builder()
                .id(1L)
                .roles(new ArrayList<>())
                .build();

        Resource resource = Resource.builder()
                .id(2L)
                .createdBy(teamMember)
                .build();

        assertDoesNotThrow(() -> projectResourceValidator.validateDeletePermission(teamMember, resource));
    }

    @Test
    void validateDeletePermissionIsManager() {
        TeamMember teamMember = TeamMember.builder()
                .id(1L)
                .roles(List.of(TeamRole.MANAGER))
                .build();

        Resource resource = Resource.builder()
                .id(2L)
                .createdBy(new TeamMember())
                .build();

        assertDoesNotThrow(() -> projectResourceValidator.validateDeletePermission(teamMember, resource));
    }

    @Test
    void validateDeletePermissionIsManagerAndISCreatedBy() {
        TeamMember teamMember = TeamMember.builder()
                .id(1L)
                .roles(List.of(TeamRole.MANAGER))
                .build();

        Resource resource = Resource.builder()
                .id(2L)
                .createdBy(teamMember)
                .build();

        assertDoesNotThrow(() -> projectResourceValidator.validateDeletePermission(teamMember, resource));
    }

    @Test
    void validateDeletePermission() {
        TeamMember teamMember = TeamMember.builder()
                .id(1L)
                .roles(new ArrayList<>())
                .build();

        Resource resource = Resource.builder()
                .id(2L)
                .createdBy(new TeamMember())
                .build();

        NoAccessException e = assertThrows(NoAccessException.class, () -> projectResourceValidator.validateDeletePermission(teamMember, resource));

        assertEquals(
                String.format("Team member '%s' does not have permission to delete resource with ID %s.", teamMember, resource.getId()),
                e.getMessage()
        );
    }
}
