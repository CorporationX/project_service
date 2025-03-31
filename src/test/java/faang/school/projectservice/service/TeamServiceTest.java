package faang.school.projectservice.service;

import faang.school.projectservice.filter.CustomMultipartFile;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.awt.image.BufferedImage;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
public class TeamServiceTest {
    private static final long LIMITATION_FILE_SIZE = 5 * 1024 * 1024; // 5 МБ
    @Mock
    TeamRepository teamRepository;
    @Mock
    S3Service s3Service;
    @Mock
    ResourceRepository resourceRepository;
    @Mock
    TeamMemberRepository teamMemberRepository;
    @Mock
    ResizeImagesService resizeImagesService;
    @InjectMocks
    TeamService teamService;


    @Test
    public void testPositiveGetImageFromMultiPartFile() {
        Long id = 1L;
        Team team = Team.builder().build();
        BufferedImage bufferedImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
        BufferedImage bufferedImage1 = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
        byte[] contentData = new byte[]{1, 2, 3};
        CustomMultipartFile customMultipartFile = CustomMultipartFile.builder()
                .content(contentData)
                .build();
        Resource resource = Resource.builder()
                .key("test")
                .build();

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(resizeImagesService.getImageFromMultiPartFile(any())).thenReturn(bufferedImage);
        when(resizeImagesService.resizeImage(any(BufferedImage.class), eq(1000),
                eq(1000))).thenReturn(bufferedImage1);
        when(resizeImagesService.convertImageToMultipartFile(any(), any())).thenReturn(customMultipartFile);
        when(s3Service.uploadFile(any(), any())).thenReturn(resource);

        teamService.upload(customMultipartFile, id);

        verify(resourceRepository, times(1)).save(resource);
        verify(teamRepository, times(1)).save(team);

        assertEquals(resource.getKey(), team.getAvatarKey());
    }

    @Test
    public void testNegativeGetImageFromMultipartFileLimitSize() {
        MockMultipartFile file = new MockMultipartFile("file",
                "test.jpg", "image/jpeg",
                new byte[(int) LIMITATION_FILE_SIZE + 1]);
        assertThrows(IllegalArgumentException.class, () -> teamService.upload(file, 1L));

    }
}
