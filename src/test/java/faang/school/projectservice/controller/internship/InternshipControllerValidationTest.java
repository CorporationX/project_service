package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.InternshipStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.List;

import static faang.school.projectservice.exception.InternshipValidationExceptionMessage.EMPTY_INTERNSHIP_NAME_EXCEPTION;
import static faang.school.projectservice.exception.InternshipValidationExceptionMessage.NULL_DTO_EXCEPTION;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InternshipControllerValidationTest {
    private final InternshipControllerValidation internshipControllerValidation = new InternshipControllerValidation();
    private InternshipDto internshipDto;

    @BeforeEach
    void init() {
        internshipDto = InternshipDto.builder()
                .name("Faang internship")
                .description("The coolest inthernship ever")
                .projectId(1L)
                .mentorId(1L)
                .internsIds(List.of(1L, 2L, 3L))
                .startDate(LocalDateTime.of(2024, 6, 1, 8, 0))
                .endDate(LocalDateTime.of(2024, 8, 31, 16, 0))
                .status(InternshipStatus.IN_PROGRESS)
                .build();
    }

    @Nested
    class PositiveTests {
        @DisplayName("shouldn't throw DataValidationException when valid dto is passed")
        @Test
        void shouldNotThrowExceptionWhenValidDtoIsPassed() {
            assertDoesNotThrow(() -> internshipControllerValidation.validationDto(internshipDto));
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("should throw DataValidationException when passed dto is null")
        @Test
        void shouldThrowDataValidationExceptionWhenPassedDtoIsNull() {
            var thrownException = assertThrows(DataValidationException.class,
                    () -> internshipControllerValidation.validationDto(null));

            assertEquals(NULL_DTO_EXCEPTION.getMessage(), thrownException.getMessage());
        }

        @DisplayName("should throw DataValidationException when passed dto has empty name")
        @ParameterizedTest
        @EmptySource
        @NullSource
        @ValueSource(strings = {" ", "\t", "\n"})
        void shouldThrowDataValidationExceptionWhenPassedDtoHasEmptyName(String name) {
            internshipDto.setName(name);

            var thrownException = assertThrows(DataValidationException.class,
                    () -> internshipControllerValidation.validationDto(internshipDto));

            assertEquals(EMPTY_INTERNSHIP_NAME_EXCEPTION.getMessage(), thrownException.getMessage());
        }

        @DisplayName("should throw DataValidationException when passed dto has empty projectId")
        @ParameterizedTest
        @EmptySource
        @NullSource
        @ValueSource(strings = {" ", "\t", "\n"})
        void shouldThrowDataValidationExceptionWhenPassedDtoHasEmptyProjectId(String name) {
            internshipDto.setName(name);

            var thrownException = assertThrows(DataValidationException.class,
                    () -> internshipControllerValidation.validationDto(internshipDto));

            assertEquals(EMPTY_INTERNSHIP_NAME_EXCEPTION.getMessage(), thrownException.getMessage());
        }
    }
}