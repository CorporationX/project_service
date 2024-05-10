package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.internship.filter.InternshipFilterService;
import faang.school.projectservice.validator.internship.InternshipValidator;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InternshipServiceImplTest {
    @InjectMocks
    private InternshipServiceImpl internshipServiceImpl;

    @Mock
    private InternshipRepository internshipRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private InternshipValidator validator;
    @Mock
    private InternshipMapper mapper;
    @Mock
    private InternshipFilterService internshipFilterService;

    private InternshipDto internshipDto;
    private Internship internship;


    @Before
    public void setUp() {
        internshipDto = new InternshipDto();
        internship = new Internship();

        when(mapper.toEntity(internshipDto)).thenReturn(internship);
        when(mapper.toDto(internship)).thenReturn(internshipDto);
    }

    @Test
    public void shouldSuccessfullyCreateInternship() {
        doNothing().when(validator).validateInternshipExistence(internshipDto);
        // TODO я думаю что эти проверки лучше делать в тестах на валидатор
        doNothing().when(validator).validateInternshipNotStarted(internship);
        when(internshipRepository.save(internship)).thenReturn(internship);

        InternshipDto result = internshipServiceImpl.createInternship(internshipDto);

        // TODO я думаю что эти проверки лучше делать в тестах на валидатор
        verify(validator).validateInternshipExistence(internshipDto);
        verify(validator).validateInternshipNotStarted(internship);
        verify(internshipRepository).save(internship);
        assertEquals(internshipDto, result);
    }

    @Test
    void shouldSuccessfullyUpdateInternship() {

        TeamMember intern = new TeamMember();
        intern.setId(2L);

        InternshipDto updatedInternshipDto = InternshipDto.builder()
                .id(1L)
                .interns(Collections.singletonList(intern))
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(29))
                .status(InternshipStatus.COMPLETED)
                .build();

        Internship updatedInternship = new Internship();
        updatedInternship.setId(1L);
        updatedInternship.setInterns(Collections.singletonList(intern));
        updatedInternship.setStartDate(LocalDateTime.now().minusDays(1));
        updatedInternship.setEndDate(LocalDateTime.now().plusDays(29));
        updatedInternship.setStatus(InternshipStatus.COMPLETED);

        Internship internship = new Internship();
        internship.setId(1L);
        internship.setInterns(Collections.singletonList(intern));
        internship.setStartDate(LocalDateTime.now().minusDays(1));
        internship.setEndDate(LocalDateTime.now().plusDays(29));
        internship.setStatus(InternshipStatus.IN_PROGRESS);

        when(internshipRepository.findById(updatedInternshipDto.getId())).thenReturn(Optional.of(internship));
        doNothing().when(validator).validateInternshipNotStarted(internship);
        doNothing().when(validator).validateInternshipNotCompleted(internship);
        doNothing().when(validator).validateUpdatedInternshipDiffersByLast(internship, updatedInternship);
        when(mapper.toEntity(updatedInternshipDto)).thenReturn(updatedInternship);
        when(mapper.toDto(updatedInternship)).thenReturn(updatedInternshipDto);
        doNothing().when(internshipRepository).deleteById(internship.getId());
        when(internshipRepository.save(updatedInternship)).thenReturn(updatedInternship);

        internshipServiceImpl.updateInternship(updatedInternshipDto);

        verify(validator).validateInternshipNotStarted(internship);
        verify(validator).validateInternshipNotCompleted(internship);
        verify(validator).validateUpdatedInternshipDiffersByLast(internship, updatedInternship);
        verify(internshipRepository).deleteById(internship.getId());
        verify(internshipRepository).save(updatedInternship);
        assertEquals(InternshipStatus.COMPLETED, updatedInternshipDto.getStatus());
    }

    @Test
    void shouldSuccessfullyGetInternshipsByStatus() {
        InternshipFilterDto filterDto = new InternshipFilterDto();
        InternshipStatus status = InternshipStatus.IN_PROGRESS;
        List<Internship> internships = getIntershipsList().stream()
                .filter(i -> i.getStatus() == status)
                .toList();

        when(internshipRepository.findByStatus(status)).thenReturn(internships);

        when(internshipFilterService.applyFilters(any(), any()))
                .thenReturn(internships.stream());

        when(mapper.toDto(any(Internship.class))).thenAnswer(invocation -> new InternshipDto());

        List<InternshipDto> result = internshipServiceImpl.getInternshipsByStatus(status, filterDto);

        assertEquals(internships.size(), result.size());

        verify(internshipRepository).findByStatus(status);
        verify(internshipFilterService).applyFilters(any(), any());
    }

    @Test
    void shouldSuccessfullyGetInternshipsByRole() {
        Stream<Internship> intershipsStream = getIntershipsList().stream();
        InternshipFilterDto filterDto = new InternshipFilterDto();
        TeamRole role = TeamRole.INTERN;

        when(internshipFilterService.applyFilters(any(), any())).thenReturn(intershipsStream);
        when(internshipRepository.findAll()).thenReturn(getIntershipsList());

        List<InternshipDto> result = internshipServiceImpl.getInternshipsByRole(role, filterDto);

        assertEquals(result, getIntershipsList().stream().map(mapper::toDto).toList());

        verify(internshipRepository, timeout(1)).findAll();
        verify(internshipFilterService, timeout(1)).applyFilters(any(), any());
    }

    @Test
    public void shouldReturnEmptyListWhenNoInternshipsAvailable() {
        when(internshipRepository.findAll()).thenReturn(Collections.emptyList());
        List<InternshipDto> results = internshipServiceImpl.getAllInternships(new InternshipFilterDto());
        assertTrue(results.isEmpty());
    }


    @Test
    void shouldSuccessfullyUpdateInternsRoles() {
        long internshipId = 1L;
        TeamMember teamMember = new TeamMember();
        teamMember.setId(2L);
        teamMember.setRoles(new ArrayList<>(Collections.singletonList(TeamRole.INTERN)));

        Internship internship = new Internship();
        internship.setInterns(new ArrayList<>(Collections.singletonList(teamMember)));

        when(internshipRepository.findById(internshipId)).thenReturn(Optional.of(internship));

        Internship updatedInternship = internshipServiceImpl.updateInternsRoles(internshipId, teamMember);

        verify(internshipRepository).findById(internshipId);
        assertTrue(updatedInternship.getInterns().contains(teamMember));
        assertEquals(TeamRole.INTERN, teamMember.getRoles().get(0));
    }

    @Test
    void shouldSuccessfullyGetAllInternships() {
        Stream<Internship> intershipsStream = getIntershipsList().stream();
        InternshipFilterDto filterDto = new InternshipFilterDto();

        when(internshipFilterService.applyFilters(any(), any())).thenReturn(intershipsStream);
        when(internshipRepository.findAll()).thenReturn(getIntershipsList());

        List<InternshipDto> result = internshipServiceImpl.getAllInternships(filterDto);

        assertEquals(result, getIntershipsList().stream().map(mapper::toDto).toList());

        verify(internshipRepository, timeout(1)).findAll();
        verify(internshipFilterService, timeout(1)).applyFilters(any(), any());
    }

    private List<Internship> getIntershipsList() {
        TeamRole role = TeamRole.INTERN;
        List<TeamRole> roles = List.of(role);

        TeamMember intern1 = TeamMember.builder().id(1L).roles(roles).build();
        TeamMember intern2 = TeamMember.builder().id(2L).roles(roles).build();
        TeamMember intern3 = TeamMember.builder().id(3L).roles(roles).build();

        List<TeamMember> interns = List.of(intern1, intern2, intern3);

        Internship internship1 = Internship.builder()
                .id(1L)
                .interns(interns)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(29))
                .status(InternshipStatus.IN_PROGRESS)
                .build();

        Internship internship2 = Internship.builder()
                .id(2L)
                .interns(interns)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(29))
                .status(InternshipStatus.COMPLETED).build();

        Internship internship3 = Internship.builder()
                .id(3L)
                .interns(interns)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(29))
                .status(InternshipStatus.IN_PROGRESS)
                .build();

        return List.of(
                internship1,
                internship2,
                internship3
        );
    }
}