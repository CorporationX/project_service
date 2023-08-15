package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.service.InternshipService;
import faang.school.projectservice.util.exception.DataValidationException;
import faang.school.projectservice.util.validator.InternshipControllerValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.xml.crypto.Data;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@ExtendWith(MockitoExtension.class)
public class InternshipControllerTest {

    @Mock
    private InternshipService internshipService;

    @Spy
    private InternshipControllerValidator validator;

    @Spy
    private UserContext userContext;

    @InjectMocks
    private InternshipController internshipController;

    @Test
    void create_StartDateAndEndDateAreAfterBeforeNow_ShouldThrowException() {
        DataValidationException e = Assertions.assertThrows(DataValidationException.class, () -> {
            internshipController.create(buildInternshipDto(() -> LocalDateTime.now().minusMonths(4),
                    () -> LocalDateTime.now().minusMonths(2)));
        });
        Assertions.assertEquals("Start date and end date must be in the future", e.getMessage());
    }

    @Test
    void create_StartDateIsAfterEndDate_ShouldThrowException() {
        DataValidationException e = Assertions.assertThrows(DataValidationException.class, () -> {
            internshipController.create(buildInternshipDto(() -> LocalDateTime.now().plusMonths(4),
                    () -> LocalDateTime.now().plusMonths(2)));
        });
        Assertions.assertEquals("Start date must be before end date", e.getMessage());
    }

    @Test
    void create_DurationIsLongerThan3Months_ShouldThrowException() {
        DataValidationException e = Assertions.assertThrows(DataValidationException.class, () -> {
            internshipController.create(buildInternshipDto(() -> LocalDateTime.now().plusMonths(4),
                    () -> LocalDateTime.now().plusMonths(8)));
        });
        Assertions.assertEquals("Internship duration must be less than 3 months", e.getMessage());
    }

    @Test
    void create_InternsAreEmpty_ShouldThrowException() {
        DataValidationException e = Assertions.assertThrows(DataValidationException.class, () -> {
            internshipController.create(buildInternshipDto(() -> LocalDateTime.now().plusMonths(2),
                    () -> LocalDateTime.now().plusMonths(4)));
        });
        Assertions.assertEquals("Same mentor and intern with id 1", e.getMessage());
    }

    private InternshipDto buildInternshipDto(Supplier<LocalDateTime> startDate,
                                             Supplier<LocalDateTime> endDate) {
        LocalDateTime startDate1 = startDate.get();
        LocalDateTime endDate1 = endDate.get();

        return InternshipDto.builder()
                .mentorId(1L)
                .projectId(1L)
                .name("Internship")
                .description("Internship")
                .startDate(startDate1)
                .internIds(List.of(1L, 2L))
                .endDate(endDate1)
                .build();
    }
}
