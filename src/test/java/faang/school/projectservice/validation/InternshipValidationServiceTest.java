package faang.school.projectservice.validation;

import faang.school.projectservice.dto.internship.InternshipCreateRequest;
import faang.school.projectservice.dto.internship.InternshipUpdateRequest;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InternshipValidationServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    private InternshipValidationService internshipValidationService;

    @BeforeEach
    void setUp() {
        internshipValidationService = new InternshipValidationService(projectRepository);
    }

    @Test
    void validateRequest_NullRequest_ThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> internshipValidationService.validateRequest(null)
        );
        assertEquals("Запрос не может быть пустым", exception.getMessage());
    }

    @Test
    void validateRequest_UnknownType_ThrowsException() {
        Object unknownRequest = new Object();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> internshipValidationService.validateRequest(unknownRequest)
        );
        assertEquals("Неизвестный тип запроса", exception.getMessage());
    }

    @Test
    void validateRequest_ValidInternshipCreateRequest_NoExceptionThrown() {
        InternshipCreateRequest createRequest = new InternshipCreateRequest(
                1L, 1L, 100L, List.of(1L,2L,3L),
                LocalDateTime.now(), LocalDateTime.now().plusMonths(2),
                "some",
                "name",
                TeamRole.DEVELOPER
        );

        Project project = new Project();
        Team team = new Team();
        TeamMember mentor = new TeamMember();
        mentor.setId(100L);
        team.setTeamMembers(List.of(mentor));
        project.setTeams(List.of(team));

        when(projectRepository.getById(1L)).thenReturn(project);

        assertDoesNotThrow(() -> internshipValidationService.validateRequest(createRequest));
    }

    @Test
    void validateRequest_CreateRequest_EndDateBeforeStartDate_ThrowsException() {
        InternshipCreateRequest createRequest = new InternshipCreateRequest(
                1L, 1L, 1L, List.of(1L,2L,3L),
                LocalDateTime.now().plusMonths(2), LocalDateTime.now(),
                "some",
                "name",
                TeamRole.DEVELOPER
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> internshipValidationService.validateRequest(createRequest)
        );

        assertEquals("Начало стажировки должно быть раньше чем ее конец", exception.getMessage());
    }

    @Test
    void validateRequest_CreateRequest_DurationMoreThanThreeMonths_ThrowsException() {
        InternshipCreateRequest createRequest = new InternshipCreateRequest(
                1L, 1L, 1L, List.of(1L,2L,3L),
                LocalDateTime.now(), LocalDateTime.now().plusMonths(4),
                "some",
                "name",
                TeamRole.DEVELOPER
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> internshipValidationService.validateRequest(createRequest)
        );

        assertEquals("Длительность стажировки не должна превышать 3-х месяцев", exception.getMessage());
    }

    @Test
    void validateRequest_CreateRequest_MentorNotInProjectTeam_ThrowsException() {
        InternshipCreateRequest createRequest = new InternshipCreateRequest(
                1L, 1L, 1L, List.of(1L,2L,3L),
                LocalDateTime.now(), LocalDateTime.now().plusMonths(2),
                "some",
                "name",
                TeamRole.DEVELOPER
        );

        Project project = new Project();
        Team team = new Team();
        TeamMember member = new TeamMember();
        member.setId(3L);
        team.setTeamMembers(List.of(member));
        project.setTeams(List.of(team));

        when(projectRepository.getById(1L)).thenReturn(project);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> internshipValidationService.validateRequest(createRequest)
        );

        assertEquals("Ментор должен состоять в команде проекта", exception.getMessage());
    }

    @Test
    void validateRequest_UpdateRequest_Valid_NoExceptionThrown() {
        InternshipUpdateRequest updateRequest = new InternshipUpdateRequest(
                1L, 1L, 1L, List.of(1L,2L,3L),
                LocalDateTime.now(), LocalDateTime.now().plusMonths(2),
                "some",
                "name",
                TeamRole.DEVELOPER
        );

        Project project = new Project();
        Team team = new Team();
        TeamMember member = new TeamMember();
        member.setId(1L);
        team.setTeamMembers(List.of(member));
        project.setTeams(List.of(team));

        when(projectRepository.getById(1L)).thenReturn(project);

        assertDoesNotThrow(() -> internshipValidationService.validateRequest(updateRequest));
    }

    @Test
    void validateRequest_UpdateRequest_EndDateBeforeStartDate_ThrowsException() {
        InternshipUpdateRequest updateRequest = new InternshipUpdateRequest(
                1L, 1L, 1L, List.of(1L,2L,3L),
                LocalDateTime.now().plusMonths(1), LocalDateTime.now(),
                "some",
                "name",
                TeamRole.DEVELOPER
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> internshipValidationService.validateRequest(updateRequest)
        );

        assertEquals("Начало стажировки должно быть раньше чем ее конец", exception.getMessage());
    }

    @Test
    void validateRequest_UpdateRequest_DurationMoreThanThreeMonths_ThrowsException() {
        InternshipUpdateRequest updateRequest = new InternshipUpdateRequest(
                1L, 1L, 1L, List.of(1L,2L,3L),
                LocalDateTime.now(), LocalDateTime.now().plusMonths(4),
                "some",
                "name",
                TeamRole.DEVELOPER
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> internshipValidationService.validateRequest(updateRequest)
        );

        assertEquals("Длительность стажировки не должна превышать 3-х месяцев", exception.getMessage());
    }

    @Test
    void validateRequest_UpdateRequest_MentorNotInProjectTeam_ThrowsException() {
        InternshipUpdateRequest updateRequest = new InternshipUpdateRequest(
                1L, 1L, 1L, List.of(1L,2L,3L),
                LocalDateTime.now(), LocalDateTime.now().plusMonths(2),
                "some",
                "name",
                TeamRole.DEVELOPER
        );

        Project project = new Project();
        Team team = new Team();
        TeamMember member = new TeamMember();
        member.setId(3L);
        team.setTeamMembers(List.of(member));
        project.setTeams(List.of(team));

        when(projectRepository.getById(1L)).thenReturn(project);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> internshipValidationService.validateRequest(updateRequest)
        );

        assertEquals("Ментор должен состоять в команде проекта", exception.getMessage());
    }
}