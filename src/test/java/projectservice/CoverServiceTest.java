package projectservice;

import faang.school.projectservice.exception.EmptyFileException;
import faang.school.projectservice.exception.FileProcessingException;
import faang.school.projectservice.exception.VacancyNotFoundException;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.cover.CoverServiceImpl;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static faang.school.projectservice.service.cover.CoverServiceImpl.FILENAME_CANT_BE_NULL;
import static faang.school.projectservice.service.cover.CoverServiceImpl.FILE_CANT_BE_EMPTY;
import static faang.school.projectservice.service.cover.CoverServiceImpl.IMAGE_PROCESSING_FAILED;
import static faang.school.projectservice.service.cover.CoverServiceImpl.UNSUPPORTED_IMAGE_FORMAT;
import static faang.school.projectservice.service.cover.CoverServiceImpl.VACANCY_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CoverServiceTest {
    @InjectMocks
    private CoverServiceImpl coverService;

    @Mock
    private MinioClient minioClient;

    @Mock
    private VacancyRepository vacancyRepository;

    private static final int MAX_IMAGE_DIMENSION = 512;

    private final Long vacancyId = 1L;
    private Vacancy vacancy;
    private MultipartFile validImageFile;
    private MultipartFile emptyFile;

    @BeforeEach
    public void setUp() throws IOException {
        ReflectionTestUtils.setField(coverService, "bucketName", "cover-bucket");
        ReflectionTestUtils.setField(coverService, "maxImageDimension", MAX_IMAGE_DIMENSION);

        vacancy = Vacancy.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .position(TeamRole.MANAGER)
                .status(VacancyStatus.OPEN)
                .build();
        BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        validImageFile = new MockMultipartFile("name.jpg",
                "name.jpg", "image/jpeg", imageBytes);
        emptyFile = new MockMultipartFile("name.jpg",
                "name.jpg", "image/jpeg", new byte[0]);
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
        EmptyFileException exception = assertThrows(EmptyFileException.class,
                () -> coverService.uploadCover(emptyFile, vacancyId)
        );

        assertEquals(FILE_CANT_BE_EMPTY, exception.getMessage());
    }

    @Test
    public void testUploadCover_nullFilename() {
        validImageFile = new MockMultipartFile("name",
                null, "image/jpeg", "imageBytes".getBytes());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> coverService.uploadCover(validImageFile, vacancyId)
        );

        assertEquals(FILENAME_CANT_BE_NULL, exception.getMessage());
    }

    @Test
    public void testUploadCover_imageProcessingFailed() throws Exception {
        when(vacancyRepository.findById(anyLong())).thenReturn(Optional.of(vacancy));
        doThrow(new IOException("Test error")).when(minioClient).putObject(any());

        FileProcessingException exception = assertThrows(FileProcessingException.class,
                () -> coverService.uploadCover(validImageFile, vacancyId)
        );

        assertEquals(IMAGE_PROCESSING_FAILED, exception.getMessage());
    }

    @Test
    public void testUploadCover_invalidImageFormat() {
        validImageFile = new MockMultipartFile("name",
                "originalName", "image/gif", "imageBytes".getBytes());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> coverService.uploadCover(validImageFile, vacancyId)
        );

        assertEquals(UNSUPPORTED_IMAGE_FORMAT, exception.getMessage());
    }

    @Test
    public void testUploadCover_imageSmallerThanMaxDimension() throws IOException {
        BufferedImage image = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        MultipartFile smallImage = new MockMultipartFile("name.jpg",
                "name.jpg", "image/jpeg", imageBytes);

        when(vacancyRepository.findById(anyLong())).thenReturn(Optional.of(vacancy));
        coverService.uploadCover(smallImage, vacancyId);

        assertEquals(200, image.getWidth());
        assertEquals(100, image.getHeight());
    }

    @Test
    public void testUploadCover_imageGreaterThanMaxDimension() throws Exception {
        BufferedImage image = new BufferedImage(2000, 1000, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        MultipartFile bigImage = new MockMultipartFile("name.jpg",
                "name.jpg", "image/jpeg", imageBytes);

        when(vacancyRepository.findById(anyLong())).thenReturn(Optional.of(vacancy));

        coverService.uploadCover(bigImage, vacancyId);

        verify(minioClient).putObject(any(PutObjectArgs.class));

        ArgumentCaptor<PutObjectArgs> putObjectArgsCaptor = ArgumentCaptor.forClass(PutObjectArgs.class);
        verify(minioClient).putObject(putObjectArgsCaptor.capture());
        PutObjectArgs args = putObjectArgsCaptor.getValue();
        assertEquals("image/jpeg", args.contentType());

        InputStream uploadedStream = args.stream();
        byte[] uploadedBytes = uploadedStream.readAllBytes();
        BufferedImage uploadedImage = ImageIO.read(new ByteArrayInputStream(uploadedBytes));
        assertTrue(Math.max(uploadedImage.getWidth(), uploadedImage.getHeight()) <= MAX_IMAGE_DIMENSION);
    }
}
