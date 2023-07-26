package faang.school.projectservice;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.service.InternshipService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class InternshipServiceTest {
    @Mock
    private InternshipRepository internshipRepository;

    @InjectMocks
    private InternshipService internshipService;

    @Test
    public void createInternshipNoInternsThrowExceptionTest() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> internshipService.saveNewInternship(new InternshipDto()));
        assertEquals(exception.getMessage(), "Can't create an internship without interns");
    }

    @Test
    public void createInternshipDoesNotHaveMentorTest() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> internshipService.saveNewInternship(InternshipDto.builder()
                        .internsId(List.of(10L))
                        .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(2, ChronoUnit.MONTHS))
                        .mentorId(null).build()));
        assertEquals(exception.getMessage(), "There is not mentor for interns!");
    }

    @Test
    public void createInternshipDateMoreThan3MonthsTest() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> internshipService.saveNewInternship(InternshipDto.builder()
                        .internsId(List.of(10L))
                        .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(4, ChronoUnit.MONTHS))
                        .mentorId(1L).build()));
        assertEquals(exception.getMessage(), "Internship cannot last more than 3 months");
    }
}