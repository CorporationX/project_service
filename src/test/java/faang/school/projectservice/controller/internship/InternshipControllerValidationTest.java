package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.testData.internship.InternshipTestData;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static faang.school.projectservice.controller.internship.InternshipControllerValidation.INTERNSHIP_MAX_DURATION_IN_MONTHS;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InternshipControllerValidationTest {
    private final InternshipControllerValidation internshipControllerValidation = new InternshipControllerValidation();
    private InternshipDto internshipDto;
    private Validator validator;

    @BeforeEach
    void setUp() {
        InternshipTestData internshipTestData = new InternshipTestData();
        internshipDto = internshipTestData.getInternshipDto();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    class PositiveTests {
        @DisplayName("shouldn't throw DataValidationException " +
                "when the passed internship has a duration of less than or equal to " + INTERNSHIP_MAX_DURATION_IN_MONTHS + " months")
        @Test
        void validateInternshipDurationTest() {
            assertDoesNotThrow(() -> internshipControllerValidation.validateInternshipDuration(internshipDto));
        }

        @DisplayName("shouldn't throw exception when the passed dto has valid fields")
        @Test
        void validInternshipDtoTest() {
            Set<ConstraintViolation<InternshipDto>> violations = validator.validate(internshipDto);

            assertEquals(0, violations.size());
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("should throw DataValidationException " +
                "when the passed internship has a duration of more than " + INTERNSHIP_MAX_DURATION_IN_MONTHS + " months")
        @Test
        void validateInternshipDurationTest() {
            internshipDto.setEndDate(LocalDateTime.of(2024, 12, 31, 16, 0));

            assertThrows(DataValidationException.class, () -> internshipControllerValidation.validateInternshipDuration(internshipDto));
        }

        @DisplayName("should violate validation when passed dto has empty name")
        @ParameterizedTest
        @EmptySource
        @NullSource
        @ValueSource(strings = {" ", "\t", "\n"})
        void invalidNameTest(String name) {
            internshipDto.setName(name);

            Set<ConstraintViolation<InternshipDto>> violations = validator.validate(internshipDto);

            assertEquals(1, violations.size());
            assertEquals("The internship must have a name.", violations.iterator().next().getMessage());
        }

        @DisplayName("should violate validation when passed dto has null projectId")
        @ParameterizedTest
        @NullSource
        void nullProjectIdTest(Long projectId) {
            internshipDto.setProjectId(projectId);

            Set<ConstraintViolation<InternshipDto>> violations = validator.validate(internshipDto);

            assertEquals(1, violations.size());
            assertEquals("The internship must be referred to a specific project.",
                    violations.iterator().next().getMessage());
        }

        @DisplayName("should violate validation when passed dto has null mentorId")
        @ParameterizedTest
        @NullSource
        void nullMentorIdTest(Long mentorId) {
            internshipDto.setMentorId(mentorId);

            Set<ConstraintViolation<InternshipDto>> violations = validator.validate(internshipDto);

            assertEquals(1, violations.size());
            assertEquals("The internship must have a mentor.",
                    violations.iterator().next().getMessage());
        }

        @DisplayName("should violate validation when passed dto has empty internsIds list")
        @ParameterizedTest
        @EmptySource
        void emptyInternsIdsListTest(List<Long> internsIds) {
            internshipDto.setInternsIds(internsIds);

            Set<ConstraintViolation<InternshipDto>> violations = validator.validate(internshipDto);

            assertEquals(1, violations.size());
            assertEquals("The internship can only be created with interns.",
                    violations.iterator().next().getMessage());
        }

        @DisplayName("should violate validation when passed dto has null internsIds")
        @ParameterizedTest
        @NullSource
        void emptyInternsIdsListTest(Long internId) {
            var internIds = new ArrayList<Long>();
            internIds.add(internId);
            internshipDto.setInternsIds(internIds);

            Set<ConstraintViolation<InternshipDto>> violations = validator.validate(internshipDto);

            assertEquals(1, violations.size());
            assertEquals("Cannot process null-valued intern id.",
                    violations.iterator().next().getMessage());
        }

        @DisplayName("should violate validation when passed dto has past endDate")
        @Test
        void pastEndDateTest() {
            internshipDto.setEndDate(LocalDateTime.of(2023, 12, 31, 16, 0));

            Set<ConstraintViolation<InternshipDto>> violations = validator.validate(internshipDto);

            assertEquals(1, violations.size());
            assertEquals("The internship cannot be created in the past.",
                    violations.iterator().next().getMessage());
        }

        @DisplayName("should violate validation when passed dto has null status")
        @ParameterizedTest
        @NullSource
        void nullStatusTest(InternshipStatus status) {
            internshipDto.setStatus(status);

            Set<ConstraintViolation<InternshipDto>> violations = validator.validate(internshipDto);

            assertEquals(1, violations.size());
            assertEquals("The internship must have a status.",
                    violations.iterator().next().getMessage());
        }
    }
}