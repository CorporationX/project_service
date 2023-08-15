package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.filter.internship.InternshipStatusFilter;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.mapper.internship.InternshipMapperImpl;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.internship.InternshipValidator;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InternshipServiceTest {
    @Mock
    private InternshipRepository internshipRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Spy
    private InternshipMapperImpl internshipMapper;
    @Mock
    private InternshipValidator validator;

    @InjectMocks
    private InternshipService service;

    @Test
    public void createInternship_Test() {
        InternshipDto internshipDto = InternshipDto.builder().interns(List.of(1L))
                .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(3, ChronoUnit.MONTHS))
                .mentorId(anyLong()).build();
        InternshipDto res = service.createInternship(internshipDto);
        Mockito.verify(internshipRepository).save(internshipMapper.toEntity(internshipDto));
    }

    @Test
    public void updateInternship_Test() {
        InternshipDto internshipDto = InternshipDto.builder()
                .interns(List.of())
                .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(2, ChronoUnit.MONTHS))
                .mentorId(anyLong()).build();
        long id = 1;
        Internship old = Internship.builder().name("OLD").status(InternshipStatus.IN_PROGRESS).interns(List.of()).build();
        Internship internship = Internship.builder().name("NEW").status(InternshipStatus.IN_PROGRESS).interns(List.of()).build();

        when(internshipRepository.findById(id)).thenReturn(Optional.ofNullable(old));
        when(internshipMapper.toEntity(internshipDto)).thenReturn(internship);

        service.updateInternship(id, internshipDto);
        Mockito.verify(internshipRepository).save(internship);
    }

    @Test
    public void updateInternship_ToCOMPLETED_Test() {
        InternshipDto internshipDto = InternshipDto.builder()
                .interns(List.of())
                .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(2, ChronoUnit.MONTHS))
                .mentorId(anyLong())
                .status(InternshipStatus.COMPLETED).build();
        long id = 1;
        Internship old = Internship.builder().name("OLD").status(InternshipStatus.IN_PROGRESS).interns(List.of()).build();
        Internship internship = Internship.builder().name("NEW").status(InternshipStatus.COMPLETED).interns(List.of()).build();

        when(internshipRepository.findById(id)).thenReturn(Optional.ofNullable(old));
        when(internshipMapper.toEntity(internshipDto)).thenReturn(internship);

        service.updateInternship(id, internshipDto);
        Mockito.verify(internshipRepository).deleteById(id);
    }

    @Test
    public void findAllInternships_Test() {
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
    public void internships_Filter_Test() {
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
