package faang.school.project_service.validation;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.validation.resource.ResourceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ResourceValidatorTest {

    private final int SQUARE_IMAGE_LENGTH = 1080;
    private final int HORIZONTAL_IMAGE_WIDTH = 1080;
    private final int HORIZONTAL_IMAGE_HEIGHT = 566;
    private final int largeFileSize = 6;
    private final MockMultipartFile largeFile = new MockMultipartFile("file", "test.jpg",
            "image/jpeg", new byte[(int) DataSize.ofMegabytes(largeFileSize).toBytes()]);

    @InjectMocks
    @Spy
    private ResourceValidator resourceValidator;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(resourceValidator, "projectAvatarSquareImageLength", SQUARE_IMAGE_LENGTH);
        ReflectionTestUtils
                .setField(resourceValidator, "projectAvatarHorizontalImageWidth", HORIZONTAL_IMAGE_WIDTH);
        ReflectionTestUtils
                .setField(resourceValidator, "projectAvatarHorizontalImageHeight", HORIZONTAL_IMAGE_HEIGHT);
    }

    @Test
    void testValidateProjectStorageSizeThrowsExceptionIfNewStorageSizeMoreThanMax() {
        Project project = Project.builder()
                .storageSize(new BigInteger("1000"))
                .maxStorageSize(new BigInteger("10000"))
                .build();

        String expectedErrorMessage = "Not enough storage size. Max storage size: %d, storage size: %d, file size: %d"
                .formatted(project.getMaxStorageSize(), project.getStorageSize(), largeFile.getSize());

        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> resourceValidator.validateProjectStorageSize(project, largeFile));
        assertEquals(expectedErrorMessage, dataValidationException.getMessage());
    }

    @Test
    void testValidateProjectStorageSize() {
        Project project = Project.builder()
                .storageSize(new BigInteger("1000"))
                .maxStorageSize(new BigInteger("10000000"))
                .build();

        assertDoesNotThrow(() -> resourceValidator.validateProjectStorageSize(project, largeFile));
    }

    @Test
    void testValidateFileSizeThrowsExceptionIfFileSizeMoreThanAllowed() {
        long permittedSize = largeFileSize - 1;
        String errorMessage = "File size exceeded. Actual: %d, permitted: %d"
                .formatted(largeFile.getSize(), DataSize.ofMegabytes(permittedSize).toBytes());

        FileException fileException = assertThrows(FileException.class,
                () -> resourceValidator.validateFileSize(largeFile, permittedSize));
        assertEquals(errorMessage, fileException.getMessage());
    }

    @Test
    void testValidateFileSize() {
        long permittedSize = largeFileSize + 1;

        assertDoesNotThrow(() -> resourceValidator.validateFileSize(largeFile, permittedSize));
    }

    @Test
    void testValidateImageDimensionsThrowsExceptionIfImageNotFound() {
        FileException fileException = assertThrows(FileException.class,
                () -> resourceValidator.validateImageDimensions(largeFile));
        assertEquals("Invalid image file", fileException.getMessage());
    }

    @Test
    void testValidateImageDimensionsWithSquarePositive() throws IOException {
        BufferedImage image = new BufferedImage(SQUARE_IMAGE_LENGTH, SQUARE_IMAGE_LENGTH, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);

        MockMultipartFile validFile = new MockMultipartFile(
                "validFile", "test.jpg", "image/jpeg", baos.toByteArray());

        MultipartFile validatedFile = assertDoesNotThrow(() -> resourceValidator.validateImageDimensions(validFile));
        assertEqualsValidAndValidatedFiles(validFile, validatedFile);
        assertEquals(validFile.getSize(), validatedFile.getSize());
    }

    @Test
    void testValidateImageDimensionsWithSquareAndResize() throws IOException {
        int newSquareImageLength = SQUARE_IMAGE_LENGTH + 5;
        BufferedImage image = new BufferedImage(newSquareImageLength, newSquareImageLength, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);

        MockMultipartFile validFile = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", baos.toByteArray());

        MultipartFile resizedFile = assertDoesNotThrow(() -> resourceValidator.validateImageDimensions(validFile));

        BufferedImage resizedImage;
        try (InputStream inputStream = resizedFile.getInputStream()) {
            resizedImage = ImageIO.read(inputStream);
        }

        assertEquals(SQUARE_IMAGE_LENGTH, resizedImage.getHeight());
        assertEquals(SQUARE_IMAGE_LENGTH, resizedImage.getWidth());

        assertEqualsValidAndValidatedFiles(validFile, resizedFile);
    }

    @Test
    void testValidateImageDimensionsWithHorizontal() throws IOException {
        BufferedImage image
                = new BufferedImage(HORIZONTAL_IMAGE_WIDTH, HORIZONTAL_IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);

        MockMultipartFile validFile = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", baos.toByteArray());

        MultipartFile validatedFile = assertDoesNotThrow(() -> resourceValidator.validateImageDimensions(validFile));
        assertEqualsValidAndValidatedFiles(validFile, validatedFile);
        assertEquals(validFile.getSize(), validatedFile.getSize());
    }

    @Test
    void testValidateImageDimensionsWithHorizontalAndWithResize() throws IOException {
        BufferedImage image = new BufferedImage(HORIZONTAL_IMAGE_WIDTH + 100,
                HORIZONTAL_IMAGE_HEIGHT + 50, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);

        MockMultipartFile validFile = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", baos.toByteArray());

        MultipartFile validatedFile = assertDoesNotThrow(() -> resourceValidator.validateImageDimensions(validFile));
        BufferedImage resizedImage;
        try (InputStream inputStream = validatedFile.getInputStream()) {
            resizedImage = ImageIO.read(inputStream);
        }

        assertEqualsValidAndValidatedFiles(validFile, validatedFile);
        assertEquals(HORIZONTAL_IMAGE_HEIGHT, resizedImage.getHeight());
        assertEquals(HORIZONTAL_IMAGE_WIDTH, resizedImage.getWidth());
    }

    private void assertEqualsValidAndValidatedFiles(MultipartFile validFile, MultipartFile validatedFile) {
        assertEquals(validFile.getName(), validatedFile.getName());
        assertEquals(validFile.getContentType(), validatedFile.getContentType());
        assertEquals(validFile.getOriginalFilename(), validatedFile.getOriginalFilename());
    }
}