package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.request.CreationRequest;
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

public class CreationRequestValidationTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("CreationRequest success validation")
    public void testCreationRequestValidationSuccess() {
        CreationRequest creationRequest = CreationRequest.builder()
                .name("test")
                .description("test description")
                .parentProjectId(1L)
                .ownerId(1L)
                .visibility(ProjectVisibility.PUBLIC)
                .build();


        Set<ConstraintViolation<CreationRequest>> violations = validator.validate(creationRequest);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("CreationRequest fail validation")
    public void testCreationRequestValidationFail() {
        CreationRequest creationRequest = CreationRequest.builder()
                .name(null)
                .description(" ")
                .parentProjectId(0L)
                .ownerId(-1L)
                .visibility(null)
                .build();

        Set<ConstraintViolation<CreationRequest>> violations = validator.validate(creationRequest);

        assertEquals(5, violations.size());
    }
}
