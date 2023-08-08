package faang.school.projectservice;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.dto.client.InternshipFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.InternshipFilter;
import faang.school.projectservice.filter.InternshipStatusFilter;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.InternshipService;
import faang.school.projectservice.validator.InternshipValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
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
    private InternshipMapper internshipMapper;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private InternshipValidator internshipValidator;

    @InjectMocks
    private InternshipService internshipService;

    @Test
    public void saveInternshipMapperTest() {
        InternshipDto internshipDto = InternshipDto.builder()
                .projectId(1L)
                .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(3, ChronoUnit.MONTHS))
                .mentorId(1L)
                .internsId(List.of(1L))
                .name("best")
                .build();
        Mockito.when(teamMemberRepository.findById(Mockito.anyLong())).thenReturn(new TeamMember());
        Mockito.when(internshipMapper.toEntity(internshipDto)).thenReturn(new Internship());
        Mockito.when(projectRepository.getProjectById(Mockito.anyLong())).thenReturn(new Project());
        internshipService.saveNewInternship(internshipDto);
        Mockito.verify(internshipRepository).save(internshipMapper.toEntity(internshipDto));
    }

    @Test
    public void updateInternshipWithStatusIN_PROGRESSAndMapperTest() {
        InternshipDto internshipDto = InternshipDto.builder()
                .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(2, ChronoUnit.MONTHS))
                .mentorId(anyLong())
                .internsId(List.of(1L))
                .build();
        long id = 1;
        Internship oldInternship = Internship.builder().name("old internship").status(InternshipStatus.IN_PROGRESS).interns(List.of()).build();
        Internship internship = Internship.builder().name("new internship").status(InternshipStatus.IN_PROGRESS).interns(List.of()).build();

        when(internshipRepository.findById(id)).thenReturn(Optional.ofNullable(oldInternship));
        when(internshipMapper.toEntity(internshipDto)).thenReturn(internship);

        internshipService.updateInternship(internshipDto, id);
        Mockito.verify(internshipRepository).save(internship);
    }

    @Test
    public void updateInternshipWithBothStatusAndMapperTest() {
        InternshipDto internshipDto = InternshipDto.builder()
                .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(2, ChronoUnit.MONTHS))
                .mentorId(1L)
                .internsId(Collections.emptyList())
                .internshipStatus(InternshipStatus.COMPLETED)
                .build();
        long id = 1;
        Internship oldInternship = Internship.builder().name("old internship").status(InternshipStatus.IN_PROGRESS).interns(List.of()).build();
        Internship internship = Internship.builder().name("new internship").status(InternshipStatus.COMPLETED).interns(List.of()).build();

        when(internshipRepository.findById(id)).thenReturn(Optional.ofNullable(oldInternship));
        when(internshipMapper.toEntity(internshipDto)).thenReturn(internship);

        internshipService.updateInternship(internshipDto, id);
        Mockito.verify(internshipRepository).deleteById(id);
    }

    @Test
    public void findAllInternshipsByStatusTest() {
        List<InternshipFilter> filters = List.of(new InternshipStatusFilter());
        InternshipFilterDto filterDto = InternshipFilterDto.builder().internshipStatus(InternshipStatus.IN_PROGRESS).build();
        //System.out.println(filterDto);

        InternshipDto firstInternship = InternshipDto.builder().internshipStatus(InternshipStatus.COMPLETED).build();
        InternshipDto secondInternship = InternshipDto.builder().internshipStatus(InternshipStatus.COMPLETED).build();
        InternshipDto thirdInternship = InternshipDto.builder().internshipStatus(InternshipStatus.IN_PROGRESS).build();
        InternshipDto fourthInternship = InternshipDto.builder().internshipStatus(InternshipStatus.IN_PROGRESS).build();
        List<InternshipDto> list = new ArrayList<>(List.of(firstInternship, secondInternship, thirdInternship, fourthInternship));
        //System.out.println(list);

        filters.stream()
                .filter(f -> f.isApplicable(filterDto))
                .forEach(f -> f.apply(list, filterDto));

        assertEquals(2, list.size());
    }

    @Test
    public void getAllInternships() {
        Internship first = Internship.builder().name("First").build();
        Internship second = Internship.builder().name("Second").build();
        List<Internship> list = List.of(first, second);
        when(internshipRepository.findAll()).thenReturn(list);
        when(internshipMapper.toDto(first)).thenReturn(InternshipDto.builder().name("First").build());
        when(internshipMapper.toDto(second)).thenReturn(InternshipDto.builder().name("Second").build());
        List<InternshipDto> resultList = internshipService.getAllInternships();

        assertEquals(resultList.get(0).getName(), "First");
        assertEquals(resultList.get(1).getName(), "Second");
    }
}
