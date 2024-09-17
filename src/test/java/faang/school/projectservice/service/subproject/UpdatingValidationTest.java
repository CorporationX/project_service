package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.request.UpdatingRequest;
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

public class UpdatingValidationTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Update subProject success validation")
    public void testSubProjectDtoUpdateValidationSuccess() {
        UpdatingRequest updatingRequest = UpdatingRequest.builder()
                .name("test")
                .description("test description")
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        Set<ConstraintViolation<UpdatingRequest>> violations = validator.validate(updatingRequest);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Update subProject fail validation")
    public void testSubProjectDtoUpdateValidationFail() {
        UpdatingRequest updatingRequest = UpdatingRequest.builder()
                .name("")
                .description(" ")
                .status(null)
                .visibility(null)
                .build();

        Set<ConstraintViolation<UpdatingRequest>> violations = validator.validate(updatingRequest);

        assertEquals(4, violations.size());
    }
}
