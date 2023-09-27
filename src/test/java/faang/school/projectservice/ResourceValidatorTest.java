package faang.school.projectservice;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.FileParseException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.validator.ResourcesValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ResourceValidatorTest {

    @InjectMocks
    private ResourcesValidator resourceValidator;

    private Project project;

    private Resource resource;

    @BeforeEach
    void setUp() {
        project = Project.builder()
                .ownerId(1L)
                .build();

        TeamMember teamMember = TeamMember.builder()
                .userId(1L)
                .build();

        resource = Resource.builder()
                .createdBy(teamMember)
                .build();
    }

    @Test
    void checkStorageCapacityThrowExceptionTest() {
        assertThrows(FileParseException.class, () -> resourceValidator.checkStorageCapacity(2_097_152_001L));
    }

    @Test
    void checkRightsToDeleteForResourceThrowExceptionTest() {
        assertThrows(DataValidationException.class, () -> resourceValidator.checkRightsToDelete(resource, project, 2L));
    }

    @Test
    void checkTeamMemberInProjectThrowExceptionTest() {
        assertThrows(DataValidationException.class, () -> resourceValidator.checkTeamMemberInProject(null));
    }
}
