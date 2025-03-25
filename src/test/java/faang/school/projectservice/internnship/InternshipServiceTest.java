package faang.school.projectservice.internnship;


import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipParticipantUpdateDto;
import faang.school.projectservice.dto.internship.InternshipUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.internship.InternshipService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InternshipServiceTest {

    @Mock
    private InternshipRepository internshipRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private InternshipMapper internshipMapper;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private InternshipService internshipService;

    private final InternshipDto internshipDto = new InternshipDto();
    private Internship internship = new Internship();
    private final Project project = new Project();
    private TeamMember mentorTeamMember;
    private TeamMember firstInternTeamMember;
    private TeamMember secondInternTeamMember;
    private final Internship updatedInternship = new Internship();
    private final Long updatedInternshipId = 1L;

    @BeforeEach
    void setUp() {
        internshipDto.setMentorId(1L);
        internshipDto.setInternIds(List.of(2L, 3L));
        internshipDto.setProjectId(100L);
        internshipDto.setStartDate(LocalDateTime.now());
        internshipDto.setEndDate(LocalDateTime.now().plusMonths(2));

        internship = new Internship();
        internship.setId(1L);

        project.setId(100L);
        project.setStatus(ProjectStatus.CREATED);

        updatedInternship.setId(updatedInternshipId);
        updatedInternship.setStatus(InternshipStatus.IN_PROGRESS);
        updatedInternship.setStartDate(LocalDateTime.now());
        updatedInternship.setEndDate(LocalDateTime.now().plusMonths(2));

        Team commonTeam = new Team(1L, List.of(), project, null);
        mentorTeamMember = new TeamMember(1L, 1L, "Mentor", List.of(TeamRole.DEVELOPER), commonTeam, List.of());
        firstInternTeamMember = new TeamMember(2L, 2L, "Intern1", List.of(TeamRole.INTERN), commonTeam, List.of());
        secondInternTeamMember = new TeamMember(3L, 3L, "Intern2", List.of(TeamRole.INTERN), commonTeam, List.of());
    }

    @Test
    void shouldCreateInternship() {
        when(userContext.getUserId()).thenReturn(123L);
        when(projectRepository.findById(internshipDto.getProjectId())).thenReturn(Optional.of(project));
        when(teamMemberRepository.findByUserId(1L)).thenReturn(List.of(mentorTeamMember));
        when(teamMemberRepository.findAllByUserIdIn(List.of(2L, 3L)))
                .thenReturn(List.of(firstInternTeamMember, secondInternTeamMember));
        when(internshipMapper.toEntity(internshipDto)).thenReturn(internship);

        internshipService.createInternship(internshipDto);
        verify(internshipRepository).save(internship);
    }

    @Test
    void createInternshipFailsWhenProjectNotFound() {
        when(projectRepository.findById(100L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> internshipService.createInternship(internshipDto));

        assertEquals(internshipService.PROJECT_NOT_FOUND, exception.getMessage());
    }

    @Test
    void createInternshipFailsWhenMentorIsIntern() {
        internshipDto.setInternIds(List.of(1L, 2L));
        when(projectRepository.findById(100L)).thenReturn(Optional.of(project));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> internshipService.createInternship(internshipDto));

        assertEquals(internshipService.MENTOR_IS_INTERN, exception.getMessage());
    }

    @Test
    void createInternshipFailsWhenStartDateAfterEndDate() {
        internshipDto.setStartDate(LocalDateTime.now().plusDays(10));
        internshipDto.setEndDate(LocalDateTime.now());
        when(projectRepository.findById(100L)).thenReturn(Optional.of(project));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> internshipService.createInternship(internshipDto));

        assertEquals(internshipService.START_DATE_AFTER_END_DATE, exception.getMessage());
    }

    @Test
    void createInternshipFailsWhenDurationExceedsThreeMonths() {
        internshipDto.setEndDate(LocalDateTime.now().plusMonths(4));
        when(projectRepository.findById(100L)).thenReturn(Optional.of(project));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> internshipService.createInternship(internshipDto));

        assertEquals(internshipService.INTERNSHIP_TO_LONG, exception.getMessage());
    }

    @Test
    void createInternshipFailsWhenMentorNotFound() {
        when(projectRepository.findById(100L)).thenReturn(Optional.of(project));
        when(teamMemberRepository.findByUserId(1L)).thenReturn(List.of());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> internshipService.createInternship(internshipDto));

        assertEquals(internshipService.MENTOR_NOT_FOUND, exception.getMessage());
    }

    @Test
    void createInternshipFailsWhenInternNotFound() {
        when(projectRepository.findById(100L)).thenReturn(Optional.of(project));
        when(teamMemberRepository.findByUserId(1L)).thenReturn(List.of(mentorTeamMember));
        when(teamMemberRepository.findAllByUserIdIn(List.of(2L, 3L))).thenReturn(List.of(firstInternTeamMember));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> internshipService.createInternship(internshipDto));

        assertEquals(internshipService.INTERN_NOT_FOUND, exception.getMessage());
    }

    @Test
    void createInternshipFailsWhenMentorAndInternsDifferentTeams() {
        Team differentTeam = new Team();
        Project differentProject = new Project();
        project.setId(11L);
        differentTeam.setProject(differentProject);
        firstInternTeamMember.setTeam(differentTeam);


        when(projectRepository.findById(100L)).thenReturn(Optional.of(project));
        when(teamMemberRepository.findByUserId(1L)).thenReturn(List.of(mentorTeamMember));

        when(teamMemberRepository.findAllByUserIdIn(List.of(2L, 3L))).thenReturn(
                List.of(firstInternTeamMember, secondInternTeamMember));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> internshipService.createInternship(internshipDto));

        assertEquals(internshipService.MENTOR_AND_INTERNS_NOT_IN_SAME_TEAM, exception.getMessage());
    }

    @Test
    void getByIdSuccess() {
        when(internshipRepository.findById(1L)).thenReturn(Optional.of(internship));

        Internship result = internshipService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getByIdFailsWhenNotFound() {
        when(internshipRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> internshipService.getById(1L));

        assertEquals("Internship not found", exception.getMessage());
    }

    @Test
    void shouldUpdateInternshipFields() {
        InternshipUpdateDto dto = new InternshipUpdateDto();
        dto.setId(updatedInternshipId);
        dto.setName("Updated name");
        dto.setDescription("Updated description");
        dto.setScheduleId(100L);

        when(internshipRepository.findById(updatedInternshipId)).thenReturn(Optional.of(updatedInternship));

        Internship result = internshipService.updateInternship(updatedInternshipId, dto);

        assertEquals("Updated name", updatedInternship.getName());
        assertEquals("Updated description", updatedInternship.getDescription());
        assertEquals(updatedInternship, result);
    }

    @Test
    void updateInternshipThrowExceptionWhenInternshipIsCompleted() {
        updatedInternship.setStatus(InternshipStatus.COMPLETED);

        InternshipUpdateDto dto = new InternshipUpdateDto();
        dto.setId(updatedInternshipId);

        when(internshipRepository.findById(updatedInternshipId)).thenReturn(Optional.of(updatedInternship));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> internshipService.updateInternship(updatedInternshipId, dto));

        assertEquals(internshipService.COMPLETED_INTERNSHIP, ex.getMessage());
    }

    @Test
    void updateInternshipNotFailWhenOptionalFieldsAreNull() {
        InternshipUpdateDto dto = new InternshipUpdateDto();
        dto.setId(updatedInternshipId);

        when(internshipRepository.findById(updatedInternshipId)).thenReturn(Optional.of(updatedInternship));

        Internship result = internshipService.updateInternship(updatedInternshipId, dto);

        assertEquals(updatedInternship, result);
    }

    @Test
    void updateInternshipFailWhenStartDateAfterEndDate() {
        InternshipUpdateDto dto = new InternshipUpdateDto();
        dto.setId(updatedInternshipId);
        dto.setEndDate(LocalDateTime.now().minusDays(1));

        when(internshipRepository.findById(updatedInternshipId)).thenReturn(Optional.of(updatedInternship));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> internshipService.updateInternship(updatedInternshipId, dto));

        assertEquals(internshipService.START_DATE_AFTER_END_DATE, ex.getMessage());
    }

    @Test
    void updateInternshipFailWhenInternshipDurationExceedsThreeMonths() {
        InternshipUpdateDto dto = new InternshipUpdateDto();
        dto.setId(updatedInternshipId);
        dto.setEndDate(LocalDateTime.now().plusMonths(4));

        when(internshipRepository.findById(updatedInternshipId)).thenReturn(Optional.of(updatedInternship));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> internshipService.updateInternship(updatedInternshipId, dto));

        assertEquals(internshipService.INTERNSHIP_TO_LONG, ex.getMessage());
    }

    @Test
    void updateInternshipFailsWhenStartDateAfterEndDate() {
        InternshipUpdateDto dto = new InternshipUpdateDto();
        dto.setId(updatedInternshipId);
        dto.setEndDate(LocalDateTime.now().minusDays(10));

        when(internshipRepository.findById(updatedInternshipId)).thenReturn(Optional.of(updatedInternship));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> internshipService.updateInternship(updatedInternshipId, dto));

        assertEquals(internshipService.START_DATE_AFTER_END_DATE, ex.getMessage());
    }

    @Test
    void updateInternshipFailsWhenDurationTooLong() {
        InternshipUpdateDto dto = new InternshipUpdateDto();
        dto.setId(updatedInternshipId);
        dto.setEndDate(LocalDateTime.now().plusDays(100));

        when(internshipRepository.findById(updatedInternshipId)).thenReturn(Optional.of(updatedInternship));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> internshipService.updateInternship(updatedInternshipId, dto));

        assertEquals(internshipService.INTERNSHIP_TO_LONG, ex.getMessage());
    }

    @Test
    void updateInternshipFieldWhenTasksNotCompleted() {
        Long internId = 2L;
        TeamMember intern = new TeamMember();
        updatedInternship.setInterns(List.of(firstInternTeamMember));

        Task unfinishedTask = new Task();
        unfinishedTask.setStatus(TaskStatus.TODO);
        unfinishedTask.setPerformerUserId(internId);

        Project project = new Project();
        project.setTasks(List.of(unfinishedTask));

        updatedInternship.setProject(project);

        InternshipUpdateDto dto = new InternshipUpdateDto();
        dto.setId(updatedInternshipId);
        InternshipParticipantUpdateDto internDto = new InternshipParticipantUpdateDto();
        internDto.setInternId(internId);
        internDto.setPassed(true);
        internDto.setNewRole(TeamRole.DEVELOPER);


        dto.setInterns(List.of(internDto));

        when(internshipRepository.findById(updatedInternshipId)).thenReturn(Optional.of(updatedInternship));
        when(teamMemberRepository.findById(internId)).thenReturn(Optional.of(intern));

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> internshipService.updateInternship(updatedInternshipId, dto));

        assertEquals(internshipService.PRESENT_UNFINISHED_TASKS, ex.getMessage());
    }
}
