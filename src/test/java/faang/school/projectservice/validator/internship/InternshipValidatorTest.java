package faang.school.projectservice.validator.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamMember;
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
    private InternshipValidator validator;

    @Test
    public void internshipControllerValidation_ThrowsDataValidationException_nullInternship_Test() {
        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                validator.internshipControllerValidation(null));
        assertEquals(exception.getMessage(), "Internship is null!");
    }

    @Test
    public void internshipControllerValidation_ThrowsDataValidationException_nullBlankName_Test() {
        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                validator.internshipControllerValidation(InternshipDto.builder().name("").build()));
        assertEquals(exception.getMessage(), "Internship name can not be blank or null!");
    }

    @Test
    public void internshipControllerValidation_ThrowsDataValidationException_noInterns_Test() {
        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                validator.internshipControllerValidation(InternshipDto.builder().name("X").build()));
        assertEquals(exception.getMessage(), "Internship relation project error!");
    }

    @Test
    public void internshipServiceValidation_Duration_ThrowsDataValidationException_Test() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> validator.internshipServiceValidation(InternshipDto.builder().interns(List.of(1L))
                        .startDate(LocalDateTime.now())
                        .endDate(LocalDateTime.now().plus(4, ChronoUnit.MONTHS)).build()));
        assertEquals(exception.getMessage(), "Internship's duration is too long!");
    }

    @Test
    public void updateInternshipServiceValidation_COMPLETED_ThrowsDataValidationException_Test() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> validator.updateInternshipServiceValidation(Internship.builder()
                                .status(InternshipStatus.COMPLETED).build(),
                        InternshipDto.builder().interns(List.of(1L))
                                .startDate(LocalDateTime.now())
                                .endDate(LocalDateTime.now().plus(3, ChronoUnit.MONTHS)).build()));
        assertEquals(exception.getMessage(), "Internship already over!");
    }

    @Test
    public void updateInternshipServiceValidation_newIntern_ThrowsDataValidationException_Test() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> validator.updateInternshipServiceValidation(Internship.builder()
                                .status(InternshipStatus.IN_PROGRESS)
                        .interns(List.of(TeamMember.builder().build())).build(),
                        InternshipDto.builder().interns(List.of(1L))
                                .startDate(LocalDateTime.now())
                                .endDate(LocalDateTime.now().plus(3, ChronoUnit.MONTHS))
                                .interns(List.of(1L, 2L)).build()));
        assertEquals(exception.getMessage(), "Can't add new intern!");
    }
}
