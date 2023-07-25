package faang.school.projectservice.service.internship;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class InternshipServiceTest {
    @Mock
    private InternshipRepository internshipRepository;
    @InjectMocks
    private InternshipService service;

//    @Test
//    public void createInternshipNoInternsTest() {
//        DataValidationException exception = assertThrows(DataValidationException.class,
//                () -> service.createInternship(new Internship()));
//        assertEquals(exception.getMessage(), "No interns!");
//    }
//
//    @Test
//    public void createInternshipDurationTest() {
//        DataValidationException exception = assertThrows(DataValidationException.class,
//                () -> service.createInternship(Internship.builder().interns(List.of(mock(TeamMember.class)))
//                        .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(4, ChronoUnit.MONTHS)).build()));
//        assertEquals(exception.getMessage(), "Internship's duration is too long!");
//    }
//
//    @Test
//    public void createInternshipHasMentorTest() {
//        DataValidationException exception = assertThrows(DataValidationException.class,
//                () -> service.createInternship(Internship.builder().interns(List.of(mock(TeamMember.class)))
//                        .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(3, ChronoUnit.MONTHS))
//                        .mentorId(null).build()));
//        assertEquals(exception.getMessage(), "Internship has no mentor!");
//    }
//
//    @Test
//    public void createInternshipTest() {
//        Internship internship = Internship.builder().interns(List.of(mock(TeamMember.class)))
//                .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(3, ChronoUnit.MONTHS))
//                .mentorId(mock(TeamMember.class)).build();
//        Internship res = service.createInternship(internship);
//        Mockito.verify(internshipRepository).save(internship);
//    }
}
