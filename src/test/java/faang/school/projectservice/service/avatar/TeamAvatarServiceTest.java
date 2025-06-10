package faang.school.projectservice.service.avatar;

import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.exception.SizeLimitException;
import faang.school.projectservice.mapper.team.TeamMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.service.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamAvatarServiceTest {
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Spy
    private TeamMapperImpl teamMapper;
    @Mock
    private S3Service s3Service;
    @InjectMocks
    private TeamAvatarService teamAvatarService;

    private Long teamId;
    private Long projectId;
    private TeamDto teamDto;
    private MultipartFile avatar;
    private Team team;
    private TeamMember teamMember;
    private Project project;

    @BeforeEach
    void setUp() {
        teamId = 1L;
        projectId = 1L;
        teamDto = new TeamDto(teamId, projectId, null);
        avatar = new MockMultipartFile(
                "avatar",
                "test-avatar.png",
                "image/png",
                new byte[1000]
        );

        project = Project.builder()
                .id(1L)
                .description("Test Project")
                .maxStorageSize(BigInteger.valueOf(1000000L))
                .storageSize(BigInteger.valueOf(100L))
                .build();

        team = Team.builder().id(1L)
                .project(mock(Project.class))
                .teamMembers(List.of())
                .avatarKey("avatar-test.png")
                .build();

        teamMember = TeamMember.builder()
                .id(1L)
                .userId(1L)
                .nickname("Test User")
                .roles(List.of(TeamRole.MANAGER))
                .team(team)
                .build();
    }

    @Test
    void testSuccessfulCreateAvatar() {
        Long id = 1L;
        team.setProject(project);
        when(teamRepository.findById(anyLong())).thenReturn(Optional.of(team));
        when(teamRepository.save(any())).thenReturn(team);
        when(s3Service.uploadFile(any(), anyString())).thenReturn(team.getAvatarKey());

        assertEquals(teamMapper.toDto(team),teamAvatarService.createAvatar(avatar, id));
        verify(teamRepository, times(2)).findById(anyLong());
        verify(teamRepository).save(any());
    }

    @Test
    void testCreateAvatarWithInnexistentTeam() {
        Long id = 1L;
        when(teamRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> teamAvatarService.createAvatar(avatar, id));
    }

    @Test
    void testCreateAvatarWithNoStorageSize() {
        Long id = 1L;
        team.setProject(project);
        project.setStorageSize(BigInteger.valueOf(1000000L));
        when(teamRepository.findById(anyLong())).thenReturn(Optional.of(team));

        assertThrows(SizeLimitException.class, () -> teamAvatarService.createAvatar(avatar, id));
    }

    @Test
    void testSuccessfulGetTeamAvatar() {
        Long id = 1L;
        when(teamRepository.findById(anyLong())).thenReturn(Optional.of(team));
        when(s3Service.downloadFile(team.getAvatarKey())).thenReturn(mock(InputStream.class));

        InputStream result = teamAvatarService.getTeamAvatar(id);

        assertNotNull(result);
        verify(teamRepository, times(2)).findById(anyLong());
        verify(s3Service).downloadFile(anyString());
    }

    @Test
    void testGetTeamAvatarWithInnexistentTeam() {
        Long id = 1L;
        when(teamRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> teamAvatarService.createAvatar(avatar, id));
    }

    @Test
    void testGetTeamAvatarWithNoAvatar() {
        Long id = 1L;
        team.setAvatarKey(null);
        when(teamRepository.findById(anyLong())).thenReturn(Optional.of(team));

        assertThrows(NoSuchElementException.class, () -> teamAvatarService.getTeamAvatar(id));
    }

    @Test
    void testSuccessfulDeleteAvatar() {
        Long id = 1L;
        when(teamRepository.findById(anyLong())).thenReturn(Optional.of(team));
        when(teamMemberRepository.findByUserIdAndProjectId(anyLong(), anyLong())).thenReturn(teamMember);
        when(s3Service.deleteFile(anyString())).thenReturn(true);

        assertTrue(teamAvatarService.deleteAvatar(id, id));
        verify(teamRepository, times(2)).findById(anyLong());
        verify(teamMemberRepository).findByUserIdAndProjectId(anyLong(), anyLong());
        verify(s3Service).deleteFile(anyString());
    }

    @Test
    void testDeleteAvatarWithInnexistentTeam() {
        Long id = 1L;
        when(teamRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> teamAvatarService.deleteAvatar(id, id));
    }

    @Test
    void testDeleteAvatarWithNoAccess() {
        Long id = 1L;
        teamMember.setRoles(List.of());
        when(teamRepository.findById(anyLong())).thenReturn(Optional.of(team));
        when(teamMemberRepository.findByUserIdAndProjectId(anyLong(), anyLong())).thenReturn(teamMember);

        assertThrows(AccessDeniedException.class, () -> teamAvatarService.deleteAvatar(id, id));
    }
}