package faang.school.projectservice.validator;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageRolesDto;
import faang.school.projectservice.exceptions.stage.StageHaveNoRolesException;
import faang.school.projectservice.exceptions.stage.StageNotHaveNameException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class StageControllerValidatorTest {
    StageControllerValidator validator;
    StageDto stageDto;

    @BeforeEach
    void init() {
        validator = new StageControllerValidator();
    }

    @Test
    void validateStageDto_whenNameNull() {
        stageDto = new StageDto(1L, null, 1L, List.of());

        Assertions.assertThrows(StageNotHaveNameException.class, () -> validator.validateStageDto(stageDto));
    }

    @Test
    void validateStageDto_whenRolesEmpty() {
        stageDto = new StageDto(1L, "name", 1L, List.of());

        Assertions.assertThrows(StageHaveNoRolesException.class, () -> validator.validateStageDto(stageDto));
    }

    @Test
    void validateId_whenNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateId(null));
    }
}
