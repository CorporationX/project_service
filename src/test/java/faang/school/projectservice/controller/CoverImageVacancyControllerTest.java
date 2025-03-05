package faang.school.projectservice.controller;

import faang.school.projectservice.dto.CoverImageVacancyReadDto.ResourceDto;
import faang.school.projectservice.service.CoverImageService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;

import static faang.school.projectservice.model.ResourceStatus.ACTIVE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CoverImageVacancyControllerTest {

    @Mock
    private CoverImageService coverImageService;

    @InjectMocks
    private CoverImageVacancyController coverImageVacancyController;

    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        byte[] fileContent = new byte[1024 * 1024 * 4];
        mockFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", fileContent);
        ReflectionTestUtils.setField(coverImageVacancyController, "maxSizeFile", 1024 * 1024 * 5);
    }

    @Test
    void testUploadCover_Success() {
        long currentUserId = 1L;
        long vacancyId = 2L;
        ResourceDto mockResourceDto = new ResourceDto(1L, ACTIVE, LocalDateTime.now());

        when(coverImageService.uploadCover(currentUserId, vacancyId, mockFile)).thenReturn(mockResourceDto);

        ResourceDto result = coverImageVacancyController.uploadCover(currentUserId, vacancyId, mockFile);

        verify(coverImageService, times(1)).uploadCover(currentUserId, vacancyId, mockFile);
        assertNotNull(result);
        assertEquals(mockResourceDto.getId(), result.getId());
        assertEquals(mockResourceDto.getStatus(), result.getStatus());
    }

    @Test
    public void testDeleteCover() {
        long currentUserId = 1L;
        long resourceId = 2L;

        doNothing().when(coverImageService).deleteCover(currentUserId, resourceId);
        coverImageVacancyController.deleteCover(currentUserId, resourceId);
        verify(coverImageService, times(1)).deleteCover(currentUserId, resourceId);
    }

    @Test
    public void testGetCoverImage() {
        long resourceId = 1L;
        InputStream expectedInputStream = new ByteArrayInputStream("test image content".getBytes());

        when(coverImageService.getCoverImage(resourceId)).thenReturn(expectedInputStream);

        InputStream result = coverImageVacancyController.getCoverImage(resourceId);

        assertNotNull(result);
        assertEquals(expectedInputStream, result);

        verify(coverImageService, times(1)).getCoverImage(resourceId);
    }

    @Test
    public void testValidationFileEmpty() {
        MultipartFile emptyFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[0]);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            coverImageVacancyController.uploadCover(1L, 1L, emptyFile);
        });

        assertEquals("файл пуст", exception.getMessage());
    }

    @Test
    public void testValidationFileSizeExceeded() {
        long currentUserId = 1L;
        long vacancyId = 2L;

        byte[] largeFileContent = new byte[1024 * 1024 * 5 + 1];
        MultipartFile largeFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", largeFileContent);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            coverImageVacancyController.uploadCover(currentUserId, vacancyId, largeFile);
        });

        assertEquals("Размер файла не может превышать 5 Мб", exception.getMessage());
    }
}
