package faang.school.projectservice.validator;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageRolesDto;
import faang.school.projectservice.dto.TeamMemberDto;
import faang.school.projectservice.exceptions.stage.StageNotHaveProjectException;
import faang.school.projectservice.model.TeamRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class StageServiceValidatorTest {
    StageServiceValidator validator;
    StageDto stageDto;
    List<TeamMemberDto> memberDtos;

    @BeforeEach
    void init() {
        validator = new StageServiceValidator();
    }

    private void initStageDto() {
        List<StageRolesDto> stageRolesDtos = List.of(
                new StageRolesDto(1L, 2, TeamRole.DESIGNER),
                new StageRolesDto(2L, 2, TeamRole.DEVELOPER)
        );

        memberDtos = new ArrayList<>(List.of(
                new TeamMemberDto(1L, List.of(TeamRole.ANALYST, TeamRole.DESIGNER)),
                new TeamMemberDto(2L, List.of(TeamRole.ANALYST, TeamRole.DEVELOPER)),
                new TeamMemberDto(2L, List.of(TeamRole.ANALYST))
        ));

        stageDto =
                new StageDto(1L, "name", 1L, stageRolesDtos);
        stageDto.setExecutorsDtos(memberDtos);
    }

    @Test
    void validateExecutorsStageRoles_whenExecutorExcess() {
        initStageDto();
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> validator.validateExecutorsStageRoles(stageDto));
    }

    @Test
    void validateExecutorsStageRoles_whenOk() {
        initStageDto();
        memberDtos.remove(2);
        stageDto.setExecutorsDtos(memberDtos);

        Assertions.assertDoesNotThrow(() -> validator.validateExecutorsStageRoles(stageDto));
    }
}
