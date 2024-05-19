package faang.school.projectservice.service.teamMember;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TeamMemberServiceImplTest {
    public static final long TEAM_MEMBER_ID = 1L;
    public static final long PROJECT_ID = 1L;

    @Mock
    public TeamMemberRepository teamMemberRepository;
    @InjectMocks
    public TeamMemberServiceImpl teamMemberService;
    public TeamMember teamMember;

    @BeforeEach
    public void setUp() {
        teamMember = new TeamMember();
        teamMember.setId(TEAM_MEMBER_ID);
        teamMember.setRoles(Collections.emptyList());
    }

    @Test
    public void whenFindByIdTeamMemberAndNotExistsThenThrowException() {
        when(teamMemberRepository.findById(TEAM_MEMBER_ID)).thenThrow(EntityNotFoundException.class);
        Assert.assertThrows(EntityNotFoundException.class,
                () -> teamMemberService.findById(TEAM_MEMBER_ID));
    }

    @Test
    public void whenFindByIdTeamMemberAndExistsThenReturnTeamMember() {
        when(teamMemberRepository.findById(TEAM_MEMBER_ID)).thenReturn(teamMember);
        TeamMember actual = teamMemberService.findById(TEAM_MEMBER_ID);
        assertThat(actual).isEqualTo(teamMember);
    }

    @Test
    void whenFindByUserIdAndProjectIdAndTeamMemberNotExistThenThrowException() {
        when(teamMemberRepository.findByUserIdAndProjectId(TEAM_MEMBER_ID, PROJECT_ID)).thenThrow(EntityNotFoundException.class);
        Assert.assertThrows(EntityNotFoundException.class,
                () -> teamMemberService.findByUserIdAndProjectId(TEAM_MEMBER_ID, PROJECT_ID));
    }

    @Test
    void whenFindByUserIdAndProjectIdAndTeamMemberExistThenReturnTeamMember() {
        when(teamMemberRepository.findByUserIdAndProjectId(TEAM_MEMBER_ID, PROJECT_ID)).thenReturn(teamMember);
        TeamMember actual = teamMemberService.findByUserIdAndProjectId(TEAM_MEMBER_ID, PROJECT_ID);
        assertThat(actual).isEqualTo(teamMember);
    }

    @Test
    void whenDeleteByIdAndTeamMemberNotExistThenThrowException() {
        when(teamMemberRepository.findById(TEAM_MEMBER_ID)).thenThrow(EntityNotFoundException.class);
        Assert.assertThrows(EntityNotFoundException.class,
                () -> teamMemberService.deleteById(TEAM_MEMBER_ID));
    }

    @Test
    void whenDeleteByIdSuccessfully() {
        when(teamMemberRepository.findById(TEAM_MEMBER_ID)).thenReturn(teamMember);
        teamMemberService.deleteById(TEAM_MEMBER_ID);
        verify(teamMemberRepository).deleteById(TEAM_MEMBER_ID);
    }
}