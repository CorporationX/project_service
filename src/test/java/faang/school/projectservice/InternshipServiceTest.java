package faang.school.projectservice;

import faang.school.projectservice.dto.client.InternshipDto;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    public void updateInternshipWithStatusINPROGRESSAndMapperTest() {
        InternshipDto internshipDto = InternshipDto.builder()
                .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(2, ChronoUnit.MONTHS))
                .mentorId(anyLong())
                .internsId(Collections.emptyList())
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
                .mentorId(anyLong())
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
}