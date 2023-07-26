package faang.school.projectservice;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.service.InternshipService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
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

    @Mock
    private InternshipMapper internshipMapper;

    @InjectMocks
    private InternshipService internshipService;

//    @Test
//    public void createInternshipNoInternsTest() {
//        DataValidationException exception = assertThrows(DataValidationException.class,
//                () -> internshipService.saveNewInternship(new InternshipDto()));
//        assertEquals(exception.getMessage(), "No interns!");
//    }

    @Test
    public void saveNewInternshipTest() {
        Internship internship = Internship.builder().interns(List.of(mock(TeamMember.class)))
                .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(3, ChronoUnit.MONTHS))
                .mentorId(mock(TeamMember.class)).build();
        internshipService.saveNewInternship(new InternshipDto());
        Mockito.verify(internshipRepository).save(internship);
    }
}