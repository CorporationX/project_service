package faang.school.projectservice.dto.project.stage;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StageUpdateDtoValidationTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("Success dto validation")
    void StageUpdateDtoValidationTest_successValidation() {
        StageUpdateDto stageUpdateDto = StageUpdateDto.builder()
                .executorIds(new ArrayList<>())
                .build();

        Set<ConstraintViolation<StageUpdateDto>> violations = validator.validate(stageUpdateDto);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Fail dto validation with null executorIds")
    void StageUpdateDtoValidationTest_failValidationWithNullExecutorIds() {
        StageUpdateDto stageUpdateDto = StageUpdateDto.builder()
                .build();

        Set<ConstraintViolation<StageUpdateDto>> violations = validator.validate(stageUpdateDto);

        assertEquals(1, violations.size());
        assertEquals("Executor ids is required", violations.iterator().next().getMessage());
    }
}
