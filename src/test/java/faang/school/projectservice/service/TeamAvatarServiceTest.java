package faang.school.projectservice.service;

import faang.school.projectservice.config.minio.properties.TeamAvatarProperties;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.adapter.TeamRepositoryAdapter;
import faang.school.projectservice.service.team.TeamAvatarService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import static faang.school.projectservice.constant.ImageTestConstants.IMAGE_MOCK_MULTIPART_FILE;

@ExtendWith(MockitoExtension.class)
class TeamAvatarServiceTest {

    @Mock
    private TeamRepositoryAdapter teamRepositoryAdapter;

    @Mock
    private MinioService minioService;

    @Spy
    private TeamAvatarProperties teamAvatarProperties;

    @InjectMocks
    private TeamAvatarService teamService;

    private Team team;

    @BeforeEach
    void setUp() {
        team = new Team();
        team.setId(1L);
        TeamMember member = new TeamMember();
        member.setUserId(2L);
        member.setRoles(List.of(TeamRole.MANAGER));
        team.setTeamMembers(List.of(member));

        teamAvatarProperties.setMaxFileSize(5242880);
        teamAvatarProperties.setMaxImageSize(512);
        teamAvatarProperties.setFolderName("team_avatar");
    }

    @Test
    void uploadAvatar_ShouldUpload_WhenUserIsInTeam() {
        Mockito.when(teamRepositoryAdapter.getById(1L)).thenReturn(team);
        Mockito.doNothing().when(minioService).uploadFile(Mockito.anyString(), Mockito.any(),
                Mockito.eq(IMAGE_MOCK_MULTIPART_FILE.getSize()),
                Mockito.eq(IMAGE_MOCK_MULTIPART_FILE.getContentType()));

        teamService.uploadAvatar(1L, IMAGE_MOCK_MULTIPART_FILE, 2L);

        Mockito.verify(teamRepositoryAdapter, Mockito.times(1)).getById(1L);
        Mockito.verify(minioService, Mockito.times(1))
                .uploadFile(Mockito.anyString(), Mockito.any(),
                        Mockito.eq(IMAGE_MOCK_MULTIPART_FILE.getSize()),
                        Mockito.eq(IMAGE_MOCK_MULTIPART_FILE.getContentType()));
    }

    @Test
    void uploadAvatar_ShouldThrowException_WhenUserNotInTeam() {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(teamRepositoryAdapter.getById(1L)).thenReturn(team);

        DataValidationException exception = Assertions.assertThrows(DataValidationException.class, () ->
                teamService.uploadAvatar(1L, file, 3L));

        Assertions.assertEquals("You're not in this team", exception.getMessage());
    }

    @Test
    void getAvatar_ShouldReturnAvatar_WhenExists() throws IOException {
        team.setAvatarKey("avatar-key");
        Mockito.when(teamRepositoryAdapter.getById(1L)).thenReturn(team);
        byte[] expectedAvatarBytes = new byte[]{1, 2, 3};
        InputStream avatarStream = new ByteArrayInputStream(expectedAvatarBytes);
        Mockito.when(minioService.getFile("avatar-key")).thenReturn(avatarStream);

        InputStream resultStream = teamService.getAvatar(1L);
        byte[] actualAvatarBytes = resultStream.readAllBytes();

        Assertions.assertArrayEquals(expectedAvatarBytes, actualAvatarBytes);
    }

    @Test
    void deleteAvatar_ShouldDelete_WhenUserIsManager() {
        team.setAvatarKey("avatar-key");
        Mockito.when(teamRepositoryAdapter.getById(1L)).thenReturn(team);

        teamService.deleteAvatar(1L, 2L);

        Mockito.verify(minioService, Mockito.times(1)).deleteFile("avatar-key");
        Assertions.assertNull(team.getAvatarKey());
    }

    @Test
    void deleteAvatar_ShouldThrowException_WhenUserNotManager() {
        TeamMember nonManager = new TeamMember();
        nonManager.setUserId(3L);
        nonManager.setRoles(Collections.emptyList());
        team.setTeamMembers(List.of(nonManager));

        Mockito.when(teamRepositoryAdapter.getById(1L)).thenReturn(team);

        DataValidationException exception = Assertions.assertThrows(DataValidationException.class, () ->
                teamService.deleteAvatar(1L, 3L));

        Assertions.assertEquals("You are not a team manager!", exception.getMessage());
        Mockito.verify(minioService, Mockito.never()).deleteFile(Mockito.anyString());
    }
}