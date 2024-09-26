package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.filter.StageFilterDto;
import faang.school.projectservice.filter.ExecutorsRoleFilter;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

class ExecutorsRoleFilterTest {
    private Stream<Stage> stageStream;
    private StageFilterDto filterDto = new StageFilterDto();
    ExecutorsRoleFilter roleFilter;

    @BeforeEach
    void init() {
        roleFilter = new ExecutorsRoleFilter();
    }


    public List<Stage> initStages() {//возвращает массив Stage, чтобы можно было что то с ними сделать
        Stage stage1 = new Stage();
        Stage stage2 = new Stage();
        Stage stage3 = new Stage();

        StageRoles roles1 = new StageRoles();
        StageRoles roles2 = new StageRoles();
        StageRoles roles3 = new StageRoles();

        roles1.setTeamRole(TeamRole.TESTER);
        roles2.setTeamRole(TeamRole.DEVELOPER);
        roles3.setTeamRole(TeamRole.DESIGNER);

        stage1.setStageRoles(List.of(roles1, roles2));
        stage2.setStageRoles(List.of(roles2, roles3));
        stage3.setStageRoles(List.of(roles3));

        stageStream = Stream.of(stage1, stage2, stage3);

        return List.of(stage1, stage2, stage3);
    }
    @Test
    void apply_whenOk() {
        List<Stage> stages = new ArrayList<>(initStages());

        filterDto.setRole(TeamRole.DEVELOPER);

        Stream<Stage> stageStream1 = roleFilter.apply(stageStream, filterDto);


        stages.remove(2);
        Assertions.assertEquals(stageStream1.toList(), stages);
    }

    @Test
    void isApplicable_whenOk() {
        filterDto.setRole(null);
        Assertions.assertFalse(roleFilter.isApplicable(filterDto));

        filterDto.setRole(TeamRole.DESIGNER);
        Assertions.assertTrue(roleFilter.isApplicable(filterDto));
    }
}
