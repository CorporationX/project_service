package faang.school.projectservice.service.utilservice;

import faang.school.projectservice.exception.NotFoundException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.TeamMember;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamMemberUtilServiceTest {

    @InjectMocks
    private TeamMemberUtilService teamMemberUtilService;

    @Mock
    private TeamMemberJpaRepository teamMemberJpaRepository;

    @Test
    void testGetByUserIdAndProjectId() {
        long userId = 121L;
        long projectId = 11L;
        TeamMember teamMember = TeamMember.builder()
                .id(21L)
                .userId(userId)
                .build();

        when(teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId)).thenReturn(Optional.of(teamMember));

        TeamMember result = teamMemberUtilService.getByUserIdAndProjectId(userId, projectId);

        assertEquals(teamMember, result);
        verify(teamMemberJpaRepository, times(1)).findByUserIdAndProjectId(userId, projectId);
    }

    @Test
    void testGetByUserIdAndProjectId_notExists_throws() {
        long userId = 121L;
        long projectId = 11L;

        when(teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> teamMemberUtilService.getByUserIdAndProjectId(userId, projectId));
        verify(teamMemberJpaRepository, times(1)).findByUserIdAndProjectId(userId, projectId);
    }

}