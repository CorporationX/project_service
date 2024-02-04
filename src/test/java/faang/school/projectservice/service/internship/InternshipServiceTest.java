package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.exeption.DataValidationException;
import faang.school.projectservice.filter.internship.InternshipStatusFilter;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.mapper.internship.TeamMemberMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static faang.school.projectservice.model.InternshipStatus.COMPLETED;
import static faang.school.projectservice.model.InternshipStatus.IN_PROGRESS;
import static faang.school.projectservice.model.TeamRole.DEVELOPER;
import static faang.school.projectservice.model.TeamRole.INTERN;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InternshipServiceTest {
    @InjectMocks
    private InternshipService internshipService;
    @Mock
    private InternshipRepository internshipRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Spy
    private InternshipMapper internshipMapper = Mappers.getMapper(InternshipMapper.class);
    @Spy
    private TeamMemberMapper teamMemberMapper = Mappers.getMapper(TeamMemberMapper.class);
    @Captor
    private ArgumentCaptor<Internship> internshipCaptor;
    private TeamMember teamMember;
    private InternshipDto internshipDto;
    private TeamMemberDto teamMemberDto;
    private List<TeamMember> interns;


    @BeforeEach
    void setUp() {
        teamMember = TeamMember.builder()
                .id(1L)
                .roles(new ArrayList<>(List.of(TeamRole.INTERN)))
                .build();
        teamMemberDto = TeamMemberDto.builder()
                .id(1L)
                .roles(new ArrayList<>(List.of(TeamRole.INTERN)))
                .build();
        interns = new ArrayList<>(List.of(teamMember, new TeamMember()));
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
        internshipCaptor = ArgumentCaptor.forClass(Internship.class);
        verify(internshipRepository).save(internshipCaptor.capture());
        assertEquals(internshipDto.getId(), internshipCaptor.getValue().getId());
    }

    @Test
    void testCreateInternshipFailure() {
        when(internshipService.createInternship(internshipDto)).thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> internshipService.createInternship(internshipDto));
    }

    @Test
    void testCreateInternshipWithNullInterns() {
        internshipDto.setInterns(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> internshipService.createInternship(internshipDto));
        assertEquals(exception.getMessage(), "Interns list cannot be empty");
    }

    @Test
    void testCreateInternshipWithEmptyInterns() {
        internshipDto.setInterns(new ArrayList<>());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> internshipService.createInternship(internshipDto));
        assertEquals(exception.getMessage(), "Interns list cannot be empty");
    }

    @Test
    void testCreateInternshipWithNullDate() {
        internshipDto.setEndDate(null);
        DataValidationException exception = assertThrows(DataValidationException.class, () -> internshipService.createInternship(internshipDto));
        assertEquals(exception.getMessage(), "Invalid dates");
    }

    @Test
    void testCreateInternshipWithIncorrectDate() {
        internshipDto.setEndDate(LocalDateTime.of(2024, Month.JANUARY, 15, 10, 0));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> internshipService.createInternship(internshipDto));
        assertEquals(exception.getMessage(), "Incorrect dates have been entered");
    }

    @Test
    void testCreateInternshipWithLongerThreeMonths() {
        internshipDto.setEndDate(LocalDateTime.of(2024, Month.MAY, 15, 10, 0));
        DataValidationException exception = assertThrows(DataValidationException.class, () -> internshipService.createInternship(internshipDto));
        assertEquals(exception.getMessage(), "Internship duration cannot exceed 91 days");
    }

    @Test
    void testAddNewInternsSuccessful() {
        Internship internship = internshipMapper.toEntity(internshipDto);
        when(internshipRepository.findById(123L)).thenReturn(Optional.of(internship));
        when(internshipRepository.save(internship)).thenReturn(internship);
        InternshipDto updatedInternshipDto = internshipService.addNewIntern(internshipDto.getId(), 1L);
        assertEquals(3, updatedInternshipDto.getInterns().size());
    }

    @Test
    void testFinishInterPrematurelySuccessful() {
        Internship internship = internshipMapper.toEntity(internshipDto);
        when(teamMemberRepository.findById(1L)).thenReturn(teamMember);
        when(internshipRepository.findById(123L)).thenReturn(Optional.of(internship));
        internshipService.finishInternPrematurely(internshipDto.getId(), teamMemberDto.getId());
        assertTrue(teamMember.getRoles().contains(DEVELOPER));
        assertFalse(teamMember.getRoles().contains(INTERN));
    }

    @Test
    void testRemoveInterPrematurelySuccessful() {
        Internship internship = internshipMapper.toEntity(internshipDto);
        when(internshipRepository.findById(123L)).thenReturn(Optional.of(internship));
        internshipService.removeInternPrematurely(internshipDto.getId(), teamMemberDto.getId());
        assertEquals(1, internship.getInterns().size());
        // здесь бы проверить на удаление роли INTERN у teamMemberDto, но не пойму как
    }

    @Test
    void testUpdateInternshipSuccessful() {
        Internship internship = internshipMapper.toEntity(internshipDto);
        when(internshipRepository.findById(123L)).thenReturn(Optional.of(internship));
        when(internshipRepository.save(internship)).thenReturn(internship);
        internshipDto.setStartDate(LocalDateTime.of(2024, Month.FEBRUARY, 15, 10, 0));
        InternshipDto updatedInternshipDto = internshipService.updateInternship(internshipDto);
        assertEquals(updatedInternshipDto.getStartDate(), internshipDto.getStartDate());
    }

    @Test
    void testGetInternshipByFilterSuccessful() {
        Internship internship = internshipMapper.toEntity(internshipDto);
        internship.setStatus(IN_PROGRESS);
        Internship internship1 = new Internship();
        internship1.setStatus(COMPLETED);
        Internship internship2 = new Internship();
        internship2.setStatus(COMPLETED);

        InternshipFilterDto internshipFilterDto = new InternshipFilterDto();
        internshipFilterDto.setStatus(IN_PROGRESS);

        internshipService = new InternshipService(internshipRepository, internshipMapper, teamMemberRepository,
                teamMemberMapper, Arrays.asList(new InternshipStatusFilter()));

        when(internshipRepository.findAll()).thenReturn(Arrays.asList(internship, internship1, internship2));
        List<InternshipDto> actualList = internshipService.getInternshipByFilter(internshipFilterDto);

        assertEquals(Arrays.asList(internshipDto), actualList);

    }

}