package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class StageRoleFilterTest {
    StageRoleFilter stageRoleFilter = new StageRoleFilter();

    @Test
    void testIsNotApplicable() {
        StageFilterDto filterDto = StageFilterDto.builder().build();

        assertFalse(stageRoleFilter.isApplicable(filterDto));
    }

    @Test
    void testIsApplicable() {
        StageFilterDto filterDto = StageFilterDto.builder().role(TeamRole.MANAGER).build();

        assertTrue(stageRoleFilter.isApplicable(filterDto));
    }

    @Test
    void applyOk() {
        Stage stage = Stage
                .builder()
                .stageRoles(List.of(StageRoles.builder().teamRole(TeamRole.MANAGER).build()))
                .build();

        StageFilterDto filter = StageFilterDto.builder().role(TeamRole.MANAGER).build();

        assertEquals(1, stageRoleFilter.apply(Stream.of(stage), filter).count());
    }
}
