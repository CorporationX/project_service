package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.filter.stage.StageFilterDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StageTeamRolesFilterTest {

    private static final TeamRole DESIGNER = TeamRole.DESIGNER;
    private static final TeamRole ANALYST = TeamRole.ANALYST;
    private static final List<TeamRole> TEAM_ROLES = new ArrayList<>();
    @InjectMocks
    private StageTeamRolesFilter stageTeamRolesFilter;
    private StageFilterDto stageFilterDto;

    @BeforeEach
    void init() {
        TEAM_ROLES.add(DESIGNER);
        TEAM_ROLES.add(ANALYST);

        stageFilterDto = StageFilterDto.builder()
                .teamRoles(TEAM_ROLES)
                .build();
    }

    @Nested
    class PositiveTest {
        @Test
        @DisplayName("Возвращаем положительный результат")
        void whenValidateIsApplicableThenReturnTrue() {
            assertTrue(stageTeamRolesFilter.isApplicable(stageFilterDto));
        }

        @Test
        @DisplayName("Возвращаем отфильтрованый список")
        void whenFilterThenReturn() {
            StageRoles stageRolesOne = new StageRoles();
            stageRolesOne.setTeamRole(DESIGNER);

            StageRoles stageRolesOTwo = new StageRoles();
            stageRolesOTwo.setTeamRole(ANALYST);

            List<StageRoles> stageRoles = new ArrayList<>(List.of(stageRolesOne, stageRolesOTwo));

            Stage stageOne = Stage.builder()
                    .stageRoles(stageRoles)
                    .build();

            Stage stageTwo = Stage.builder()
                    .stageRoles(stageRoles)
                    .build();

            Stream<Stage> stageStream = Stream.of(stageOne, stageTwo);

            Stream<Stage> result = stageTeamRolesFilter.applyFilter(stageStream, stageFilterDto);

            assertNotNull(result);
            assertEquals(result.count(), 2);
        }
    }

    @Nested
    class NegativeTest {
        @Test
        @DisplayName("Возвращаем отрицательный результат")
        void whenValidateIsApplicableThenReturnFalse() {
            stageFilterDto = StageFilterDto.builder()
                    .teamRoles(null)
                    .build();

            assertFalse(stageTeamRolesFilter.isApplicable(stageFilterDto));

            stageFilterDto = StageFilterDto.builder()
                    .teamRoles(new ArrayList<>())
                    .build();

            assertFalse(stageTeamRolesFilter.isApplicable(stageFilterDto));
        }
    }
}