package faang.school.projectservice.dto.project.stage;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RemoveTypeDtoValidationTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("Success dto validation")
    void removeTypeDtoValidationTest_successValidation() {
        RemoveTypeDto removeTypeDto = RemoveTypeDto.builder()
                .removeStrategy(RemoveStrategy.CLOSE)
                .build();

        Set<ConstraintViolation<RemoveTypeDto>> violations = validator.validate(removeTypeDto);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Fail dto validation with null removeStrategy")
    void removeTypeDtoValidationTest_failValidationWithNullRemoveStrategy() {
        RemoveTypeDto removeTypeDto = RemoveTypeDto.builder()
                .build();

        Set<ConstraintViolation<RemoveTypeDto>> violations = validator.validate(removeTypeDto);

        assertEquals(1, violations.size());
        assertEquals("Remove strategy is required", violations.iterator().next().getMessage());
    }
}
