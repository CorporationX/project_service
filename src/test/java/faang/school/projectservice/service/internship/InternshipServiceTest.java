package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InternshipServiceTest {
    @InjectMocks
    private InternshipService internshipService;

    @Mock
    private InternshipRepository internshipRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Spy
    private InternshipMapper internshipMapper = Mappers.getMapper(InternshipMapper.class);
    private InternshipDto internshipDto;
    private Project project;
    private TeamMember mentor;
    private List<TeamMember> interns;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(5L);
        mentor = new TeamMember();
        mentor.setId(5L);
        interns = new ArrayList<>(List.of(new TeamMember(), new TeamMember()));
        internshipDto = InternshipDto.builder()
                .id(123L)
                .project(project)
                .mentorId(mentor)
                .interns(interns)
                .startDate(LocalDateTime.of(2024, Month.FEBRUARY, 5, 10, 0))
                .endDate((LocalDateTime.of(2024, Month.MAY, 5, 10, 0)))
                .build();
        when(projectRepository.getProjectById(5L)).thenReturn(project);
        when(teamMemberRepository.findById(5L)).thenReturn(mentor);
    }

    @Test
    void testCreateInternshipSuccessful() {
        internshipService.createInternship(internshipDto);
        ArgumentCaptor<Internship> internshipCaptor = ArgumentCaptor.forClass(Internship.class);
        verify(internshipRepository).save(internshipCaptor.capture());
        assertEquals(internshipDto.getId(), internshipCaptor.getValue().getId());
    }

    @Test
    void testCreateInternshipFailure() {
        when(internshipService.createInternship(internshipDto)).thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> internshipService.createInternship(internshipDto));
    }

    @Test
    void testCreateInternshipWithExistenceIdIntership() {
        Mockito.when(internshipRepository.existsById(123L)).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> internshipService.createInternship(internshipDto));
    }

    @Test
    void testCreateInternshipWithNullInterns() {
        internshipDto.setInterns(null);
        assertThrows(IllegalArgumentException.class, () -> internshipService.createInternship(internshipDto));
    }

    @Test
    void testCreateInternshipWithEmptyInterns() {
        internshipDto.setInterns(new ArrayList<>());
        assertThrows(IllegalArgumentException.class, () -> internshipService.createInternship(internshipDto));
    }

    @Test
    void testCreateInternshipWithIncorrectDate() {
        internshipDto.setEndDate(LocalDateTime.of(2024, Month.JANUARY, 15, 10, 0));
        assertThrows(IllegalArgumentException.class, () -> internshipService.createInternship(internshipDto));
    }

    @Test
    void testCreateInternshipWithLongerThreeMonths() {
        internshipDto.setEndDate(LocalDateTime.of(2024, Month.MAY, 15, 10, 0));
        assertThrows(IllegalArgumentException.class, () -> internshipService.createInternship(internshipDto));
    }
}