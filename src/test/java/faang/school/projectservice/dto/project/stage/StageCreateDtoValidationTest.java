package faang.school.projectservice.dto.project.stage;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StageCreateDtoValidationTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("Success dto validation")
    void stageCreateDtoValidationTest_successValidation() {
        StageCreateDto stageCreateDto = StageCreateDto.builder()
                .stageName("test")
                .projectId(1L)
                .roles(List.of(
                        StageRoleDto.builder()
                                .teamRole(TeamRole.ANALYST)
                                .count(1)
                                .build())
                )
                .build();

        Set<ConstraintViolation<StageCreateDto>> violations = validator.validate(stageCreateDto);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Fail dto validation with null name")
    void stageCreateDtoValidationTest_failValidationWithNullName() {
        StageCreateDto stageCreateDto = StageCreateDto.builder()
                .stageName(null)
                .projectId(1L)
                .roles(List.of(
                        StageRoleDto.builder()
                                .teamRole(TeamRole.ANALYST)
                                .count(1)
                                .build())
                )
                .build();

        Set<ConstraintViolation<StageCreateDto>> violations = validator.validate(stageCreateDto);

        assertEquals(1, violations.size());
        assertEquals("Stage name is required, and can't be blank", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Fail dto validation with empty name")
    void stageCreateDtoValidationTest_failValidationWithEmptyName() {
        StageCreateDto stageCreateDto = StageCreateDto.builder()
                .stageName("")
                .projectId(1L)
                .roles(List.of(
                        StageRoleDto.builder()
                                .teamRole(TeamRole.ANALYST)
                                .count(1)
                                .build())
                )
                .build();

        Set<ConstraintViolation<StageCreateDto>> violations = validator.validate(stageCreateDto);

        assertEquals(1, violations.size());
        assertEquals("Stage name is required, and can't be blank", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Fail dto validation with blank name")
    void stageCreateDtoValidationTest_failValidationWithBlankName() {
        StageCreateDto stageCreateDto = StageCreateDto.builder()
                .stageName("   ")
                .projectId(1L)
                .roles(List.of(
                        StageRoleDto.builder()
                                .teamRole(TeamRole.ANALYST)
                                .count(1)
                                .build())
                )
                .build();

        Set<ConstraintViolation<StageCreateDto>> violations = validator.validate(stageCreateDto);

        assertEquals(1, violations.size());
        assertEquals("Stage name is required, and can't be blank", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Fail dto validation with null project id")
    void stageCreateDtoValidationTest_failValidationWithNullProjectId() {
        StageCreateDto stageCreateDto = StageCreateDto.builder()
                .stageName("test")
                .projectId(null)
                .roles(List.of(
                        StageRoleDto.builder()
                                .teamRole(TeamRole.ANALYST)
                                .count(1)
                                .build())
                )
                .build();

        Set<ConstraintViolation<StageCreateDto>> violations = validator.validate(stageCreateDto);

        assertEquals(1, violations.size());
        assertEquals("Project id is required", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Fail dto validation with non positive project id")
    void stageCreateDtoValidationTest_failValidationWithNonPositiveProjectId() {
        StageCreateDto stageCreateDto = StageCreateDto.builder()
                .stageName("test")
                .projectId(-1L)
                .roles(List.of(
                        StageRoleDto.builder()
                                .teamRole(TeamRole.ANALYST)
                                .count(1)
                                .build())
                )
                .build();

        Set<ConstraintViolation<StageCreateDto>> violations = validator.validate(stageCreateDto);

        assertEquals(1, violations.size());
        assertEquals("Project id must be positive", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Fail dto validation with null roles")
    void stageCreateDtoValidationTest_failValidationWithNullRoles() {
        StageCreateDto stageCreateDto = StageCreateDto.builder()
                .stageName("test")
                .projectId(1L)
                .roles(null)
                .build();

        Set<ConstraintViolation<StageCreateDto>> violations = validator.validate(stageCreateDto);

        assertEquals(1, violations.size());
        assertEquals("Stage roles is required", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Fail dto validation with empty roles")
    void stageCreateDtoValidationTest_failValidationWithEmptyRoles() {
        StageCreateDto stageCreateDto = StageCreateDto.builder()
                .stageName("test")
                .projectId(1L)
                .roles(new ArrayList<>())
                .build();

        Set<ConstraintViolation<StageCreateDto>> violations = validator.validate(stageCreateDto);

        assertEquals(1, violations.size());
        assertEquals("Stage roles is required", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Fail dto validation with not valid role")
    void stageCreateDtoValidationTest_failValidationWithNotValidRole() {
        StageCreateDto stageCreateDto = StageCreateDto.builder()
                .stageName("test")
                .projectId(1L)
                .roles(List.of(
                        StageRoleDto.builder()
                                .teamRole(null)
                                .count(1)
                                .build())
                )
                .build();

        Set<ConstraintViolation<StageCreateDto>> violations = validator.validate(stageCreateDto);

        assertEquals(1, violations.size());
        assertEquals("Role name is required", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Fail dto validation with null name, null project id, null roles")
    void StageCreateDtoValidationTest_failValidationWithNullArguments() {
        StageCreateDto stageCreateDto = StageCreateDto.builder()
                .stageName(null)
                .projectId(null)
                .roles(null)
                .build();

        Set<ConstraintViolation<StageCreateDto>> violations = validator.validate(stageCreateDto);

        assertEquals(3, violations.size());
        assertTrue(violations.stream().
                map(ConstraintViolation::getMessage)
                .toList()
                .containsAll(List.of("Stage name is required, and can't be blank",
                        "Project id is required",
                        "Stage roles is required")));
    }
}
