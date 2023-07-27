package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.filter.internship.InternshipStatusFilter;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
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
    private InternshipRepository internshipRepository = Mockito.mock(InternshipRepository.class);;
    @Mock
    private TeamMemberRepository teamMemberRepository = Mockito.mock(TeamMemberRepository.class);
    @Mock
    private InternshipMapper internshipMapper = Mockito.mock(InternshipMapper.class);
    @InjectMocks
    private InternshipService service;

    @Test
    public void createInternshipNoInternsTest() {
        DataValidationException exception = assertThrows(DataValidationException.class, () -> service.createInternship(new InternshipDto()));
        assertEquals(exception.getMessage(), "No interns!");
    }

    @Test
    public void createInternshipDurationTest() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> service.createInternship(InternshipDto.builder().interns(List.of(1L))
                        .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(4, ChronoUnit.MONTHS)).build()));
        assertEquals(exception.getMessage(), "Internship's duration is too long!");
    }

    @Test
    public void createInternshipHasMentorTest() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> service.createInternship(InternshipDto.builder().interns(List.of(1L))
                        .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(3, ChronoUnit.MONTHS))
                        .mentorId(null).build()));
        assertEquals(exception.getMessage(), "Internship has no mentor!");
    }

    @Test
    public void createInternshipTest() {
        InternshipDto internshipDto = InternshipDto.builder().interns(List.of(1L))
                .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(3, ChronoUnit.MONTHS))
                .mentorId(anyLong()).build();
        InternshipDto res = service.createInternship(internshipDto);
        Mockito.verify(internshipRepository).save(internshipMapper.toEntity(internshipDto));
    }

    @Test
    public void updateAlreadyOverInternshipTest() {
        InternshipDto internshipDto = InternshipDto.builder().interns(List.of(1L))
                .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(3, ChronoUnit.MONTHS))
                .mentorId(anyLong()).build();
        long id = 1;
        Internship old = Internship.builder().status(InternshipStatus.COMPLETED).build();

        when(internshipRepository.getById(id)).thenReturn(old);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> service.updateInternship(id, internshipDto));
        assertEquals(exception.getMessage(), "Internship already over!");
    }

    @Test
    public void updateInternshipNewInternTest() {
        InternshipDto internshipDto = InternshipDto.builder()
                .interns(List.of(1L, 2L, 3L))
                .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(3, ChronoUnit.MONTHS))
                .mentorId(anyLong()).build();
        long id = 1;
        Internship old = Internship.builder().status(InternshipStatus.IN_PROGRESS).interns(List.of()).build();

        when(internshipRepository.getById(id)).thenReturn(old);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> service.updateInternship(id, internshipDto));
        assertEquals(exception.getMessage(), "Can't add new intern!");
    }

    @Test
    public void updateInternshipTest() {
        InternshipDto internshipDto = InternshipDto.builder()
                .interns(List.of())
                .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(3, ChronoUnit.MONTHS))
                .mentorId(anyLong()).build();
        long id = 1;
        Internship old = Internship.builder().name("OLD").status(InternshipStatus.IN_PROGRESS).interns(List.of()).build();
        Internship internship = Internship.builder().name("NEW").status(InternshipStatus.IN_PROGRESS).interns(List.of()).build();

        when(internshipRepository.getById(id)).thenReturn(old);
        when(internshipMapper.toEntity(internshipDto)).thenReturn(internship);

        service.updateInternship(id, internshipDto);
        Mockito.verify(internshipRepository).save(internship);
    }

    @Test
    public void updateInternshipToCOMPLETEDTest() {
        InternshipDto internshipDto = InternshipDto.builder()
                .interns(List.of())
                .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(2, ChronoUnit.MONTHS))
                .mentorId(anyLong())
                .status(InternshipStatus.COMPLETED).build();
        long id = 1;
        Internship old = Internship.builder().name("OLD").status(InternshipStatus.IN_PROGRESS).interns(List.of()).build();
        Internship internship = Internship.builder().name("NEW").status(InternshipStatus.COMPLETED).interns(List.of()).build();

        when(internshipRepository.getById(id)).thenReturn(old);
        when(internshipMapper.toEntity(internshipDto)).thenReturn(internship);

        service.updateInternship(id, internshipDto);
        Mockito.verify(internshipRepository).deleteById(id);
    }

    @Test
    public void findAllInternshipsTest() {
        Internship i1 = Internship.builder().name("A").build();
        Internship i2 = Internship.builder().name("B").build();
        List<Internship> list = List.of(i1, i2);
        when(internshipRepository.findAll()).thenReturn(list);
        when(internshipMapper.toDto(i1)).thenReturn(InternshipDto.builder().name("A").build());
        when(internshipMapper.toDto(i2)).thenReturn(InternshipDto.builder().name("B").build());
        List<InternshipDto> res = service.findAllInternships();

        assertEquals(res.get(0).getName(), "A");
        assertEquals(res.get(1).getName(), "B");
    }

    @Test
    public void internshipsFilterTest() {
        List<InternshipFilter> filters = List.of(new InternshipStatusFilter());
        InternshipFilterDto filterDto = InternshipFilterDto.builder().status(InternshipStatus.IN_PROGRESS).build();
        System.out.println(filterDto);

        InternshipDto i1 = InternshipDto.builder().status(InternshipStatus.COMPLETED).build();
        InternshipDto i2 = InternshipDto.builder().status(InternshipStatus.COMPLETED).build();
        InternshipDto i3 = InternshipDto.builder().status(InternshipStatus.IN_PROGRESS).build();
        InternshipDto i4 = InternshipDto.builder().status(InternshipStatus.IN_PROGRESS).build();
        List<InternshipDto> list = new java.util.ArrayList<>(List.of(i1, i2, i3, i4));
        System.out.println(list);

        filters.stream()
                .filter((fil) -> fil.isApplicable(filterDto))
                .forEach((fil) -> fil.apply(list, filterDto));

        assertEquals(2, list.size());
    }
}
