package faang.school.projectservice.dto.project.stage;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StageRoleDtoValidationTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
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
}
