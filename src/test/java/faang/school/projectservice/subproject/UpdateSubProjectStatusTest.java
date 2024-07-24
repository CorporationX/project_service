package faang.school.projectservice.subproject;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.mapper.TeamMapper;
import faang.school.projectservice.mapper.TeamMapperImpl;
import faang.school.projectservice.mapper.TeamMemberMapper;
import faang.school.projectservice.mapper.TeamMemberMapperImpl;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.service.project.update_subproject_param.UpdateSubProjectStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateSubProjectStatusTest {
    @Mock
    private ProjectMapper mapper;
    @Mock
    private TeamMapper teamMapper;
    @Mock
    private TeamMemberMapper teamMemberMapper;
    @Mock
    private MomentRepository momentRepository;
    @InjectMocks
    private UpdateSubProjectStatus updateSubProjectStatus;
    private UpdateSubProjectDto paramDto;
    private ProjectDto projectDto;
    @Captor
    private ArgumentCaptor<Moment> momentCaptor;

    @BeforeEach
    public void setup() {
        paramDto = new UpdateSubProjectDto();
        projectDto = new ProjectDto();
    }

    @Test
    public void testIsExecutable_statusNull() {
        boolean expected = false;

        boolean result = updateSubProjectStatus.isExecutable(paramDto);

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void testIsExecute_statusNotNull() {
        boolean expected = true;
        paramDto.setStatus(ProjectStatus.CREATED);

        boolean result = updateSubProjectStatus.isExecutable(paramDto);

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void testExecute_NotEqualChildrenStatus() {
        ProjectDto childOne = new ProjectDto();
        childOne.setStatus(ProjectStatus.CREATED);
        ProjectDto child2 = new ProjectDto();
        child2.setStatus(ProjectStatus.COMPLETED);
        projectDto.setChildren(new ArrayList<>(List.of(childOne, child2)));
        paramDto.setStatus(ProjectStatus.COMPLETED);

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            updateSubProjectStatus.execute(projectDto, paramDto);
        });
        Assertions.assertEquals("Не все подпроекты имеют указанный статус", exception.getMessage());
    }

    @Test
    public void testExecute_MomentNotExist() {
        ProjectDto childOne = new ProjectDto();
        childOne.setStatus(ProjectStatus.COMPLETED);
        ProjectDto child2 = new ProjectDto();
        child2.setStatus(ProjectStatus.COMPLETED);
        projectDto.setChildren(new ArrayList<>(List.of(childOne, child2)));
        paramDto.setStatus(ProjectStatus.COMPLETED);
        projectDto.setResourceIds(new ArrayList<>(List.of(1L, 2L, 3L)));
        projectDto.setMomentIds(new ArrayList<>());
        TeamDto team1 = new TeamDto();
        TeamDto team2 = new TeamDto();
        team1.setProject(projectDto);
        team2.setProject(projectDto);
        team1.setTeamMembers(new ArrayList<>(List.of(
                new TeamMemberDto(1L, 1L),
                // new TeamMemberDto(1L, 1L, team1),
                new TeamMemberDto(2L, 2L)
                // new TeamMemberDto(2L, 2L, team1)
        )));
        team2.setTeamMembers(new ArrayList<>(List.of(
                new TeamMemberDto(3L, 3L),
                // new TeamMemberDto(3L, 3L, team2),
                new TeamMemberDto(4L, 4L)
                // new TeamMemberDto(4L, 4L, team2)
        )));
        projectDto.setTeams(new ArrayList<>(List.of(team1, team2)));
        Moment moment = new Moment();
        String completedMomentName = "Выполнены все подпроекты";
        moment.setName(completedMomentName);
        moment.setDescription(completedMomentName);
        Resource res1 = new Resource();
        res1.setId(1L);
        Resource res2 = new Resource();
        res1.setId(2L);
        Resource res3 = new Resource();
        res1.setId(3L);
        moment.setResource(new ArrayList<>(
                List.of(res1, res2, res3)
        ));
        Project project = new Project();
        Project childPrj1 = new Project();
        childOne.setStatus(ProjectStatus.COMPLETED);
        Project childPrj2 = new Project();
        child2.setStatus(ProjectStatus.COMPLETED);
        project.setChildren(new ArrayList<>(List.of(childPrj1, childPrj2)));
        project.setMoments(new ArrayList<>());
        Team teamPrj1 = new Team();
        Team teamPrj2 = new Team();
        teamPrj1.setProject(project);
        teamPrj2.setProject(project);
        TeamMember tm1 = new TeamMember();
        tm1.setId(1L);
        tm1.setUserId(1L);
        tm1.setTeam(teamPrj1);
        TeamMember tm2 = new TeamMember();
        tm2.setId(2L);
        tm2.setUserId(2L);
        tm2.setTeam(teamPrj1);
        TeamMember tm3 = new TeamMember();
        tm3.setId(3L);
        tm3.setUserId(3L);
        tm3.setTeam(teamPrj2);
        TeamMember tm4 = new TeamMember();
        tm4.setId(4L);
        tm4.setUserId(4L);
        tm4.setTeam(teamPrj2);

        teamPrj1.setTeamMembers(new ArrayList<>(List.of(tm1, tm2)));
        teamPrj2.setTeamMembers(new ArrayList<>(List.of(tm3, tm4)));
        project.setTeams(new ArrayList<>(List.of(teamPrj1, teamPrj2)));

        when(momentRepository.findByName(completedMomentName)).thenReturn(null);
        when(mapper.toEntity(projectDto)).thenReturn(project);
        lenient().when(momentRepository.save(any())).thenReturn(moment);
        updateSubProjectStatus.execute(projectDto, paramDto);
        verify(momentRepository, times(1)).save(momentCaptor.capture());
        Assertions.assertEquals(moment, momentCaptor.getValue());
    }
}
