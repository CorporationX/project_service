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

    @Test
    public void testCreateInternship_Success() {
        CreateInternshipDto dto = new CreateInternshipDto(
                "Yandex",
                "I'm gay",
                IN_PROGRESS,
                OWNER,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                1L,
                2L,
                Collections.singletonList(3L)
        );

        TeamMember mentor = new TeamMember();

        Team team = new Team();
        team.setTeamMembers(List.of(mentor));

        List<Team> teams = new ArrayList<>();
        teams.add(team);

        Project project = new Project();
        project.setTeams(teams);

        TeamMember intern = new TeamMember();

        List<TeamMember> interns = new ArrayList<>();
        interns.add(intern);

        Mockito.when(projectRepository.findByIdOrThrow(1L)).thenReturn(project);
        Mockito.when(teamMemberRepository.findMentorByIdOrThrow(2L)).thenReturn(mentor);
        Mockito.when(teamMemberRepository.findAllById(dto.internsIds())).thenReturn(interns);
        Mockito.when(internshipRepository.save(Mockito.any(Internship.class)))
                .thenAnswer(invocationOnMock -> {
                    return invocationOnMock.getArgument(0);
                });

        InternshipDto result = internshipService.createInternship(dto);

        Assertions.assertEquals("I'm gay", result.description());
        Assertions.assertDoesNotThrow(() -> internshipService.createInternship(dto));
    }

    @Test
    public void testValidateInternsNotEmpty_Null() {
        CreateInternshipDto dto = new CreateInternshipDto(
                "Yandex",
                "I'm gay",
                IN_PROGRESS,
                OWNER,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                1L,
                2L,
                Collections.singletonList(3L)
        );

        TeamMember mentor = new TeamMember();

        Team team = new Team();
        team.setTeamMembers(List.of(mentor));

        List<Team> teams = new ArrayList<>();
        teams.add(team);

        Project project = new Project();
        project.setTeams(teams);

        TeamMember intern = new TeamMember();

        List<TeamMember> interns = new ArrayList<>();
        interns.add(intern);

        Mockito.when(projectRepository.findByIdOrThrow(1L)).thenReturn(project);
        Mockito.when(teamMemberRepository.findMentorByIdOrThrow(2L)).thenReturn(mentor);
        Mockito.when(teamMemberRepository.findAllById(dto.internsIds())).thenReturn(null);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> internshipService.createInternship(dto));
    }

    @Test
    public void testValidateInternsNotEmpty_Empty() {
        CreateInternshipDto dto = new CreateInternshipDto(
                "Yandex",
                "I'm gay",
                IN_PROGRESS,
                OWNER,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                1L,
                2L,
                Collections.singletonList(3L)
        );

        TeamMember mentor = new TeamMember();

        Team team = new Team();
        team.setTeamMembers(List.of(mentor));

        List<Team> teams = new ArrayList<>();
        teams.add(team);

        Project project = new Project();
        project.setTeams(teams);

        List<TeamMember> interns = new ArrayList<>();

        Mockito.when(projectRepository.findByIdOrThrow(1L)).thenReturn(project);
        Mockito.when(teamMemberRepository.findMentorByIdOrThrow(2L)).thenReturn(mentor);
        Mockito.when(teamMemberRepository.findAllById(dto.internsIds())).thenReturn(interns);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> internshipService.createInternship(dto));
    }
}