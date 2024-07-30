package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.stage.StageRoles;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class ValidatorTest {
    private Validator validator;
    private StageDto stageDto;
    private StageRoles stageRoles;

    @BeforeEach
    void init() {
        validator = new Validator();
        stageDto = new StageDto();
        stageRoles = new StageRoles();
        stageDto.setExecutorIds(List.of(1L, 2L, 3L));
        stageDto.setProject(new Project());
        stageDto.setStageName("planning");
        stageDto.setStageRoles(List.of(new StageRoles()));
        stageRoles.setCount(0);
    }

    @Test
    void shouldReturnFalseWithoutProjectWhenValidateInputStageDataTest() {
        stageDto.setProject(null);

        Assertions.assertFalse(validator.validateInputStageData(stageDto));
    }

    @Test
    void shouldReturnFalseWithoutStageNameWhenValidateInputStageDataTest() {
        stageDto.setStageName("");

        Assertions.assertFalse(validator.validateInputStageData(stageDto));
    }

    @Test
    void shouldReturnFalseWithoutStageRolesWhenValidateInputStageDataTest() {
        stageDto.setStageRoles(List.of());

        Assertions.assertFalse(validator.validateInputStageData(stageDto));
    }

    @Test
    void shouldReturnFalseWithStageRolesCountNullWhenValidateInputStageDataTest() {
        stageDto.setStageRoles(List.of(stageRoles));

        Assertions.assertFalse(validator.validateInputStageData(stageDto));
    }

    @Test
    void shouldReturnTrueWhenValidateInputStageDataTest() {
        stageRoles.setCount(1);
        stageDto.setStageRoles(List.of(stageRoles));

        Assertions.assertTrue(validator.validateInputStageData(stageDto));
    }
}