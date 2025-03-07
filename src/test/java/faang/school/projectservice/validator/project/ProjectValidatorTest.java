package faang.school.projectservice.validator.project;

import faang.school.projectservice.exception.CampaignCannotBeCreated;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectValidatorTest {
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @InjectMocks
    private ProjectValidator projectValidator;

    private Project project;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(1L);
        project.setOwnerId(1L);
    }

    @Test
    public void testValidateUserOwnerOrManager_ShouldNotThrowExceptionWhenUserIsOwner() {
        assertDoesNotThrow(() ->
                projectValidator.validateUserOwnerOrManager(project, 1L));
    }

    @Test
    public void testValidateUserOwnerOrManager_ShouldThrowExceptionWhenUserIsNotOwnerAndManager() {
        when(teamMemberRepository.checkUserHavingRole(2L, 1L, TeamRole.MANAGER))
                .thenReturn(false);

        assertThrows(CampaignCannotBeCreated.class,
                () -> projectValidator.validateUserOwnerOrManager(project, 2L));
    }

}
