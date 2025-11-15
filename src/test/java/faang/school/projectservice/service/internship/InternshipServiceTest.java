package faang.school.projectservice.service.internship;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static faang.school.projectservice.model.InternshipStatus.IN_PROGRESS;
import static faang.school.projectservice.model.TeamRole.OWNER;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InternshipServiceTest {

    @InjectMocks
    private InternshipService internshipService;

    @Mock
    private InternshipRepository internshipRepository;

    @Spy
    private InternshipMapper internshipDtoMapper =
            Mappers.getMapper(InternshipMapper.class);

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private UserContext userContext;

    private final TeamMember mentor = new TeamMember();
    private final Team team = new Team();
    private final List<Team> teams = new ArrayList<>();
    private final Project project = new Project();
    private final TeamMember intern = new TeamMember();
    private final List<TeamMember> interns = new ArrayList<>();

    private static final long DAYS_NUMBER = 1L;
    private static final long PROJECT_ID = 1L;
    private static final long MENTOR_ID = 2L;
    private static final long INTERNS = 3L;
    private static final long USER_ID = 4L;

    private CreateInternshipDto createInternshipDto() {
        return new CreateInternshipDto(
                "Yandex",
                "I'm gay",
                OWNER,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(DAYS_NUMBER),
                PROJECT_ID,
                MENTOR_ID,
                Collections.singletonList(INTERNS)
        );
    }

    @Test
    public void testCreateInternship_Success() {
        CreateInternshipDto dto = createInternshipDto();

        team.setTeamMembers(List.of(mentor));

        teams.add(team);

        project.setTeams(teams);
        project.setId(PROJECT_ID);
        mentor.setId(MENTOR_ID);

        intern.setId(INTERNS);
        interns.add(intern);

        when(projectRepository.findByIdOrThrow(PROJECT_ID)).thenReturn(project);
        when(teamMemberRepository.findMentorByIdOrThrow(MENTOR_ID)).thenReturn(mentor);
        when(teamMemberRepository.findAllById(dto.internsIds())).thenReturn(interns);
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(internshipRepository.save(any(Internship.class)))
                .thenAnswer(invocationOnMock -> {
                    return invocationOnMock.getArgument(0);
                });

        InternshipDto result = internshipService.createInternship(dto);

        assertEquals("Yandex", result.name());
        assertEquals("I'm gay", result.description());
        assertEquals(IN_PROGRESS, result.status());
        assertEquals(OWNER, result.role());
        assertTrue(Duration.between(result.startDate(), LocalDateTime.now()).abs().toMillis() <= 300);
        assertTrue(Duration.between(result.endDate(), LocalDateTime.now().plusDays(DAYS_NUMBER)).abs().toMillis() <= 300);
        assertEquals(PROJECT_ID, result.projectId());
        assertEquals(MENTOR_ID, result.mentorId());
        assertEquals(Collections.singletonList(INTERNS), result.internsIds());
        assertDoesNotThrow(() -> internshipService.createInternship(dto));
    }

    @Test
    public void testValidateInternsNotEmpty_Null() {
        CreateInternshipDto dto = createInternshipDto();

        team.setTeamMembers(List.of(mentor));

        teams.add(team);

        project.setTeams(teams);

        interns.add(intern);

        when(projectRepository.findByIdOrThrow(1L)).thenReturn(project);
        when(teamMemberRepository.findMentorByIdOrThrow(2L)).thenReturn(mentor);
        when(teamMemberRepository.findAllById(dto.internsIds())).thenReturn(null);

        assertThrows(EntityNotFoundException.class,
                () -> internshipService.createInternship(dto));
    }

    @Test
    public void testValidateInternsNotEmpty_Empty() {
        CreateInternshipDto dto = createInternshipDto();

        team.setTeamMembers(List.of(mentor));

        teams.add(team);

        project.setTeams(teams);

        when(projectRepository.findByIdOrThrow(1L)).thenReturn(project);
        when(teamMemberRepository.findMentorByIdOrThrow(2L)).thenReturn(mentor);
        when(teamMemberRepository.findAllById(dto.internsIds())).thenReturn(Collections.emptyList());

        assertThrows(EntityNotFoundException.class,
                () -> internshipService.createInternship(dto));
    }
}