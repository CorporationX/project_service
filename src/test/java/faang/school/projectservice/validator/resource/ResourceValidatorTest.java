package faang.school.projectservice.validator.resource;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.entity.Project;
import faang.school.projectservice.model.entity.Resource;
import faang.school.projectservice.model.entity.TeamMember;
import faang.school.projectservice.model.entity.TeamRole;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ResourceValidatorTest {
    ResourceValidator resourceValidator = new ResourceValidator();

    @Test
    void testValidateStorageExceed() {
        Project project = Project.builder()
                .maxStorageSize(BigInteger.valueOf(500))
                .build();

        assertThrows(DataValidationException.class,
                () -> resourceValidator.validateStorage(project, BigInteger.valueOf(700)));
    }

    @Test
    void testValidateStorageOk() {
        Project project = Project.builder()
                .maxStorageSize(BigInteger.valueOf(500))
                .build();

        resourceValidator.validateStorage(project, BigInteger.valueOf(200));
    }

    @Test
    void testValidateResourceOwner() {
        TeamMember teamMember = TeamMember.builder()
                .roles(List.of(TeamRole.ANALYST))
                .build();

        Resource resource = Resource.builder()
                .createdBy(TeamMember.builder().build())
                .build();

        assertThrows(DataValidationException.class,
                () -> resourceValidator.validateResourceOwner(resource, teamMember));
    }
}
