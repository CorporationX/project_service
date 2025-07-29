package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.config.property.ImageProperty;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.NotImageException;
import faang.school.projectservice.exception.ResourceNotReceivedException;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.validator.ResourceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
class TeamServiceTest {
    private TeamService teamService;
    private ImageProperty imageProperty = new ImageProperty(512, 1);
    @Mock
    private UserContext userContext;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private S3Service s3Service;
    @Mock
    private TeamRepository teamRepository;
    @Spy
    private ResourceValidator resourceValidator;
    @Captor
    private ArgumentCaptor<MultipartFile> multipartFileCaptor;
    @Captor
    private ArgumentCaptor<Team> teamCaptor;
    @Captor
    private ArgumentCaptor<String> keyCaptor;

    private static final long TEAM_ID = 1;
    private static final long USER_ID = 2;
    private static final long MANAGER_ID = 3;
    private static final long OTHER_USER_ID = 4;
    private static final String KEY = UUID.randomUUID().toString();
    private static final String FILE_PARAM_NAME = "file";
    private static final String FILENAME = "filename";
    private static final String IMAGE_FORMAT = "jpg";

    private MockMultipartFile imageFile;

    @BeforeEach
    void setUp() {
        teamService = new TeamService(
                imageProperty, userContext, userServiceClient, s3Service, teamRepository, resourceValidator);

        imageFile = getMultipartFile(MediaType.IMAGE_JPEG_VALUE, createImageAsByte(512, 512));
    }

    @Test
    @DisplayName("Успешное добавление аватарки команды - корректный размер в px")
    void positive_whenCorrectSize_shouldUploadAvatar() throws IOException {
        BufferedImage expectedImage = ImageIO.read(imageFile.getInputStream());
        preparePositiveUploadBehavior();

        teamService.uploadAvatar(TEAM_ID, imageFile);

        verify(s3Service, times(1)).uploadFile(multipartFileCaptor.capture(), keyCaptor.capture());
        verify(teamRepository, times(1)).save(teamCaptor.capture());

        BufferedImage actualImage = ImageIO.read(multipartFileCaptor.getValue().getInputStream());
        String key = keyCaptor.getValue();
        Team team = teamCaptor.getValue();
        assertNotNull(actualImage);
        assertEquals(expectedImage.getWidth(), actualImage.getWidth());
        assertEquals(expectedImage.getHeight(), actualImage.getHeight());
        assertNotNull(key);
        assertNotNull(team);
        assertEquals(key, team.getAvatarKey());
    }

    @Test
    @DisplayName("Успешное добавление аватарки команды - большой размер в px")
    void positive_whenLargeSize_shouldResizeUploadAvatar() throws IOException {
        MockMultipartFile imageFile = getMultipartFile(MediaType.IMAGE_JPEG_VALUE, createImageAsByte(512, 712));
        BufferedImage expectedImage = ImageIO.read(imageFile.getInputStream());
        preparePositiveUploadBehavior();

        teamService.uploadAvatar(TEAM_ID, imageFile);

        verify(s3Service, times(1)).uploadFile(multipartFileCaptor.capture(), keyCaptor.capture());
        verify(teamRepository, times(1)).save(teamCaptor.capture());

        BufferedImage actualImage = ImageIO.read(multipartFileCaptor.getValue().getInputStream());
        String key = keyCaptor.getValue();
        Team team = teamCaptor.getValue();
        assertNotNull(actualImage);
        assertNotEquals(expectedImage.getWidth(), actualImage.getWidth());
        assertNotEquals(expectedImage.getHeight(), actualImage.getHeight());
        assertNotNull(key);
        assertNotNull(team);
        assertEquals(key, team.getAvatarKey());
    }

    @Test
    @DisplayName("Успешное удаление аватарки команды по id")
    void positive_shouldDeleteAvatar() {
        when(userContext.getUserId()).thenReturn(MANAGER_ID);
        when(teamRepository.findById(TEAM_ID)).thenReturn(prepareTeam(KEY));
        when(userServiceClient.getUser(MANAGER_ID)).thenReturn(prepareUser(MANAGER_ID));

        teamService.deleteAvatar(TEAM_ID);

        verify(s3Service, times(1)).deleteFile(keyCaptor.capture());
        verify(teamRepository, times(1)).save(teamCaptor.capture());
        String key = keyCaptor.getValue();
        Team team = teamCaptor.getValue();
        assertNotNull(keyCaptor.getValue());
        assertNotNull(team);
        assertNull(team.getAvatarKey());
        assertEquals(KEY, key);
    }

    @Test
    @DisplayName("Ошибка добавления аватарки команды - не изображение")
    void negative_whenIsNotImage_uploadThrowsException() {
        MockMultipartFile textFile = getMultipartFile(MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());

        verify(s3Service, never()).uploadFile(any(MultipartFile.class), anyString());
        verify(teamRepository, never()).save(any(Team.class));
        assertThrows(NotImageException.class,
                     () -> teamService.uploadAvatar(TEAM_ID, textFile));
    }

    @Test
    @DisplayName("Ошибка добавления аватарки команды - команда не найдена")
    void negative_whenTeamNotFound_uploadThrowsException() {
        prepareNegativeBehavior(USER_ID, Optional.empty());

        verify(s3Service, never()).uploadFile(any(MultipartFile.class), anyString());
        verify(teamRepository, never()).save(any(Team.class));
        assertThrows(EntityNotFoundException.class,
                     () -> teamService.uploadAvatar(TEAM_ID, imageFile));
    }

