package faang.school.projectservice.validator;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.enums.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CampaignValidatorTest {
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private TeamMemberJpaRepository teamMemberJpaRepository;
    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private CampaignValidator validator;

    @Test
    void testValidateUserThrowsWhenUserIdIsNegative() {
        assertThrows(DataValidationException.class, () -> validator.validateUser(-1));
    }

    @Test
    void testValidateUserThrowsWhenUserDoesNotExist() {
        when(userServiceClient.getUser(anyLong())).thenReturn(null);
        assertThrows(DataValidationException.class, () -> validator.validateUser(1));
    }

    @Test
    void testValidateUserIsCreatorThrowsWhenUserIdDoesNotMatch() {
        assertThrows(DataValidationException.class, () -> validator.validateUserIsCreator(1, 2));
    }

    @Test
    void testValidateManagerOrOwnerThrowsWhenUserIsNeither() {
        when(teamMemberJpaRepository.findRolesByProjectIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Arrays.asList(TeamRole.INTERN));
        assertThrows(DataValidationException.class, () -> validator.validateManagerOrOwner(1, 1));
    }

    @Test
    void testValidateManagerOrOwnerPassesWhenUserIsOwner() {
        when(teamMemberJpaRepository.findRolesByProjectIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Arrays.asList(TeamRole.OWNER));
        validator.validateManagerOrOwner(1, 1);
    }

    @Test
    void testValidateProjectExistsThrowsWhenProjectDoesNotExist() {
        when(projectRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(DataValidationException.class, () -> validator.validateProjectExists(1L));
    }

    @Test
    void testValidateCreatorIsTheSameThrowsWhenCreatorsDiffer() {
        assertThrows(DataValidationException.class, () -> validator.validateCreatorIsTheSame(1L, 2L));
    }
}
