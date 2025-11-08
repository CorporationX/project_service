package faang.school.projectservice.validator.internship;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.Collections;

public class InternshipValidatorTest {

    private CreateInternshipDto createInternshipDto(LocalDateTime startDate, LocalDateTime endDate) {
        return new CreateInternshipDto(
                "Yandex",
                "I'm gay",
                InternshipStatus.IN_PROGRESS,
                TeamRole.DEVELOPER,
                startDate,
                endDate,
                1L,
                2L,
                Collections.singletonList(3L)
        );
    }

    @Test
    public void testValidateInternshipLength_Success() {
        CreateInternshipDto dto = createInternshipDto(
                LocalDateTime.now(),
                LocalDateTime.now().plusMonths(3)
        );
        Assertions.assertDoesNotThrow(() -> InternshipValidator.validateInternshipLengthDate(dto));
    }

    @Test
    public void testValidateInternshipLength_TooLong() {
        CreateInternshipDto dto = createInternshipDto(
                LocalDateTime.now(),
                LocalDateTime.now().plusMonths(4)
        );
        Assertions.assertThrows(DateTimeException.class,
                () -> InternshipValidator.validateInternshipLengthDate(dto));
    }

    @Test
    public void testValidateInternshipLength_NoEndDate() {
        CreateInternshipDto dto = createInternshipDto(
                LocalDateTime.now(),
                null
        );
        Assertions.assertDoesNotThrow(() -> InternshipValidator.validateInternshipLengthDate(dto));
    }

    @Test
    public void testValidateInternshipLength_BoundarySuccess() {
        CreateInternshipDto dto = createInternshipDto(
                LocalDateTime.now(),
                LocalDateTime.now().plusMonths(3).minusSeconds(1)
        );
        Assertions.assertDoesNotThrow(() -> InternshipValidator.validateInternshipLengthDate(dto));
    }

    @Test
    public void testValidateInternshipLength_Minimum() {
        CreateInternshipDto dto = createInternshipDto(
                LocalDateTime.now(),
                LocalDateTime.now().plusSeconds(1)
        );
        Assertions.assertDoesNotThrow(() -> InternshipValidator.validateInternshipLengthDate(dto));
    }
}