package faang.school.projectservice;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.InternshipService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InternshipServiceTest {
    @Mock
    private InternshipRepository internshipRepository;

    @Mock
    private InternshipMapper internshipMapper;

    @InjectMocks
    private InternshipService internshipService;

    @Test
    public void updateAlreadyOverInternshipTest() {
        InternshipDto internshipDto = InternshipDto.builder().interns(List.of(10L))
                .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(3, ChronoUnit.MONTHS))
                .mentorId(anyLong()).build();
        long id = 1;
        Internship old = Internship.builder().status(InternshipStatus.COMPLETED).build();

        when(internshipRepository.getById(id)).thenReturn(old);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> internshipService.updateInternship(internshipDto, id));
        assertEquals(exception.getMessage(), "Internship is over!");
    }

    @Test
    public void updateInternshipNewInternTest() {
        InternshipDto internshipDto = InternshipDto.builder()
                .interns(List.of(10L))
                .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(3, ChronoUnit.MONTHS))
                .mentorId(anyLong()).build();
        long id = 1;
        Internship old = Internship.builder().status(InternshipStatus.IN_PROGRESS).interns(List.of()).build();

        when(internshipRepository.getById(id)).thenReturn(old);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> internshipService.updateInternship(internshipDto, id));
        assertEquals(exception.getMessage(), "Cannot add interns!");
    }

    @Test
    public void updateInternshipTest() {
        InternshipDto internshipDto = InternshipDto.builder()
                .interns(List.of())
                .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(3, ChronoUnit.MONTHS))
                .mentorId(anyLong()).build();
        long id = 1;
        Internship oldInternship = Internship.builder().name("old internship").status(InternshipStatus.IN_PROGRESS).interns(List.of()).build();
        Internship internship = Internship.builder().name("new internship").status(InternshipStatus.IN_PROGRESS).interns(List.of()).build();

        when(internshipRepository.getById(id)).thenReturn(oldInternship);
        when(internshipMapper.toEntity(internshipDto)).thenReturn(internship);

        internshipService.updateInternship(internshipDto, id);
        Mockito.verify(internshipRepository).save(internship);
    }

    @Test
    public void updateInternshipToCOMPLETEDTest() {
        InternshipDto internshipDto = InternshipDto.builder()
                .interns(List.of())
                .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(2, ChronoUnit.MONTHS))
                .mentorId(anyLong())
                .internshipStatus(InternshipStatus.COMPLETED).build();
        long id = 1;
        Internship oldInternship = Internship.builder().name("old internship").status(InternshipStatus.IN_PROGRESS).interns(List.of()).build();
        Internship internship = Internship.builder().name("new internship").status(InternshipStatus.COMPLETED).interns(List.of()).build();

        when(internshipRepository.getById(id)).thenReturn(oldInternship);
        when(internshipMapper.toEntity(internshipDto)).thenReturn(internship);

        internshipService.updateInternship(internshipDto, id);
        Mockito.verify(internshipRepository).deleteById(id);
    }
}