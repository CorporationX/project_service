package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.filter.internship.InternshipStatusFilter;
import faang.school.projectservice.mapper.internship.InternshipMapperImpl;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.model.TeamMember;
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
                .schedule(1L).status(InternshipStatus.IN_PROGRESS)
                .mentorId(anyLong()).build();
        long id = 1;
        Internship old = Internship.builder().name("OLD").status(InternshipStatus.IN_PROGRESS)
                .schedule(new Schedule()).interns(List.of()).build();

        when(internshipRepository.findById(id)).thenReturn(Optional.ofNullable(old));

        service.updateInternship(id, internshipDto);
        Mockito.verify(internshipRepository).save(internshipMapper.toEntity(internshipDto));
    }

    @Test
    public void findAllInternships_Test() {
        Internship internship1 = Internship.builder().name("A")
                .interns(List.of(TeamMember.builder().id(1L).build()))
                .schedule(new Schedule()).build();
        Internship internship2 = Internship.builder().name("B")
                .interns(List.of(TeamMember.builder().id(2L).build()))
                .schedule(new Schedule()).build();
        List<Internship> list = List.of(internship1, internship2);
        when(internshipRepository.findAll()).thenReturn(list);
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
