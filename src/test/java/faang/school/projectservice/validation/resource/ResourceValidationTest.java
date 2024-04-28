package faang.school.projectservice.validation.resource;

import faang.school.projectservice.handler.exceptions.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ResourceValidationTest {
    @InjectMocks
    private ResourceValidation resourceValidation;

    @Test
    public void testCheckingUserForUpdatingFileFail() {
        Project project = new Project();
        project.setId(3L);
        project.setOwnerId(4L);
        TeamMember teamMember1 = new TeamMember();
        teamMember1.setId(2L);
        TeamMember teamMember2 = new TeamMember();
        teamMember2.setId(1L);
        Resource resource = new Resource();
        resource.setId(1L);
        resource.setCreatedBy(teamMember1);
        resource.setProject(project);

        Assertions.assertThrows(DataValidationException.class, () -> resourceValidation.checkingUserForUpdatingFile(teamMember2, resource));
    }

    @Test
    public void testCheckingUserForDeletingFile() {
        Project project = new Project();
        project.setId(3L);
        project.setOwnerId(4L);
        TeamMember teamMember1 = new TeamMember();
        teamMember1.setId(2L);
        TeamMember teamMember2 = new TeamMember();
        teamMember2.setId(1L);
        Resource resource = new Resource();
        resource.setId(1L);
        resource.setCreatedBy(teamMember1);
        resource.setProject(project);

        Assertions.assertThrows(DataValidationException.class, () -> resourceValidation.checkingUserForDeletingFile(teamMember2, resource));
    }
}
