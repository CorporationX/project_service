package projectservice;

import faang.school.projectservice.exception.EmptyFileException;
import faang.school.projectservice.exception.VacancyNotFoundException;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.cover.CoverServiceImpl;
import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

import static faang.school.projectservice.service.cover.CoverServiceImpl.FILENAME_CANT_BE_NULL;
import static faang.school.projectservice.service.cover.CoverServiceImpl.FILE_CANT_BE_EMPTY;
import static faang.school.projectservice.service.cover.CoverServiceImpl.VACANCY_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CoverServiceTest {
    @InjectMocks
    private CoverServiceImpl coverService;

    @Mock
    private MinioClient minioClient;

    @Mock
    VacancyRepository vacancyRepository;

    private final Long vacancyId = 1L;
    private Vacancy vacancy;
    private MultipartFile validImageFile;
    private MultipartFile emptyFile;
    private MultipartFile inValidImageFile;

    @BeforeEach
    public void setUp() throws IOException {
        ReflectionTestUtils.setField(coverService, "bucketName", "cover-bucket");
        BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        validImageFile = new MockMultipartFile("name.jpg",
                "name.jpg", "image/jpeg", imageBytes);
        emptyFile = new MockMultipartFile("name.jpg",
                "name.jpg", "image/jpeg", new byte[0]);
        inValidImageFile = new MockMultipartFile("invalid.txt",
                "invalid.txt", "text/plain", "not an image".getBytes());
    }

    @Test
    public void testUploadCover_vacancyNotFound() {
        when(vacancyRepository.findById(anyLong())).thenReturn(Optional.empty());

        VacancyNotFoundException exception = assertThrows(VacancyNotFoundException.class,
                () -> coverService.uploadCover(validImageFile, vacancyId)
        );

        assertEquals(String.format(VACANCY_NOT_FOUND, vacancyId), exception.getMessage());
    }

    @Test
    public void testUploadCover_emptyFile() {
        when(vacancyRepository.findById(anyLong())).thenReturn(Optional.of(new Vacancy()));

        EmptyFileException exception = assertThrows(EmptyFileException.class,
                () -> coverService.uploadCover(emptyFile, vacancyId)
        );

        assertEquals(FILE_CANT_BE_EMPTY, exception.getMessage());
    }

    @Test
    public void testUploadCover_nullFilename() {
        when(vacancyRepository.findById(anyLong())).thenReturn(Optional.of(new Vacancy()));
        validImageFile = new MockMultipartFile("name",
                null, "image/jpeg", "imageBytes".getBytes());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> coverService.uploadCover(validImageFile, vacancyId)
        );

        assertEquals(FILENAME_CANT_BE_NULL, exception.getMessage());
    }

    @Test
    @SneakyThrows
    public void testUploadCover_bucketDoesNotExist() throws ErrorResponseException {
        when(vacancyRepository.findById(anyLong())).thenReturn(Optional.of(new Vacancy()));
        when(minioClient.bucketExists(any())).thenReturn(false);

        coverService.uploadCover(validImageFile, vacancyId);

        verify(minioClient, times(1)).makeBucket(any());
    }
}
