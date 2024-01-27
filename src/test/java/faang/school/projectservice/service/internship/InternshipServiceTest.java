package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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
    private List<Long> interns;

    @BeforeEach
    void setUp() {
        interns = new ArrayList<>(List.of(1L,2L));
        internshipDto = InternshipDto.builder()
                .id(123L)
                .projectId(1L)
                .mentorId(5L)
                .interns(interns)
                .startDate(LocalDateTime.of(2024, Month.FEBRUARY, 5, 10, 0))
                .endDate((LocalDateTime.of(2024, Month.MAY, 5, 10, 0)))
                .build();
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

//    @Test
//    void testCreateInternshipWithExistenceInternshipId() {
//        lenient().when(projectRepository.getProjectById(5L)).thenReturn(project);
//        lenient().when(teamMemberRepository.findById(5L)).thenReturn(mentor);
//        when(internshipRepository.existsById(123L)).thenReturn(true);
//        assertThrows(IllegalArgumentException.class, () -> internshipService.createInternship(internshipDto));
//        verify(internshipRepository).existsById(123L);
//
//    }

    @Test
    void testCreateInternshipWithNullInterns() {
        internshipDto.setInterns(null);
        IllegalArgumentException exception =  assertThrows(IllegalArgumentException.class, () -> internshipService.createInternship(internshipDto));
        assertEquals(exception.getMessage(), "Interns list cannot be empty");
    }

    @Test
    void testCreateInternshipWithEmptyInterns() {
        internshipDto.setInterns(new ArrayList<>());
        IllegalArgumentException exception =  assertThrows(IllegalArgumentException.class, () -> internshipService.createInternship(internshipDto));
        assertEquals(exception.getMessage(), "Interns list cannot be empty");
    }

    @Test
    void testCreateInternshipWithIncorrectDate() {
        internshipDto.setEndDate(LocalDateTime.of(2024, Month.JANUARY, 15, 10, 0));
        IllegalArgumentException exception =   assertThrows(IllegalArgumentException.class, () -> internshipService.createInternship(internshipDto));
        assertEquals(exception.getMessage(), "Incorrect dates have been entered");
    }

    @Test
    void testCreateInternshipWithLongerThreeMonths() {
        internshipDto.setEndDate(LocalDateTime.of(2024, Month.MAY, 15, 10, 0));
        IllegalArgumentException exception =   assertThrows(IllegalArgumentException.class, () -> internshipService.createInternship(internshipDto));
        assertEquals(exception.getMessage(), "Internship duration cannot exceed 91 days");
    }
}