    @Test
    @DisplayName("Ошибка добавления аватарки команды - юзер не в команде")
    void negative_whenUserNotTeamMember_uploadThrowsException() {
        prepareNegativeBehavior(OTHER_USER_ID, prepareTeam(null));

        verify(s3Service, never()).uploadFile(any(MultipartFile.class), anyString());
        verify(teamRepository, never()).save(any(Team.class));
        assertThrows(AccessDeniedException.class,
                     () -> teamService.uploadAvatar(TEAM_ID, imageFile));
    }

    @Test
    @DisplayName("Ошибка добавления аватарки команды - юзер не получен")
    void negative_whenUserNotFound_uploadThrowsException() {
        prepareNegativeBehavior(USER_ID, prepareTeam(null));

        verify(s3Service, never()).uploadFile(any(MultipartFile.class), anyString());
        verify(teamRepository, never()).save(any(Team.class));
        assertThrows(ResourceNotReceivedException.class,
                     () -> teamService.uploadAvatar(TEAM_ID, imageFile));
    }

    @Test
    @DisplayName("Ошибка удаления аватарки команды - команда не найдена")
    void negative_whenTeamNotFound_deleteThrowsException() {
        prepareNegativeBehavior(USER_ID, Optional.empty());

        verify(s3Service, never()).deleteFile(anyString());
        verify(teamRepository, never()).save(any(Team.class));
        assertThrows(EntityNotFoundException.class,
                     () -> teamService.deleteAvatar(TEAM_ID));
    }

    @Test
    @DisplayName("Ошибка удаления аватарки команды - юзер не в команде")
    void negative_whenUserNotTeamMember_deleteThrowsException() {
        String expectedMessage = "User id=" + OTHER_USER_ID + " is not a member of team id=" + TEAM_ID;
        prepareNegativeBehavior(OTHER_USER_ID, prepareTeam(KEY));

        verify(s3Service, never()).deleteFile(anyString());
        verify(teamRepository, never()).save(any(Team.class));
        String actualMessage = assertThrows(AccessDeniedException.class,
                                            () -> teamService.deleteAvatar(TEAM_ID)).getMessage();
        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    @DisplayName("Ошибка удаления аватарки команды - юзер не менеджер")
    void negative_whenUserNotManager_deleteThrowsException() {
        String expectedMessage = "User id=" + USER_ID + " is not a manager of team id=" + TEAM_ID;
        prepareNegativeBehavior(USER_ID, prepareTeam(KEY));

        verify(s3Service, never()).deleteFile(anyString());
        verify(teamRepository, never()).save(any(Team.class));
        String actualMessage = assertThrows(AccessDeniedException.class,
                                            () -> teamService.deleteAvatar(TEAM_ID)).getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Ошибка удаления аватарки команды - юзер не получен")
    void negative_whenUserNotFound_deleteThrowsException() {
        prepareNegativeBehavior(MANAGER_ID, prepareTeam(KEY));

        verify(s3Service, never()).deleteFile(anyString());
        verify(teamRepository, never()).save(any(Team.class));
        assertThrows(ResourceNotReceivedException.class,
                     () -> teamService.deleteAvatar(TEAM_ID));

    }

    // -----------------------

    private void preparePositiveUploadBehavior() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(teamRepository.findById(TEAM_ID)).thenReturn(prepareTeam(null));
        when(userServiceClient.getUser(USER_ID)).thenReturn(prepareUser(USER_ID));
        doNothing().when(s3Service).uploadFile(multipartFileCaptor.capture(), keyCaptor.capture());
        when(teamRepository.save(teamCaptor.capture()))
                .thenAnswer(invocation -> {
                    Team team = teamCaptor.getValue();
                    team.setAvatarKey(keyCaptor.getValue());
                    return team;
                });
    }

    private void prepareNegativeBehavior(Long userId, Optional<Team> team) {
        when(userContext.getUserId()).thenReturn(userId);
        when(teamRepository.findById(TEAM_ID)).thenReturn(team);
    }

    private MockMultipartFile getMultipartFile(String contentType, byte[] bytes) {
        return new MockMultipartFile(FILE_PARAM_NAME,
                                     FILENAME,
                                     contentType,
                                     bytes);
    }

    private Optional<Team> prepareTeam(String key) {
        return Optional.of(
                Team.builder()
                        .id(TEAM_ID)
                        .avatarKey(key)
                        .teamMembers(prepareTeamMembers())
                        .build());
    }

    private List<TeamMember> prepareTeamMembers() {
        return List.of(prepareTeamMember(MANAGER_ID, List.of(TeamRole.MANAGER)),
                       prepareTeamMember(USER_ID, List.of(TeamRole.DEVELOPER)));
    }

    private TeamMember prepareTeamMember(Long userId, List<TeamRole> roles) {
        return TeamMember.builder()
                .userId(userId)
                .roles(roles)
                .build();
    }

    private UserDto prepareUser(long userId) {
        return new UserDto(userId, null, null);
    }

    private byte[] createImageAsByte(int widthPx, int heightPx) {
        try {
            BufferedImage bufferedImage = new BufferedImage(widthPx, heightPx, BufferedImage.TYPE_INT_RGB);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, IMAGE_FORMAT, outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}