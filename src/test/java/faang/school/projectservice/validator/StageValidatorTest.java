package faang.school.projectservice.validator;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StageValidatorTest {

    private final StageValidator stageValidator = new StageValidator();

    @Test
    void testIsExecutorExistWithExistingRoleReturnTrue() {
        boolean result = stageValidator.isExecutorExist(setUpStage(), TeamRole.DEVELOPER.toString());

        assertTrue(result);
    }

    @Test
    void testIsExecutorExistWithNotExistingRoleReturnFalse() {
        boolean result = stageValidator.isExecutorExist(setUpStage(), TeamRole.INTERN.toString());

        assertFalse(result);
    }

    private Stage setUpStage() {
        TeamMember member1 = new TeamMember();
        member1.setRoles(List.of(TeamRole.DEVELOPER));
        TeamMember member2 = new TeamMember();
        member2.setRoles(List.of(TeamRole.DESIGNER));
        Stage stage = new Stage();
        stage.setExecutors(List.of(member1, member2));
        return stage;
    }
}
