package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static faang.school.projectservice.model.TeamRole.ANALYST;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        List<StageRoles> stageRoles = List.of(StageRoles.builder().teamRole(ANALYST).build());
        Stage stage = Stage.builder().stageRoles(stageRoles).build();
        Stream<Stage> stageStream = List.of(stage).stream();

        //Act
        List<Stage> result = stageTeamRoleFilter.apply(stageStream, filter).toList();

        //Assert
        assertTrue(result.contains(stage));
    }
}