package faang.school.projectservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.projectservice.config.CoverImageConfig;
import faang.school.projectservice.dto.ImageConfig;
import faang.school.projectservice.util.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AmazonS3ServiceTest {
    private static final String IMAGE_KEY = "image-mock-key-value";
    private static final String BUCKET_NAME = "mock-bucket-name";
    private static final String PREFIX = "image-key_prefix";
    private static final int FILE_SIZE_MB = 1;
    private static final int SQUARE_SIZE = 100;
    private static final int RECT_WIDTH_SIZE = 100;
    private static final int RECT_HEIGHT_SIZE = 60;
    public static final String IMAGE_NAME = "image.jpg";
    public static final String ORIGINAL_IMAGE_NAME = "original-image.jpg";
    public static final String DEFAULT_CONTENT_TYPE = "image/jpeg";

    @Mock
    private AmazonS3 amazonS3;
    @Spy
    private Utils utils;
    @InjectMocks
    private AmazonS3Service amazonS3Service;

    @BeforeEach
    public void setUp() {
        amazonS3Service.setBucketName(BUCKET_NAME);
    }

    @Test
    public void testCheckExistsBucket() {
        when(amazonS3.doesBucketExistV2(BUCKET_NAME)).thenReturn(true);

        amazonS3Service.checkBucket();

        verify(amazonS3, times(0)).createBucket(BUCKET_NAME);
    }

    @Test
    public void testCheckMissingBucket() {
        when(amazonS3.doesBucketExistV2(BUCKET_NAME)).thenReturn(false);

        when(amazonS3.createBucket(BUCKET_NAME)).thenReturn(any());

        amazonS3Service.checkBucket();

        verify(amazonS3).createBucket(BUCKET_NAME);
    }

    @Test
    public void testDeleteImage() {
        doNothing().when(amazonS3).deleteObject(BUCKET_NAME, IMAGE_KEY);

        amazonS3Service.deleteImage(IMAGE_KEY);

        verify(amazonS3).deleteObject(BUCKET_NAME, IMAGE_KEY);
    }

    @Test
    public void testUploadSquareCoverImage() throws IOException {
        ImageConfig imageConfig = getImageConfig();
        // Создаем тестовое изображение
        BufferedImage mockImage = new BufferedImage(SQUARE_SIZE, SQUARE_SIZE, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(mockImage, "jpg", outputStream);
        MultipartFile mockMultipartFile = new MockMultipartFile(
                IMAGE_NAME, ORIGINAL_IMAGE_NAME, DEFAULT_CONTENT_TYPE, outputStream.toByteArray());

        amazonS3Service.uploadImage(mockMultipartFile, PREFIX, imageConfig);

        verify(amazonS3).putObject(eq(BUCKET_NAME), anyString(), any(InputStream.class), any(ObjectMetadata.class));
    }

    @Test
    public void testUploadSquareCoverImage_Resize() throws IOException {
        ImageConfig imageConfig = getImageConfig();
        // Создаем тестовое изображение
        BufferedImage mockImage = new BufferedImage(
                SQUARE_SIZE * 2, SQUARE_SIZE * 2, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(mockImage, "jpg", outputStream);
        MultipartFile mockMultipartFile = new MockMultipartFile(
                IMAGE_NAME, ORIGINAL_IMAGE_NAME, DEFAULT_CONTENT_TYPE, outputStream.toByteArray());

        amazonS3Service.uploadImage(mockMultipartFile, PREFIX, imageConfig);

        verify(amazonS3).putObject(eq(BUCKET_NAME), anyString(), any(InputStream.class), any(ObjectMetadata.class));
    }

    @Test
    public void testUploadRectangleCoverImage() throws IOException {
        ImageConfig imageConfig = getImageConfig();
        // Создаем тестовое изображение
        BufferedImage mockImage = new BufferedImage(RECT_WIDTH_SIZE, RECT_HEIGHT_SIZE, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(mockImage, "jpg", outputStream);
        MultipartFile mockMultipartFile = new MockMultipartFile(
                IMAGE_NAME, ORIGINAL_IMAGE_NAME, DEFAULT_CONTENT_TYPE, outputStream.toByteArray());

        amazonS3Service.uploadImage(mockMultipartFile, PREFIX, imageConfig);

        verify(amazonS3).putObject(eq(BUCKET_NAME), anyString(), any(InputStream.class), any(ObjectMetadata.class));
    }

    @Test
    public void testUploadRectangleCoverImage_Resize() throws IOException {
        ImageConfig imageConfig = getImageConfig();
        // Создаем тестовое изображение
        BufferedImage mockImage = new BufferedImage(
                RECT_WIDTH_SIZE * 2, RECT_HEIGHT_SIZE * 3, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(mockImage, "jpg", outputStream);
        MultipartFile mockMultipartFile = new MockMultipartFile(
                IMAGE_NAME, ORIGINAL_IMAGE_NAME, DEFAULT_CONTENT_TYPE, outputStream.toByteArray());

        amazonS3Service.uploadImage(mockMultipartFile, PREFIX, imageConfig);

        verify(amazonS3).putObject(eq(BUCKET_NAME), anyString(), any(InputStream.class), any(ObjectMetadata.class));
    }

    private ImageConfig getImageConfig() {
        ImageConfig imageConfig = new CoverImageConfig();
        imageConfig.setFileSizeMb(FILE_SIZE_MB);
        imageConfig.setSquareSize(SQUARE_SIZE);
        imageConfig.setRectWidthSize(RECT_WIDTH_SIZE);
        imageConfig.setRectHeightSize(RECT_HEIGHT_SIZE);
        return imageConfig;
    }
}