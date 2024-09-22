package faang.school.projectservice.dto.project.stage;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StageRoleDtoValidationTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("Success dto validation")
    void stageRoleDtoValidationTest_successDtoValidation() {
        StageRoleDto stageRoleDto = StageRoleDto.builder()
                .teamRole(TeamRole.ANALYST)
                .count(1)
                .build();

        Set<ConstraintViolation<StageRoleDto>> violations = validator.validate(stageRoleDto);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Fail validation with null teamRole")
    void stageRoleDtoValidationTest_failValidationWithNullTeamRole() {
        StageRoleDto stageRoleDto = StageRoleDto.builder()
                .count(1)
                .build();

        Set<ConstraintViolation<StageRoleDto>> violations = validator.validate(stageRoleDto);

        assertEquals(1, violations.size());
        assertEquals("Role name is required", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Fail validation with null count")
    void stageRoleDtoValidationTest_failValidationWithNullCount() {
        StageRoleDto stageRoleDto = StageRoleDto.builder()
                .teamRole(TeamRole.ANALYST)
                .build();

        Set<ConstraintViolation<StageRoleDto>> violations = validator.validate(stageRoleDto);

        assertEquals(1, violations.size());
        assertEquals("Count for member role is required", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Fail validation with non positive count")
    void stageRoleDtoValidationTest_failValidationWithNonPositiveCount() {
        StageRoleDto stageRoleDto = StageRoleDto.builder()
                .teamRole(TeamRole.ANALYST)
                .count(-1)
                .build();

        Set<ConstraintViolation<StageRoleDto>> violations = validator.validate(stageRoleDto);

        assertEquals(1, violations.size());
        assertEquals("Count for member role must be positive", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Fail validation with null teamRole and null count")
    void stageRoleDtoValidationTest_failValidationWithNullTeamRoleAndNullCount() {
        StageRoleDto stageRoleDto = StageRoleDto.builder()
                .build();

        Set<ConstraintViolation<StageRoleDto>> violations = validator.validate(stageRoleDto);

        assertEquals(2, violations.size());
        assertTrue(violations.stream()
                .map(ConstraintViolation::getMessage)
                .toList().containsAll(List.of("Role name is required", "Count for member role is required")));
    }
}
