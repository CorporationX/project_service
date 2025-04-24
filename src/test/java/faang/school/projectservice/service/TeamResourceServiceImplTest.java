package faang.school.projectservice.service;

import faang.school.projectservice.config.TeamResourceConfig;
import faang.school.projectservice.dto.client.TeamResourceDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.ResourceProcessingException;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeamResourceServiceImplTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private TeamResourceConfig teamResourceConfig;

    @InjectMocks
    private TeamResourceServiceImpl teamResourceService;

    private Long teamId = 1L;
    private Long userId = 2L;
    private Team team;

    @BeforeEach
    void setUp() {
        team = new Team();
        team.setId(teamId);
        team.setAvatarKey("avatar_key_1");
    }

    @Test
    void shouldUploadAvatarSuccessfully() throws Exception {
        when(teamResourceConfig.getSupportedContentTypes()).thenReturn(Set.of("image/png", "image/jpeg"));
        when(teamResourceConfig.getMaxSize()).thenReturn(5 * 1024 * 1024L);
        when(teamResourceConfig.getWidth()).thenReturn(512);
        when(teamResourceConfig.getHeight()).thenReturn(512);

        String filename = "avatar.png";
        String contentType = "image/png";
        long size = 1024L;

        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        File tempFile = File.createTempFile("avatar", ".png");
        ImageIO.write(image, "png", tempFile);
        tempFile.deleteOnExit();

        MultipartFile file = new MockMultipartFile(
                filename, filename, contentType, new FileInputStream(tempFile)
        );

        team.setAvatarKey("previousAvatarKey");

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        doNothing().when(s3Service).uploadImage(any(), any(), any());

        TeamResourceDto result = teamResourceService.uploadAvatar(teamId, file);

        assertNotNull(result);
        assertEquals(contentType, result.getFileType());
        assertEquals(file.getSize(), result.getFileSize());
        assertNotNull(result.getAvatarKey());
        assertNotNull(result.getUploadedAt());
    }

    @Test
    void shouldNotUploadAvatarWhenFileIsNull() {
        ResourceProcessingException exception = assertThrows(ResourceProcessingException.class,
                () -> teamResourceService.uploadAvatar(teamId, null));

        assertEquals("Uploaded file is null or empty", exception.getMessage());
    }

    @Test
    void shouldNotUploadAvatarWhenFileIsEmpty() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        ResourceProcessingException exception = assertThrows(ResourceProcessingException.class, () ->
                teamResourceService.uploadAvatar(teamId, file));

        assertEquals("Uploaded file is null or empty", exception.getMessage());
    }

    @Test
    void shouldNotUploadAvatarWhenUnsupportedFileType() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("image/gif");

        ResourceProcessingException exception = assertThrows(ResourceProcessingException.class, () ->
                teamResourceService.uploadAvatar(teamId, file));

        assertEquals("Only PNG and JPEG images are supported.", exception.getMessage());
    }

    @Test
    void shouldNotUploadAvatarWhenFileSizeExceedsLimit() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getSize()).thenReturn(6 * 1024 * 1024L);
        when(teamResourceConfig.getSupportedContentTypes()).thenReturn(Set.of("image/png", "image/jpeg"));
        when(teamResourceConfig.getMaxSize()).thenReturn(5 * 1024 * 1024L);

        ResourceProcessingException exception = assertThrows(ResourceProcessingException.class, () ->
                teamResourceService.uploadAvatar(teamId, file));

        assertEquals("File size exceeds the 5 MB limit for team avatar.", exception.getMessage());
    }

    @Test
    void shouldThrowNotFoundWhenTeamNotFound() throws Exception {
        MultipartFile file = new MockMultipartFile("avatar.png",
                "avatar.png", "image/png", new byte[10]);

        when(teamResourceConfig.getSupportedContentTypes()).thenReturn(Set.of("image/png", "image/jpeg"));
        when(teamResourceConfig.getMaxSize()).thenReturn(5 * 1024 * 1024L);

        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                teamResourceService.uploadAvatar(teamId, file));

        assertEquals("Team with id 1 not found", exception.getMessage());
    }

    @Test
    void shouldNotUploadAvatarWhenUploadToMinioFails() throws Exception {
        when(teamResourceConfig.getSupportedContentTypes()).thenReturn(Set.of("image/png", "image/jpeg"));
        when(teamResourceConfig.getMaxSize()).thenReturn(5 * 1024 * 1024L);
        when(teamResourceConfig.getWidth()).thenReturn(512);
        when(teamResourceConfig.getHeight()).thenReturn(512);

        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        File tempFile = File.createTempFile("avatar", ".jpg");
        ImageIO.write(image, "jpg", tempFile);
        tempFile.deleteOnExit();

        MultipartFile file = new MockMultipartFile("avatar.jpg", "avatar.jpg",
                "image/jpeg", new FileInputStream(tempFile));

        team.setAvatarKey("prev_key");

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        doThrow(new ResourceProcessingException("Failed to upload avatar for team with id: " + teamId))
                .when(s3Service).uploadImage(any(), any(), any());

        ResourceProcessingException exception = assertThrows(ResourceProcessingException.class, () ->
                teamResourceService.uploadAvatar(teamId, file));

        assertEquals("Failed to upload avatar for team with id: " + teamId, exception.getMessage());
    }

    @Test
    void shouldDeleteAvatarSuccessfully() {
        team.setAvatarKey("avatar_key_1");

        TeamMember teamMember = new TeamMember();
        teamMember.setRoles(List.of(TeamRole.MANAGER));

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(teamMemberRepository.findByTeamIdAndUserId(teamId, userId)).thenReturn(Optional.of(teamMember));

        teamResourceService.deleteAvatar(teamId, userId);

        assertNull(team.getAvatarKey());
        verify(s3Service).deleteImage(eq("avatar_key_1"));
    }

    @Test
    void shouldNotDeleteAvatarWhenTeamNotFound() {
        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                teamResourceService.deleteAvatar(teamId, userId));

        assertEquals("Team with id 1 not found", exception.getMessage());
    }

    @Test
    void shouldNotDeleteAvatarWhenTeamMemberNotFound() {
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(teamMemberRepository.findByTeamIdAndUserId(teamId, userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                teamResourceService.deleteAvatar(teamId, userId));

        assertEquals("User is not a member of the team", exception.getMessage());
    }

    @Test
    void shouldNotDeleteAvatarWhenUserIsNotAuthorized() {
        TeamMember teamMember = new TeamMember();
        teamMember.setRoles(List.of());

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(teamMemberRepository.findByTeamIdAndUserId(teamId, userId)).thenReturn(Optional.of(teamMember));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                teamResourceService.deleteAvatar(teamId, userId));

        assertEquals("You are not authorized to delete the avatar", exception.getMessage());
    }

    @Test
    void shouldNotDeleteAvatarWhenAvatarKeyIsNull() {
        team.setAvatarKey(null);

        TeamMember teamMember = new TeamMember();
        teamMember.setRoles(List.of(TeamRole.MANAGER));

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(teamMemberRepository.findByTeamIdAndUserId(teamId, userId)).thenReturn(Optional.of(teamMember));

        teamResourceService.deleteAvatar(teamId, userId);

        verify(s3Service, never()).deleteImage(any());
    }
}


