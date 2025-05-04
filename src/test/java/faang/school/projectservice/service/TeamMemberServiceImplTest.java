package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.teammember.TeamMemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.AccessDeniedException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamMemberServiceImplTest {

    @Mock
    private UserContext userContext;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private TeamMemberServiceImpl teamMemberService;

    @BeforeEach
    void setUp() {
        when(userContext.getUserId()).thenReturn(1L);
    }

    @Test
    void testGetCurrentTeamMemberSuccess() throws AccessDeniedException {
        TeamMember teamMember = new TeamMember();
        when(teamMemberRepository.findByUserIdAndProjectId(1L, 1L))
                .thenReturn(Optional.of(teamMember));

        TeamMember result = teamMemberService.getCurrentTeamMember(1L);

        assertNotNull(result);
        assertEquals(teamMember, result);
    }

    @Test
    void testGetCurrentTeamMemberAccessDenied() {
        when(teamMemberRepository.findByUserIdAndProjectId(1L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(AccessDeniedException.class,
                () -> teamMemberService.getCurrentTeamMember(1L));
    }
}