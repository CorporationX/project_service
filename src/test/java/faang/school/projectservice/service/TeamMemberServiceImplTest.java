package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.TeamMemberDto;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.TeamMemberMapperImpl;
import faang.school.projectservice.model.TeamMember;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamMemberServiceImplTest {

    @Mock
    private TeamMemberJpaRepository teamMemberRepository;

    @Spy
    private TeamMemberMapperImpl teamMemberMapper;

    @InjectMocks
    private TeamMemberServiceImpl teamMemberService;
    private long projectId;
    private long creatorId;

    @BeforeEach
    void setUp() {
        projectId = 2L;
        creatorId = 1L;
    }

    @Test
    void testGetTeamMember_Success() {
        TeamMember mockTeamMember = new TeamMember();
        TeamMemberDto mockTeamMemberDto = new TeamMemberDto();
        when(teamMemberRepository.findByUserIdAndProjectIdWithRole(creatorId, projectId))
                .thenReturn(Optional.of(mockTeamMember));

        TeamMemberDto result = teamMemberService.getTeamMember(creatorId, projectId);

        assertEquals(mockTeamMemberDto, result);
        verify(teamMemberRepository).findByUserIdAndProjectIdWithRole(creatorId, projectId);
        verify(teamMemberMapper).toDto(mockTeamMember);
    }

    @Test
    void testGetTeamMember_EntityNotFoundException() {
        String message = "Failed to find team member with creator id %d and project id %d".formatted(creatorId, projectId);
        when(teamMemberRepository.findByUserIdAndProjectIdWithRole(creatorId, projectId))
                .thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class,
                () -> teamMemberService.getTeamMember(creatorId, projectId));

        assertEquals(message, thrown.getMessage());
        verify(teamMemberRepository).findByUserIdAndProjectIdWithRole(creatorId, projectId);
    }
}
