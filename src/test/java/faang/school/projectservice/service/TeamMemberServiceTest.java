package faang.school.projectservice.service;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.team_member.TeamMemberService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TeamMemberServiceTest {
    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    TeamMemberService teamMemberService;

    private TeamMember teamMember;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        teamMember = TeamMember.builder().id(1L).userId(2L).build();
    }

    @Test
    void testFindById_Found() {
        when(teamMemberRepository.findById(1L)).thenReturn(teamMember);

        TeamMember result = teamMemberService.findById(1L);

        assertNotNull(result, "TeamMember should not be null");
        assertEquals(1L, result.getId());
        assertEquals(2L, result.getUserId());

        verify(teamMemberRepository).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        when(teamMemberRepository.findById(1L)).thenReturn(null);

        doThrow(new EntityNotFoundException("Team member doesn't exist by id: %s")).
                when(teamMemberRepository).findById(1L);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> teamMemberService.findById(1L));

        assertEquals("Team member doesn't exist by id: %s", exception.getMessage());
        verify(teamMemberRepository).findById(1L);
    }

    @Test
    void testExistedTeamMember_Exists() {
        when(teamMemberRepository.existedTeamMember(1L)).thenReturn(true);

        boolean result = teamMemberService.existedTeamMember(1L);

        verify(teamMemberRepository).existedTeamMember(1L);
    }

    @Test
    void testExistedTeamMember_NotExists() {
        when(teamMemberRepository.existedTeamMember(1L)).thenReturn(false);

        boolean result = teamMemberService.existedTeamMember(1L);

        verify(teamMemberRepository).existedTeamMember(1L);
    }
}
