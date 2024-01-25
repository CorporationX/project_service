package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.TeamRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static faang.school.projectservice.model.TeamRole.ANALYST;
import static org.junit.jupiter.api.Assertions.*;

class StageTeamRoleFilterTest {

    private StageTeamRoleFilter stageTeamRoleFilter = new StageTeamRoleFilter();
    private StageFilterDto filter;

    @BeforeEach
    void setUp() {
        filter = StageFilterDto.builder().teamRole(ANALYST).build();

    }

    @Test
    void testIsApplicable() {
        //Act & Assert
        assertTrue(stageTeamRoleFilter.isApplicable(filter));
    }

    @Test
    void testApply() {
        //Arrange

        //Act

        //Assert
    }
}