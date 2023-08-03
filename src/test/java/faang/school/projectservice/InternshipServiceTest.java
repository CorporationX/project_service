package faang.school.projectservice;

import faang.school.projectservice.InternshipService.InternshipService;
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

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private InternshipService internshipService;

    @Test
    public void saveNewInternshipThrowsExceptionTest() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> internshipService.saveNewInternship(new InternshipDto()));
        assertEquals(exception.getMessage(), "There is not project for create internship!");
    }

    @Test
    public void saveNewInternshipWithWrongDateTest() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> internshipService.saveNewInternship(InternshipDto.builder()
                        .projectId(1L)
                        .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(4, ChronoUnit.MONTHS))
                        .internsId(List.of(1L))
                        .build()));
        assertEquals(exception.getMessage(), "Internship cannot last more than 3 months!");
    }

    @Test
    public void saveNewInternshipWithoutMentorTest() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> internshipService.saveNewInternship(InternshipDto.builder()
                        .projectId(1L)
                        .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(3, ChronoUnit.MONTHS))
                        .mentorId(null)
                        .build()));
        assertEquals(exception.getMessage(), "There is not mentor for internship!");
    }

    @Test
    public void saveNewInternshipWithoutInternsTest() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> internshipService.saveNewInternship(InternshipDto.builder()
                        .projectId(1L)
                        .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(3, ChronoUnit.MONTHS))
                        .mentorId(1L)
                        .internsId(Collections.emptyList())
                        .build()));
        assertEquals(exception.getMessage(), "There is not interns for internship!");
    }

    @Test
    public void saveNewInternshipWithWrongNameTest() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> internshipService.saveNewInternship(InternshipDto.builder()
                        .projectId(1L)
                        .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(3, ChronoUnit.MONTHS))
                        .mentorId(1L)
                        .internsId(List.of(10L))
                        .name(null)
                        .build()));
        assertEquals(exception.getMessage(), "Need create a name for the internship");
    }

    @Test
    public void saveInternshipMapperTest() {
        InternshipDto internshipDto = InternshipDto.builder()
                .projectId(1L)
                .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(2, ChronoUnit.MONTHS))
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
    public void updateInternshipWithWrongDateTest() {
        InternshipDto internshipDto = InternshipDto.builder().internsId(List.of(10L))
                .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(3, ChronoUnit.MONTHS))
                .mentorId(anyLong()).build();
        long id = 1;
        Internship old = Internship.builder().status(InternshipStatus.COMPLETED).build();

        when(internshipRepository.getById(id)).thenReturn(old);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> internshipService.updateInternship(internshipDto, id));
        assertEquals(exception.getMessage(), "Internship is over!");
    }

    @Test
    public void updateInternshipAfterItsStartTest() {
        InternshipDto internshipDto = InternshipDto.builder()
                .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(3, ChronoUnit.MONTHS))
                .mentorId(anyLong())
                .internsId(List.of(10L))
                .build();
        long id = 1;
        Internship old = Internship.builder().status(InternshipStatus.IN_PROGRESS).interns(List.of()).build();

        when(internshipRepository.getById(id)).thenReturn(old);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> internshipService.updateInternship(internshipDto, id));
        assertEquals(exception.getMessage(), "Can't add new interns!");
    }

    @Test
    public void updateInternshipWithStatusINPROGRESSAndMapperTest() {
        InternshipDto internshipDto = InternshipDto.builder()
                .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(2, ChronoUnit.MONTHS))
                .mentorId(anyLong())
                .internsId(Collections.emptyList())
                .build();
        long id = 1;
        Internship old = Internship.builder().name("old internship").status(InternshipStatus.IN_PROGRESS).interns(List.of()).build();
        Internship internship = Internship.builder().name("new internship").status(InternshipStatus.IN_PROGRESS).interns(List.of()).build();

        when(internshipRepository.getById(id)).thenReturn(old);
        when(internshipMapper.toEntity(internshipDto)).thenReturn(internship);

        internshipService.updateInternship(internshipDto, id);
        Mockito.verify(internshipRepository).save(internship);
    }

    @Test
    public void updateInternshipWithBothStatusAndMapperTest() {
        InternshipDto internshipDto = InternshipDto.builder()
                .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(2, ChronoUnit.MONTHS))
                .mentorId(anyLong())
                .internsId(Collections.emptyList())
                .internshipStatus(InternshipStatus.COMPLETED)
                .build();
        long id = 1;
        Internship oldInternship = Internship.builder().name("old internship").status(InternshipStatus.IN_PROGRESS).interns(List.of()).build();
        Internship internship = Internship.builder().name("new internship").status(InternshipStatus.COMPLETED).interns(List.of()).build();

        when(internshipRepository.getById(id)).thenReturn(oldInternship);
        when(internshipMapper.toEntity(internshipDto)).thenReturn(internship);

        internshipService.updateInternship(internshipDto, id);
        Mockito.verify(internshipRepository).deleteById(id);
    }

    @Test
    public void internships_Filter_Test() {
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
}