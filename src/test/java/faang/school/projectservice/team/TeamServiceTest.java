package faang.school.projectservice.team;

import faang.school.projectservice.exception.DataValidateException;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.adapter.TeamRepositoryAdapter;
import faang.school.projectservice.service.MinioService;
import faang.school.projectservice.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepositoryAdapter teamRepositoryAdapter;

    @Mock
    private MinioService minioService;

    @InjectMocks
    private TeamService teamService;

    private Team team;

    @BeforeEach
    void setUp() {
        team = new Team();
        team.setId(1L);
        TeamMember member = new TeamMember();
        member.setUserId(2L);
        member.setRoles(List.of(TeamRole.MANAGER));
        team.setTeamMembers(List.of(member));
    }

    @Test
    void uploadAvatar_ShouldUpload_WhenUserIsInTeam() {
        MultipartFile file = mock(MultipartFile.class);
        when(teamRepositoryAdapter.getById(1L)).thenReturn(team);
        when(minioService.uploadFile(file)).thenReturn("avatar-key");

        teamService.uploadAvatar(1L, file, 2L);

        assertEquals("avatar-key", team.getAvatarKey());
    }

    @Test
    void uploadAvatar_ShouldThrowException_WhenUserNotInTeam() {
        MultipartFile file = mock(MultipartFile.class);
        when(teamRepositoryAdapter.getById(1L)).thenReturn(team);

        DataValidateException exception = assertThrows(DataValidateException.class, () ->
                teamService.uploadAvatar(1L, file, 3L));

        assertEquals("You're not in this team", exception.getMessage());
    }

    @Test
    void getAvatar_ShouldReturnAvatar_WhenExists() throws IOException {
        team.setAvatarKey("avatar-key");
        when(teamRepositoryAdapter.getById(1L)).thenReturn(team);
        byte[] expectedAvatarBytes = new byte[]{1, 2, 3};
        InputStream avatarStream = new ByteArrayInputStream(expectedAvatarBytes);
        when(minioService.getFile("avatar-key")).thenReturn(avatarStream);

        InputStream resultStream = teamService.getAvatar(1L);
        byte[] actualAvatarBytes = resultStream.readAllBytes();

        assertArrayEquals(expectedAvatarBytes, actualAvatarBytes);
    }

    @Test
    void deleteAvatar_ShouldDelete_WhenUserIsManager() {
        team.setAvatarKey("avatar-key");
        when(teamRepositoryAdapter.getById(1L)).thenReturn(team);

        teamService.deleteAvatar(1L, 2L);

        verify(minioService, times(1)).deleteFile("avatar-key");
        assertNull(team.getAvatarKey());
    }

    @Test
    void deleteAvatar_ShouldThrowException_WhenUserNotManager() {
        TeamMember nonManager = new TeamMember();
        nonManager.setUserId(3L);
        nonManager.setRoles(Collections.emptyList());
        team.setTeamMembers(List.of(nonManager));

        when(teamRepositoryAdapter.getById(1L)).thenReturn(team);

        DataValidateException exception = assertThrows(DataValidateException.class, () ->
                teamService.deleteAvatar(1L, 3L));

        assertEquals("You are not a team manager!", exception.getMessage());
        verify(minioService, never()).deleteFile(anyString());
    }
}