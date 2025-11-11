package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.mapper.internship.InternshipDtoMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static faang.school.projectservice.model.InternshipStatus.IN_PROGRESS;
import static faang.school.projectservice.model.TeamRole.OWNER;

@ExtendWith(MockitoExtension.class)
class InternshipServiceTest {

    @InjectMocks
    private InternshipService internshipService;

    @Mock
    private InternshipRepository internshipRepository;

    @Spy
    private InternshipDtoMapper internshipDtoMapper =
            Mappers.getMapper(InternshipDtoMapper.class);

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    private final TeamMember mentor = new TeamMember();
    private final Team team = new Team();
    private final List<Team> teams = new ArrayList<>();
    private final Project project = new Project();
    private final TeamMember intern = new TeamMember();
    private final List<TeamMember> interns = new ArrayList<>();

    private final long DAYS_NUMBER = 1L;
    private final long PROJECT_ID = 1L;
    private final long MENTOR_ID = 2L;
    private final long INTERNS = 3L;

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

        Mockito.when(projectRepository.findByIdOrThrow(PROJECT_ID)).thenReturn(project);
        Mockito.when(teamMemberRepository.findMentorByIdOrThrow(MENTOR_ID)).thenReturn(mentor);
        Mockito.when(teamMemberRepository.findAllById(dto.internsIds())).thenReturn(interns);
        Mockito.when(internshipRepository.save(Mockito.any(Internship.class)))
                .thenAnswer(invocationOnMock -> {
                    return invocationOnMock.getArgument(0);
                });

        InternshipDto result = internshipService.createInternship(dto);

        Assertions.assertEquals("Yandex", result.name());
        Assertions.assertEquals("I'm gay", result.description());
        Assertions.assertEquals(IN_PROGRESS, result.status());
        Assertions.assertEquals(OWNER, result.role());
        Assertions.assertTrue(Duration.between(result.startDate(), LocalDateTime.now()).abs().toMillis() <= 100);
        Assertions.assertTrue(Duration.between(result.endDate(), LocalDateTime.now().plusDays(DAYS_NUMBER)).abs().toMillis() <= 100);
        Assertions.assertEquals(PROJECT_ID, result.projectId());
        Assertions.assertEquals(MENTOR_ID, result.mentorId());
        Assertions.assertEquals(Collections.singletonList(INTERNS), result.internsIds());
        Assertions.assertDoesNotThrow(() -> internshipService.createInternship(dto));
    }

    @Test
    public void testValidateInternsNotEmpty_Null() {
        CreateInternshipDto dto = createInternshipDto();

        team.setTeamMembers(List.of(mentor));

        teams.add(team);

        project.setTeams(teams);

        interns.add(intern);

        Mockito.when(projectRepository.findByIdOrThrow(1L)).thenReturn(project);
        Mockito.when(teamMemberRepository.findMentorByIdOrThrow(2L)).thenReturn(mentor);
        Mockito.when(teamMemberRepository.findAllById(dto.internsIds())).thenReturn(null);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> internshipService.createInternship(dto));
    }

    @Test
    public void testValidateInternsNotEmpty_Empty() {
        CreateInternshipDto dto = createInternshipDto();

        team.setTeamMembers(List.of(mentor));

        teams.add(team);

        project.setTeams(teams);

        Mockito.when(projectRepository.findByIdOrThrow(1L)).thenReturn(project);
        Mockito.when(teamMemberRepository.findMentorByIdOrThrow(2L)).thenReturn(mentor);
        Mockito.when(teamMemberRepository.findAllById(dto.internsIds())).thenReturn(Collections.emptyList());

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> internshipService.createInternship(dto));
    }
}