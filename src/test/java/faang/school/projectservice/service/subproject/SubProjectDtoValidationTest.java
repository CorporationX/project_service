package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.validation.CreateGroup;
import faang.school.projectservice.dto.validation.UpdateGroup;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
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

public class SubProjectDtoValidationTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Create subProject success validation")
    public void testSubProjectDtoCreateValidationSuccess() {
        SubProjectDto subProjectDto = SubProjectDto.builder()
                .name("test")
                .description("test description")
                .parentProjectId(1L)
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        Set<ConstraintViolation<SubProjectDto>> violations = validator.validate(subProjectDto, CreateGroup.class);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Create subProject fail validation")
    public void testSubProjectDtoCreateValidationFail() {
        SubProjectDto subProjectDto = SubProjectDto.builder()
                .name(null)
                .description(" ")
                .parentProjectId(0L)
                .ownerId(-1L)
                .build();

        Set<ConstraintViolation<SubProjectDto>> violations = validator.validate(subProjectDto, CreateGroup.class);

        assertEquals(4, violations.size());
    }

    @Test
    @DisplayName("Update subProject success validation")
    public void testSubProjectDtoUpdateValidationSuccess() {
        SubProjectDto subProjectDto = SubProjectDto.builder()
                .id(1L)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        Set<ConstraintViolation<SubProjectDto>> violations = validator.validate(subProjectDto, UpdateGroup.class);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Update subProject fail validation")
    public void testSubProjectDtoUpdateValidationFail() {
        SubProjectDto subProjectDto = SubProjectDto.builder()
                .id(null)
                .status(null)
                .visibility(null)
                .build();

        Set<ConstraintViolation<SubProjectDto>> violations = validator.validate(subProjectDto, UpdateGroup.class);

        assertEquals(3, violations.size());
    }
}
