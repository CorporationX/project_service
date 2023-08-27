package faang.school.projectservice.service;

import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TeamMemberServiceTest {

    @InjectMocks()
    private TeamMemberService teamMemberService;

    @Mock
    private TeamMemberJpaRepository teamMemberJpaRepository;

    @Test
    void testGetTeamMemberByUserIdAndProjectId() {
        teamMemberService.getTeamMemberByUserIdAndProjectId(1L, 1L);
        Mockito.verify(teamMemberJpaRepository).findByUserIdAndProjectId(1L, 1L);
    }
}