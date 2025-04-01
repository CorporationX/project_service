package faang.school.projectservice.validator;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.exception.DataValidateException;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.adapter.TeamMemberRepositoryAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CampaignValidatorTest {

    @InjectMocks
    private CampaignValidator validator;

    @Mock
    private TeamMemberRepositoryAdapter teamMemberRepositoryAdapter;
    @Mock
    private UserContext userContext;

    TeamMember teamMemberWithoutRequiredRoles = TeamMember.builder()
            .roles(List.of(TeamRole.ANALYST,
                    TeamRole.INTERN,
                    TeamRole.DESIGNER,
                    TeamRole.DEVELOPER,
                    TeamRole.TESTER))
            .build();

    static Stream<Object[]> teamMembersWithAppropriateRules() {
        return Stream.of(
                new Object[]{TeamMember.builder()
                        .roles(List.of(TeamRole.OWNER)).build()},
                new Object[]{TeamMember.builder()
                        .roles(List.of(TeamRole.OWNER)).build()}
        );
    }

    static Stream<Object[]> invalidStatusesForCreate() {
        return Stream.of(
                new Object[]{CampaignStatus.CANCELED},
                new Object[]{CampaignStatus.COMPLETED},
                new Object[]{CampaignStatus.DELETED}
        );
    }

    @ParameterizedTest
    @MethodSource({"teamMembersWithAppropriateRules"})
    void userStatusValidationPositiveTest(TeamMember teamMember) {
        when(userContext.getUserId()).thenReturn(1L);
        when(teamMemberRepositoryAdapter.getByUserIdAndProjectId(1L, 1L))
                .thenReturn(teamMember);
        validator.userStatusValidation(1L);
    }

    @Test
    void userStatusValidationNegativeTest() {
        when(userContext.getUserId()).thenReturn(1L);
        when(teamMemberRepositoryAdapter.getByUserIdAndProjectId(1L, 1L))
                .thenReturn(teamMemberWithoutRequiredRoles);
        assertThrows(DataValidateException.class,
                () -> validator.userStatusValidation(1L),
                "You are not the creator or manager of the project");
    }

    @Test
    void statusByCreateValidationPositiveTest() {
        validator.statusByCreateValidation(CampaignStatus.ACTIVE);
    }

    @ParameterizedTest
    @MethodSource({"invalidStatusesForCreate"})
    void statusByCreateValidationNegativeTest(CampaignStatus status) {
        assertThrows(DataValidateException.class,
                () -> validator.statusByCreateValidation(status),
                "When created, the status can only be ACTIVE");
    }
}
