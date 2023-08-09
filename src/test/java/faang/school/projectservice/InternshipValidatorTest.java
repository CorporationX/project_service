package faang.school.projectservice;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.validator.InternshipValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class InternshipValidatorTest {

    @InjectMocks
    private InternshipValidator internshipValidator;

    @Test
    public void internshipValidationThereIsNotInternshipTest() {
        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                internshipValidator.validateControllerInternship(null));
        assertEquals(exception.getMessage(), "There is not internship!");
    }

    @Test
    public void internshipValidationWithoutNameTest() {
        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                internshipValidator.validateControllerInternship(InternshipDto
                        .builder()
                        .name(null)
                        .build()));
        assertEquals(exception.getMessage(), "Internship name can not be blank or null!");
    }

    @Test
    public void internshipValidationWithWrongProjectIdTest() {
        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                internshipValidator.validateControllerInternship(InternshipDto
                        .builder()
                        .name("faang-internship")
                        .projectId(null)
                        .build()));
        assertEquals(exception.getMessage(), "Internship relation project error!");
    }

    @Test
    public void internshipValidationWithWrongDateTest() {
        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                internshipValidator.validateServiceSaveInternship(InternshipDto
                        .builder()
                        .name("best")
                        .projectId(1L)
                        .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(4, ChronoUnit.MONTHS))
                        .build()));
        assertEquals(exception.getMessage(), "Internship cannot last more than 3 months!");
    }

    @Test
    public void internshipValidationWithoutMentorTest() {
        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                internshipValidator.validateServiceSaveInternship(InternshipDto
                        .builder()
                        .name("best")
                        .projectId(1L)
                        .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(2, ChronoUnit.MONTHS))
                        .mentorId(null)
                        .build()));
        assertEquals(exception.getMessage(), "There is not mentor for internship!");
    }

    @Test
    public void internshipValidationWithoutInternsTest() {
        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                internshipValidator.validateServiceSaveInternship(InternshipDto
                        .builder()
                        .name("best")
                        .projectId(1L)
                        .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(3, ChronoUnit.MONTHS))
                        .mentorId(1L)
                        .internsId(null)
                        .build()));
        assertEquals(exception.getMessage(), "There is not interns for internship!");
    }

    @Test
    public void internshipValidationWithCOMPLITEDStatusTest() {
        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                internshipValidator.validateServiceUpdateInternship(Internship.builder()
                                .status(InternshipStatus.COMPLETED)
                                .build(),
                        InternshipDto.builder()
                        .name("best")
                        .projectId(1L)
                        .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(3, ChronoUnit.MONTHS))
                        .mentorId(1L)
                        .internsId(List.of(1L))
                        .build()));
        assertEquals(exception.getMessage(), "Internship is over!");
    }

    @Test
    public void internshipValidationWithCOMPDStatusTest() {
        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                internshipValidator.validateServiceUpdateInternship(Internship.builder()
                                .status(InternshipStatus.IN_PROGRESS)
                                .build(),
                        InternshipDto.builder()
                                .name("best")
                                .projectId(1L)
                                .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(3, ChronoUnit.MONTHS))
                                .mentorId(1L)
                                .internsId(List.of(1L))
                                .build()));
        assertEquals(exception.getMessage(), "Can't add new interns!");
    }
}
