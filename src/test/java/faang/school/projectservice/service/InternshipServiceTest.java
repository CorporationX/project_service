package faang.school.projectservice.service;

import faang.school.projectservice.dto.internship.InternshipCreateRequest;
import faang.school.projectservice.dto.internship.InternshipResponse;
import faang.school.projectservice.dto.internship.InternshipUpdateRequest;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.validation.InternshipValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InternshipServiceTest {
    @Mock
    private InternshipRepository internshipRepository;
    @Mock
    private InternshipValidationService internshipValidationService;
    @Mock
    private Internship internship;

    @Spy
    private InternshipMapper internshipMapper = Mappers.getMapper(InternshipMapper.class);

    @InjectMocks
    private InternshipService internshipService;


    @Test
    public void createInternshipSuccessfully() {
        InternshipCreateRequest dto = new InternshipCreateRequest(1L, 1L, 1L,
                List.of(1L),
                LocalDateTime.of(2025, 1, 29, 13, 00),
                LocalDateTime.of(2025, 2, 28, 13, 00),
                "Some description",
                "some name",
                TeamRole.DEVELOPER);

        Internship actualInternship = internshipMapper.toEntity(dto);
        internshipService.createInternship(dto);

        verify(internshipValidationService, times(1)).validateRequest(dto);
        verify(internshipMapper, times(2)).toEntity(dto);
        verify(internshipRepository, times(1)).save(actualInternship);

    }

    @Test
    public void createInternshipWithIncorrectData() {
        InternshipCreateRequest dto = new InternshipCreateRequest(1L, 1L, 1L,
                List.of(1L),
                LocalDateTime.of(2025, 1, 29, 13, 00),
                LocalDateTime.of(2025, 2, 28, 13, 00),
                "Some description",
                "some name",
                TeamRole.DEVELOPER);

        internshipService.createInternship(dto);
        doThrow(new IllegalArgumentException()).when(internshipValidationService).validateRequest(dto);

        verify(internshipValidationService, times(1)).validateRequest(dto);
        assertThrows(IllegalArgumentException.class, () -> internshipValidationService.validateRequest(dto));
    }

    @Test
    public void updateInternship_ShouldValidateAndUpdateRoles_WhenEndDatePassed() {
        InternshipUpdateRequest dto = mock(InternshipUpdateRequest.class);
        when(dto.getId()).thenReturn(1L);
        when(dto.getEndDate()).thenReturn(LocalDateTime.now().minusDays(1));
        when(dto.getRole()).thenReturn(TeamRole.DEVELOPER);

        Internship internship = mock(Internship.class);
        TeamMember firstIntern = mock(TeamMember.class);
        TeamMember secondIntern = mock(TeamMember.class);

        List<TeamMember> interns = List.of(firstIntern, secondIntern);

        when(internshipRepository.getById(dto.getId())).thenReturn(internship);
        when(internship.getInterns()).thenReturn(interns);

        internshipService.updateInternship(dto);

        verify(internshipValidationService, times(1)).validateRequest(dto);
        verify(internshipRepository, times(1)).save(internship);

        for (TeamMember intern : interns) {
            verify(intern).setRoles(argThat(roles ->
                    roles.contains(TeamRole.DEVELOPER) && !roles.contains(TeamRole.INTERN)
            ));
        }
    }

    @Test
    public void updateInternshipWithNewData() {
        InternshipUpdateRequest internshipUpdateRequest = new InternshipUpdateRequest(
                1L, 2L, 3L, List.of(1L, 2L, 3L),
                LocalDateTime.of(2025, 5, 1, 23, 00),
                LocalDateTime.of(2025, 6, 1, 23, 00),
                "changed description",
                "changed name",
                TeamRole.DEVELOPER
        );
        InternshipCreateRequest internshipCreateDto = new InternshipCreateRequest(
                1L, 2L, 3L, List.of(1L, 2L, 3L),
                LocalDateTime.of(2025, 5, 1, 23, 00),
                LocalDateTime.of(2025, 6, 1, 23, 00),
                "some description",
                "some name",
                TeamRole.DEVELOPER
        );
        Internship internshipThatShouldBeUpdated = internshipMapper.toEntity(internshipCreateDto);
        when(internshipRepository.getById(internshipUpdateRequest.getId())).thenReturn(internshipThatShouldBeUpdated);


        internshipService.updateInternship(internshipUpdateRequest);


        verify(internshipRepository, times(1))
                .save(internshipThatShouldBeUpdated);
        verify(internshipMapper, times(1))
                .update(internshipUpdateRequest, internshipThatShouldBeUpdated);
        assertEquals(internshipThatShouldBeUpdated.getProject().getId(), internshipUpdateRequest.getProjectId());
        assertEquals(internshipThatShouldBeUpdated.getId(), internshipUpdateRequest.getId());
        assertEquals(internshipThatShouldBeUpdated.getMentorId().getId(), internshipUpdateRequest.getMentorId());
        assertEquals(internshipThatShouldBeUpdated.getStartDate(), internshipUpdateRequest.getStartDate());
        assertEquals(internshipThatShouldBeUpdated.getEndDate(), internshipUpdateRequest.getEndDate());
        assertEquals(internshipThatShouldBeUpdated.getDescription(), internshipUpdateRequest.getDescription());
        assertEquals(internshipThatShouldBeUpdated.getName(), internshipUpdateRequest.getName());
        assertEquals(internshipThatShouldBeUpdated.getRole(), internshipUpdateRequest.getRole());
    }

    @Test
    void getAllInternships_ReturnsList() {
        TeamMember teamLead1 = new TeamMember();
        teamLead1.setId(100L);
        TeamMember teamLead2 = new TeamMember();
        teamLead2.setId(200L);
        Internship internship1 = new Internship();
        internship1.setId(1L);
        internship1.setInterns(new ArrayList<>());
        internship1.setMentorId(teamLead1);

        Internship internship2 = new Internship();
        internship2.setId(2L);
        internship2.setInterns(new ArrayList<>());
        internship2.setMentorId(teamLead2);

        when(internshipRepository.findAll()).thenReturn(List.of(internship1, internship2));

        List<InternshipResponse> result = internshipService.getAllInternships();

        assertEquals(2, result.size());
        verify(internshipMapper, times(2)).toDto(any(Internship.class));
    }


    @Test
    void getInternshipById_Found_ReturnsResponse() {
        long internshipId = 1L;
        TeamMember teamLead = new TeamMember();
        teamLead.setId(100L);
        Internship internship = new Internship();
        internship.setId(internshipId);
        internship.setName("Test Internship");
        internship.setInterns(new ArrayList<>());
        internship.setMentorId(teamLead);

        when(internshipRepository.findById(internshipId)).thenReturn(Optional.of(internship));

        InternshipResponse response = internshipService.getInternshipById(internshipId);

        assertEquals("Test Internship", response.name());
        verify(internshipMapper, times(1)).toDto(internship);
    }

    @Test
    void getInternshipById_NotFound_ThrowsException() {
        long internshipId = 999L;
        when(internshipRepository.findById(internshipId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> internshipService.getInternshipById(internshipId));
    }

    @Test
    void removeInternFromInternship_Success() {
        long internshipId = 1L;
        List<Long> internIdsToRemove = List.of(1L, 2L);

        TeamMember intern1 = new TeamMember();
        intern1.setId(1L);
        TeamMember intern2 = new TeamMember();
        intern2.setId(2L);
        TeamMember intern3 = new TeamMember();
        intern3.setId(3L);

        Internship internship = new Internship();
        internship.setId(internshipId);
        internship.setInterns(List.of(intern1, intern2, intern3));

        when(internshipRepository.getById(internshipId)).thenReturn(internship);

        internshipService.removeInternFromInternship(internIdsToRemove, internshipId);

        verify(internshipRepository, times(1)).save(argThat(savedInternship ->
                savedInternship.getInterns().size() == 1 &&
                        savedInternship.getInterns().get(0).getId() == 3L
        ));
    }

    @Test
    void finishTheInternshipAheadOfScheduleFor_Success() {
        long internshipId = 1L;
        List<Long> internIdsToFinish = List.of(1L, 2L);

        TeamMember intern1 = new TeamMember();
        intern1.setId(1L);
        intern1.setRoles(new ArrayList<>());

        TeamMember intern2 = new TeamMember();
        intern2.setId(2L);
        intern2.setRoles(new ArrayList<>());

        TeamMember intern3 = new TeamMember();
        intern3.setId(3L);
        intern3.setRoles(new ArrayList<>());

        Internship internship = new Internship();
        internship.setId(internshipId);
        internship.setRole(TeamRole.DEVELOPER);
        internship.setInterns(List.of(intern1, intern2, intern3));

        when(internshipRepository.getById(internshipId)).thenReturn(internship);

        internshipService.finishTheInternshipAheadOfScheduleFor(internIdsToFinish, internshipId);

        verify(internshipRepository, times(1)).save(argThat(savedInternship ->
                savedInternship.getInterns().size() == 1 &&
                        savedInternship.getInterns().get(0).getId() == 3L &&
                        intern1.getRoles().contains(TeamRole.DEVELOPER) &&
                        intern2.getRoles().contains(TeamRole.DEVELOPER)
        ));
    }

}