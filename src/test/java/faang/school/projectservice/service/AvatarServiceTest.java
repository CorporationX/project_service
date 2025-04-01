package faang.school.projectservice.service;

import static faang.school.projectservice.contants.ErrorMessage.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import faang.school.projectservice.exception.UnsupportedFileTypeException;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.s3.S3ServiceImpl;
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AvatarServiceTest {
    private final static long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @InjectMocks
    private AvatarService avatarService;

    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private S3ServiceImpl s3Service;

    private Team team;
    private long teamId;
    private Resource resource;
    private MultipartFile validFile;
    private MultipartFile largeFile;
    private MultipartFile invalidFile;
    private String avatarKey;
    private TeamMember teamMember;

    @BeforeEach
    void setUp() {
        team = new Team();
        resource = new Resource();
        teamId = 1L;
        avatarKey = "avatar-key";
        teamMember = new TeamMember();
        teamMember.setUserId(1L);
        teamMember.setTeam(team);
        teamMember.setRoles(List.of(TeamRole.MANAGER));

        try {
            BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            validFile = new MockMultipartFile("file", "avatar.png", "image/png", new ByteArrayInputStream(baos.toByteArray()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        invalidFile = new MockMultipartFile("file", "text.txt", "text/plain", "Not an image".getBytes());

        byte[] largeImageBytes = new byte[6 * 1024 * 1024];
        largeFile = new MockMultipartFile("file", "big.png", "image/png", largeImageBytes);

        team.setId(teamId);
        resource.setKey(avatarKey);
    }
    //Positive
    @Test
    void addAvatar_Success() {
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(s3Service.uploadFile(any(), anyString())).thenReturn(resource);

        avatarService.addAvatar(teamId, validFile);

        verify(resourceRepository, times(1)).save(resource);
        verify(teamRepository, times(1)).save(team);
    }

    @Test
    void deleteAvatar_Success() {
        team.setAvatarKey(avatarKey);
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(teamMemberRepository.findByUserId(1L)).thenReturn(List.of(teamMember));

        avatarService.deleteAvatar(teamId, 1L);

        verify(s3Service, times(1)).deleteFile(avatarKey);
        verify(teamRepository, times(1)).save(team);
    }

    //Negative

    @Test
    void addAvatar_InvalidFileType_ThrowsException() {
        Exception exception = assertThrows(UnsupportedFileTypeException.class, () -> avatarService.addAvatar(teamId, invalidFile));

        assertEquals(ERROR_INVALID_FILE_TYPE, exception.getMessage());
    }

    @Test
    void addAvatar_FileTooLarge_ThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> avatarService.addAvatar(teamId, largeFile));

        assertEquals(getErrorLimitSizeFile(MAX_FILE_SIZE), exception.getMessage());
    }
    @Test
    void deleteAvatar_NoAvatarKey(){
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(teamMemberRepository.findByUserId(1L)).thenReturn(List.of(teamMember));

        avatarService.deleteAvatar(teamId, 1L);

        verify(s3Service, never()).deleteFile(any());
    }
    @Test
    void deleteAvatar_NoManagerTeam(){
        teamMember.setRoles(List.of(TeamRole.DEVELOPER));
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(teamMemberRepository.findByUserId(1L)).thenReturn(List.of(teamMember));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> avatarService.deleteAvatar(teamId, 1L));
        assertEquals(ERROR_UNAUTHORIZED_ACCESS, exception.getMessage());
    }
}