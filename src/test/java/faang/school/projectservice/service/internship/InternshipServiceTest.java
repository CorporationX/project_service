package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.exeption.DataValidationException;
import faang.school.projectservice.filter.internship.InternshipStatusFilter;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.mapper.internship.TeamMemberMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static faang.school.projectservice.model.InternshipStatus.COMPLETED;
import static faang.school.projectservice.model.InternshipStatus.IN_PROGRESS;
import static faang.school.projectservice.model.TeamRole.DEVELOPER;
import static faang.school.projectservice.model.TeamRole.INTERN;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InternshipServiceTest {
    @InjectMocks
    private InternshipServiceImpl internshipService;
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
        // interns = new ArrayList<>(List.of(teamMember, new TeamMember()));
        internshipDto = InternshipDto.builder()
                .id(123L)
                .projectId(1L)
                .mentorId(5L)
                .internsId(List.of(8L, 9L, 10L))
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(90))
                .build();
    }

    @Test
    void testCreateInternshipSuccessful() {
        when(teamMemberRepository.existsById(anyLong())).thenReturn(true);
        when(teamMemberRepository.findById(anyLong())).thenReturn(teamMember);
        internshipService.createInternship(internshipDto);
        internshipCaptor = ArgumentCaptor.forClass(Internship.class);
        verify(internshipRepository).save(internshipCaptor.capture());
        assertEquals(internshipDto.getId(), internshipCaptor.getValue().getId());
    }

    @Test
    void testCreateInternshipFailure() {
        when(teamMemberRepository.existsById(anyLong())).thenReturn(false);
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> internshipService.createInternship(internshipDto));
        assertEquals("There is no mentor with this id in the team member", exception.getMessage());
    }

    @Test
    void testCreateInternshipWithNullInterns() {
        when(teamMemberRepository.existsById(anyLong())).thenReturn(true);
        internshipDto.setInternsId(List.of());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> internshipService.createInternship(internshipDto));
        assertEquals("Interns list cannot be empty", exception.getMessage());
    }

    @Test
    void testCreateInternshipWithEmptyInterns() {
        when(teamMemberRepository.existsById(anyLong())).thenReturn(true);
        internshipDto.setInternsId(new ArrayList<>());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> internshipService.createInternship(internshipDto));
        assertEquals(exception.getMessage(), "Interns list cannot be empty");
    }

    @Test
    void testCreateInternshipWithNullDate() {
        when(teamMemberRepository.existsById(anyLong())).thenReturn(true);
        internshipDto.setEndDate(null);
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> internshipService.createInternship(internshipDto));
        assertEquals(exception.getMessage(), "Invalid dates");
    }

    @Test
    void testCreateInternshipWithIncorrectDate() {
        when(teamMemberRepository.existsById(anyLong())).thenReturn(true);
        internshipDto.setEndDate(LocalDateTime.now().minusDays(1));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> internshipService.createInternship(internshipDto));
        assertEquals(exception.getMessage(), "Incorrect dates have been entered");
    }

    @Test
    void testCreateInternshipWithLongerThreeMonths() {
        when(teamMemberRepository.existsById(anyLong())).thenReturn(true);
        internshipDto.setEndDate(LocalDateTime.now().plusDays(95));
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> internshipService.createInternship(internshipDto));
        assertEquals(exception.getMessage(), "Internship duration cannot exceed 91 days");
    }

    @Test
    void testAddNewInternsSuccessful() {
        Internship internship = internshipMapper.toEntity(internshipDto);
        internship.setInterns(new ArrayList<>(List.of(TeamMember.builder().id(8L).build())));
        when(internshipRepository.findById(123L)).thenReturn(Optional.of(internship));
        when(teamMemberRepository.findById(anyLong())).thenReturn(teamMember);
        InternshipDto updatedInternshipDto = internshipService.addNewIntern(internshipDto.getId(), 1L);
        assertEquals(2, updatedInternshipDto.getInternsId().size());
    }

    @Test
    void testFinishInterPrematurelySuccessful() {
        Internship internship = internshipMapper.toEntity(internshipDto);
        internship.setInterns(new ArrayList<>(List.of(TeamMember.builder().id(8L).build())));
        when(teamMemberRepository.findById(1L)).thenReturn(teamMember);
        when(internshipRepository.findById(123L)).thenReturn(Optional.of(internship));
        internshipService.finishInternPrematurely(internshipDto.getId(), teamMemberDto.getId());
        assertTrue(teamMember.getRoles().contains(DEVELOPER));
        assertFalse(teamMember.getRoles().contains(INTERN));
    }

    @Test
    void testRemoveInterPrematurelySuccessful() {
        Internship internship = internshipMapper.toEntity(internshipDto);
        internship.setInterns(new ArrayList<>(List.of(teamMember, new TeamMember())));
        when(internshipRepository.findById(anyLong())).thenReturn(Optional.of(internship));
        internshipService.removeInternPrematurely(internshipDto.getId(), teamMemberDto.getId());
        assertEquals(1, internship.getInterns().size());
    }

  /*  @Test
    void testUpdateInternshipSuccessful() {
        Internship internship = internshipMapper.toEntity(internshipDto);
        when(internshipRepository.findById(123L)).thenReturn(Optional.of(internship));
       // when(internshipRepository.save(internship)).thenReturn(internship);
        internshipDto.setStartDate(LocalDateTime.now().plusDays(5));
        InternshipDto updatedInternshipDto = internshipService.updateInternship(internshipDto);
        assertEquals(updatedInternshipDto.getStartDate(), internshipDto.getStartDate());
    }*/

    @Test
    void testUpdateInternshipAfterEndDate_InternshipNotFound() {
        when(internshipRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () ->
                internshipService.updateInternshipAfterEndDate(123L));
    }


    @Test
    void testGetInternshipByStatusSuccessful() {
        internshipDto.setInternsId(new ArrayList<>());
        Internship internship = internshipMapper.toEntity(internshipDto);
        internship.setStatus(IN_PROGRESS);
        Internship internship1 = new Internship();
        internship1.setStatus(COMPLETED);
        Internship internship2 = new Internship();
        internship2.setStatus(COMPLETED);

        InternshipFilterDto internshipFilterDto = new InternshipFilterDto();
        internshipFilterDto.setStatus(IN_PROGRESS);

        internshipService = new InternshipServiceImpl(internshipRepository, internshipMapper, teamMemberRepository,
                teamMemberMapper, Arrays.asList(new InternshipStatusFilter()));

        when(internshipRepository.findAll()).thenReturn(Arrays.asList(internship, internship1, internship2));
        List<InternshipDto> actualList = internshipService.getInternshipByStatus(internshipFilterDto);

        assertEquals(Arrays.asList(internshipDto), actualList);
    }
}