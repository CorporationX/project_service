package faang.school.projectservice.service.teamMember;

import faang.school.projectservice.exception.NoSuchTeamMemberException;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TeamMemberServiceTest {
    @Mock
    TeamMemberRepository teamMemberRepository;
    @InjectMocks
    TeamMemberService teamMemberService;

    @Test
    void getTeamMemberByIdAndProjectId() {
        TeamMember teamMember = TeamMember.builder()
                .id(1l)
                .team(Team.builder()
                        .id(1l)
                        .build())
                .build();

        Mockito.when(teamMemberRepository.findByUserIdAndProjectId(1L, 1L)).thenReturn(teamMember);
        TeamMember actual = teamMemberService.getTeamMemberByIdAndProjectId(1L, 1L);
        Assertions.assertEquals(teamMember, actual);
        Mockito.verify(teamMemberRepository, Mockito.times(1))
                .findByUserIdAndProjectId(1L, 1L);
    }

    @Test
    void getTeamMemberByIdAndProjectIdNegative() {
        Mockito.when(teamMemberRepository.findByUserIdAndProjectId(1L, 1L)).thenReturn(null);
        Assertions.assertThrows(NoSuchTeamMemberException.class,
                () -> teamMemberService.getTeamMemberByIdAndProjectId(1L, 1L),
                ("No team member found with id 1 in project 1"));
        Mockito.verify(teamMemberRepository, Mockito.times(1))
                .findByUserIdAndProjectId(1L, 1L);
    }
}