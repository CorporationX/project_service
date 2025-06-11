package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.excepcion.ImageProcessingException;
import faang.school.projectservice.excepcion.S3OperationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.internal.ProcessedImage;
import faang.school.projectservice.repository.adapter.vacancy.VacancyRepositoryAdapter;
import faang.school.projectservice.service.adapter.TeamMemberServiceAdapter;
import faang.school.projectservice.service.s3.S3ServiceInterface;
import faang.school.projectservice.utility.TwelveMonkeysImageUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacancyCoverServiceImplTest {
    @Mock
    private UserContext userContext;
    @Mock
    private S3ServiceInterface s3ServiceInterface;
    @Mock
    private TwelveMonkeysImageUtility imageUtility;
    @Mock
    private VacancyRepositoryAdapter vacancyRepositoryAdapter;
    @Mock
    private TeamMemberServiceAdapter teamMemberServiceAdapter;

    @InjectMocks
    private VacancyCoverServiceImpl vacancyCoverService;

    private final String BUCKET_NAME = "test-bucket";
    private final Long VACANCY_ID = 1L;
    private final Long USER_ID = 100L;
    private final Long PROJECT_ID = 200L;

    @BeforeEach
    void setUp() throws IllegalAccessException, NoSuchFieldException {
        ReflectionTestUtils.setField(vacancyCoverService, "coversBucket", BUCKET_NAME);
        Field field = VacancyCoverServiceImpl.class.getDeclaredField("maxImageSizeBytes");
        field.setAccessible(true);
        field.set(vacancyCoverService, 5 * 1024 * 1024L);
    }

    private Vacancy createMockVacancy(Long createdByUserId, String coverImageKey) {
        Vacancy vacancy = new Vacancy();
        vacancy.setId(VACANCY_ID);
        vacancy.setCreatedBy(createdByUserId);
        Project project = new Project();
        project.setId(PROJECT_ID);
        vacancy.setProject(project);
        vacancy.setCoverImageKey(Objects.requireNonNullElse(coverImageKey, ""));
        return vacancy;
    }

    @Nested
    @DisplayName("Tests for uploadVacancyCover method")
    class UploadVacancyCoverTests {
        @Test
        void testUploadVacancyCover_Success_UserIsCreator() {
            Vacancy mockVacancy = createMockVacancy(USER_ID, null);
            MockMultipartFile multipartFile = new MockMultipartFile(
                    "file", "test.jpg", "image/jpeg", "test image content".getBytes());
            ProcessedImage processedImage = new ProcessedImage(
                    new ByteArrayInputStream("processed content".getBytes()),
                    20L,
                    "image/jpeg",
                    "jpg"
            );

            when(vacancyRepositoryAdapter.getVacancyOrThrow(VACANCY_ID)).thenReturn(mockVacancy);
            when(userContext.getUserId()).thenReturn(USER_ID);
            when(imageUtility.processAndResizeImage(any(InputStream.class), anyString(), anyString()))
                    .thenReturn(processedImage);

            vacancyCoverService.uploadVacancyCover(VACANCY_ID, multipartFile);

            verify(s3ServiceInterface).uploadObject(
                    eq(BUCKET_NAME),
                    anyString(),
                    any(InputStream.class),
                    eq(processedImage.size()),
                    eq(processedImage.contentType())
            );
            verify(vacancyRepositoryAdapter).save(mockVacancy);
            assertNotNull(mockVacancy.getCoverImageKey());
            System.out.println("Cover image key: " + mockVacancy.getCoverImageKey());
            assertFalse(mockVacancy.getCoverImageKey().isBlank());
        }

        @Test
        void testUploadVacancyCover_Success_UserIsManager() {
            Long creatorId = USER_ID + 1;
            Vacancy mockVacancy = createMockVacancy(creatorId, null);
            MockMultipartFile multipartFile = new MockMultipartFile(
                    "file", "test.jpg", "image/jpeg", "test image content".getBytes());
            ProcessedImage processedImage = new ProcessedImage(
                    new ByteArrayInputStream("processed content".getBytes()),
                    20L,
                    "image/jpeg",
                    "jpg"
            );

            when(vacancyRepositoryAdapter.getVacancyOrThrow(VACANCY_ID)).thenReturn(mockVacancy);
            when(userContext.getUserId()).thenReturn(USER_ID);
            doNothing().when(teamMemberServiceAdapter).assertOwnerOrManager(PROJECT_ID, USER_ID);
            when(imageUtility.processAndResizeImage(any(InputStream.class), anyString(), anyString()))
                    .thenReturn(processedImage);

            vacancyCoverService.uploadVacancyCover(VACANCY_ID, multipartFile);

            verify(s3ServiceInterface).uploadObject(
                    eq(BUCKET_NAME),
                    anyString(),
                    any(InputStream.class),
                    eq(processedImage.size()),
                    eq(processedImage.contentType())
            );
            verify(vacancyRepositoryAdapter).save(mockVacancy);
            assertNotNull(mockVacancy.getCoverImageKey());
        }

        @Test
        void testUploadVacancyCover_EmptyFile_ThrowsImageProcessingException() {
            Vacancy mockVacancy = createMockVacancy(USER_ID, null);
            MockMultipartFile emptyFile = new MockMultipartFile(
                    "file", "test.jpg", "image/jpeg", new byte[0]);

            when(vacancyRepositoryAdapter.getVacancyOrThrow(VACANCY_ID)).thenReturn(mockVacancy);
            when(userContext.getUserId()).thenReturn(USER_ID);

            ImageProcessingException exception = assertThrows(ImageProcessingException.class,
                    () -> vacancyCoverService.uploadVacancyCover(VACANCY_ID, emptyFile));
            assertEquals("Uploaded file is empty.", exception.getMessage());
        }

        @Test
        void testUploadVacancyCover_FileTooLarge_ThrowsImageProcessingException() {
            Vacancy mockVacancy = createMockVacancy(USER_ID, null);
            byte[] largeContent = new byte[6 * 1024 * 1024]; // 6MB
            MockMultipartFile largeFile = new MockMultipartFile(
                    "file", "large.jpg", "image/jpeg", largeContent);

            when(vacancyRepositoryAdapter.getVacancyOrThrow(VACANCY_ID)).thenReturn(mockVacancy);
            when(userContext.getUserId()).thenReturn(USER_ID);

            ImageProcessingException exception = assertThrows(ImageProcessingException.class,
                    () -> vacancyCoverService.uploadVacancyCover(VACANCY_ID, largeFile));
            assertEquals("File exceeds maximum allowed size of 5MB.", exception.getMessage());
        }

        @Test
        void testUploadVacancyCover_IOExceptionDuringProcessing_ThrowsImageProcessingException() throws IOException {
            Vacancy mockVacancy = createMockVacancy(USER_ID, null);
            MultipartFile mockFile = mock(MultipartFile.class);

            when(vacancyRepositoryAdapter.getVacancyOrThrow(VACANCY_ID)).thenReturn(mockVacancy);
            when(userContext.getUserId()).thenReturn(USER_ID);
            when(mockFile.isEmpty()).thenReturn(false);
            when(mockFile.getSize()).thenReturn(1024L); // Valid size
            when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
            when(mockFile.getContentType()).thenReturn("image/jpeg");
            when(mockFile.getInputStream()).thenThrow(new IOException("Test IOException"));

            ImageProcessingException exception = assertThrows(ImageProcessingException.class,
                    () -> vacancyCoverService.uploadVacancyCover(VACANCY_ID, mockFile));
            assertTrue(exception.getMessage().startsWith("Failed to read or process image file for vacancy"));
        }

        @Test
        void testUploadVacancyCover_ImageUtilityThrowsException_PropagatesException() {
            Vacancy mockVacancy = createMockVacancy(USER_ID, null);
            MockMultipartFile multipartFile = new MockMultipartFile(
                    "file", "test.jpg", "image/jpeg", "test image content".getBytes());

            when(vacancyRepositoryAdapter.getVacancyOrThrow(VACANCY_ID)).thenReturn(mockVacancy);
            when(userContext.getUserId()).thenReturn(USER_ID);
            when(imageUtility.processAndResizeImage(any(InputStream.class), anyString(), anyString()))
                    .thenThrow(new ImageProcessingException("message utility error"));

            ImageProcessingException exception = assertThrows(ImageProcessingException.class,
                    () -> vacancyCoverService.uploadVacancyCover(VACANCY_ID, multipartFile));
            assertEquals("message utility error", exception.getMessage());
        }

        @Test
        void testUploadVacancyCover_S3UploadThrowsException_PropagatesS3OperationException() {
            Vacancy mockVacancy = createMockVacancy(USER_ID, null);
            MockMultipartFile multipartFile = new MockMultipartFile(
                    "file", "test.jpg", "image/jpeg", "test image content".getBytes());
            ProcessedImage processedImage = new ProcessedImage(
                    new ByteArrayInputStream("processed content".getBytes()),
                    20L,
                    "image/jpeg",
                    "jpg"
            );

            when(vacancyRepositoryAdapter.getVacancyOrThrow(VACANCY_ID)).thenReturn(mockVacancy);
            when(userContext.getUserId()).thenReturn(USER_ID);
            when(imageUtility.processAndResizeImage(any(InputStream.class), anyString(), anyString()))
                    .thenReturn(processedImage);
            doThrow(new S3OperationException("S3 upload failed"))
                    .when(s3ServiceInterface).uploadObject(anyString(), anyString(), any(InputStream.class), anyLong(), anyString());

            S3OperationException exception = assertThrows(S3OperationException.class,
                    () -> vacancyCoverService.uploadVacancyCover(VACANCY_ID, multipartFile));
            assertEquals("S3 upload failed", exception.getMessage());
            verify(vacancyRepositoryAdapter, never()).save(any(Vacancy.class));
        }

        @Test
        void testUploadVacancyCover_UnexpectedException_ThrowsRuntimeException() {
            Vacancy mockVacancy = createMockVacancy(USER_ID, null);
            MockMultipartFile multipartFile = new MockMultipartFile(
                    "file", "test.jpg", "image/jpeg", "test image content".getBytes());

            when(vacancyRepositoryAdapter.getVacancyOrThrow(VACANCY_ID)).thenReturn(mockVacancy);
            when(userContext.getUserId()).thenReturn(USER_ID);
            when(imageUtility.processAndResizeImage(any(InputStream.class), anyString(), anyString()))
                    .thenThrow(new NullPointerException("Unexpected NPE"));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> vacancyCoverService.uploadVacancyCover(VACANCY_ID, multipartFile));

            assertTrue(exception.getMessage().startsWith("Unexpected error processing cover image for vacancy"));
            assertEquals("Unexpected NPE", exception.getCause().getMessage());
        }

        @Test
        void testUploadVacancyCover_IOExceptionInProcessedImageInputStream_ThrowsImageProcessingException()
                throws IOException {
            Vacancy mockVacancy = createMockVacancy(USER_ID, null);
            MockMultipartFile multipartFile = new MockMultipartFile(
                    "file",
                    "test-image.jpg",
                    "image/jpeg",
                    "test image content".getBytes()
            );
            InputStream faultyInputStream = mock(InputStream.class);
            IOException ioExceptionToThrow = new IOException("Faulty processed stream on close");

            doThrow(ioExceptionToThrow).when(faultyInputStream).close();

            ProcessedImage processedImage = new ProcessedImage(
                    faultyInputStream,
                    20L,
                    "image/png",
                    "png"
            );

            when(vacancyRepositoryAdapter.getVacancyOrThrow(VACANCY_ID)).thenReturn(mockVacancy);
            when(imageUtility.processAndResizeImage(
                    any(InputStream.class),
                    anyString(),
                    anyString())
            ).thenReturn(processedImage);

            ImageProcessingException exception = assertThrows(ImageProcessingException.class, () ->
                    vacancyCoverService.uploadVacancyCover(VACANCY_ID, multipartFile)
            );
            assertTrue(exception.getMessage().startsWith("Failed to read or process image file for vacancy"));
            assertNotNull(exception.getCause(), "The cause of the ImageProcessingException " +
                    "should be the original IOException.");
            assertInstanceOf(IOException.class, exception.getCause(), "The cause should be an IOException.");
            assertEquals("Faulty processed stream on close", exception.getCause().getMessage(),
                    "The cause message should match the mocked IOException.");
        }
    }

    @Nested
    @DisplayName("Tests for deleteVacancyCover method")
    class DeleteVacancyCoverTests {
        @Test
        void testDeleteVacancyCover_Success_UserIsCreator() {
            String coverKey = "covers/vacancy-1/some-uuid.png";
            Vacancy mockVacancy = createMockVacancy(USER_ID, coverKey);

            when(vacancyRepositoryAdapter.getVacancyOrThrow(VACANCY_ID)).thenReturn(mockVacancy);
            when(userContext.getUserId()).thenReturn(USER_ID);
            doNothing().when(s3ServiceInterface).removeObject(BUCKET_NAME, coverKey);

            vacancyCoverService.deleteVacancyCover(VACANCY_ID);

            verify(s3ServiceInterface).removeObject(BUCKET_NAME, coverKey);
            verify(vacancyRepositoryAdapter).save(mockVacancy);
            assertNull(mockVacancy.getCoverImageKey());
        }

        @Test
        void testDeleteVacancyCover_Success_UserIsManager() {
            Long creatorId = USER_ID + 1;
            String coverKey = "covers/vacancy-1/some-uuid.png";
            Vacancy mockVacancy = createMockVacancy(creatorId, coverKey);

            when(vacancyRepositoryAdapter.getVacancyOrThrow(VACANCY_ID)).thenReturn(mockVacancy);
            when(userContext.getUserId()).thenReturn(USER_ID);
            doNothing().when(teamMemberServiceAdapter).assertOwnerOrManager(PROJECT_ID, USER_ID);
            doNothing().when(s3ServiceInterface).removeObject(BUCKET_NAME, coverKey);

            vacancyCoverService.deleteVacancyCover(VACANCY_ID);

            verify(s3ServiceInterface).removeObject(BUCKET_NAME, coverKey);
            verify(vacancyRepositoryAdapter).save(mockVacancy);
            assertNull(mockVacancy.getCoverImageKey());
        }

        @Test
        void testDeleteVacancyCover_NoCoverImage_ReturnsEarly() {
            Vacancy mockVacancy = createMockVacancy(USER_ID, "");

            when(vacancyRepositoryAdapter.getVacancyOrThrow(VACANCY_ID)).thenReturn(mockVacancy);
            when(userContext.getUserId()).thenReturn(USER_ID);

            vacancyCoverService.deleteVacancyCover(VACANCY_ID);

            verify(s3ServiceInterface, never()).removeObject(anyString(), anyString());
            verify(vacancyRepositoryAdapter, never()).save(any(Vacancy.class));
        }

        @Test
        void testDeleteVacancyCover_NoCoverImageKeyIsNull_ReturnsEarly() {
            Vacancy mockVacancy = createMockVacancy(USER_ID, null);
            mockVacancy.setCoverImageKey(null);

            when(vacancyRepositoryAdapter.getVacancyOrThrow(VACANCY_ID)).thenReturn(mockVacancy);
            when(userContext.getUserId()).thenReturn(USER_ID);

            vacancyCoverService.deleteVacancyCover(VACANCY_ID);

            verify(s3ServiceInterface, never()).removeObject(anyString(), anyString());
            verify(vacancyRepositoryAdapter, never()).save(any(Vacancy.class));
        }

        @Test
        void testDeleteVacancyCover_S3ThrowsException_ThrowsS3OperationException() {
            String coverKey = "covers/vacancy-1/some-uuid.png";
            Vacancy mockVacancy = createMockVacancy(USER_ID, coverKey);

            when(vacancyRepositoryAdapter.getVacancyOrThrow(VACANCY_ID)).thenReturn(mockVacancy);
            when(userContext.getUserId()).thenReturn(USER_ID);
            doThrow(new S3OperationException("S3 delete failed"))
                    .when(s3ServiceInterface).removeObject(BUCKET_NAME, coverKey);

            S3OperationException exception = assertThrows(S3OperationException.class,
                    () -> vacancyCoverService.deleteVacancyCover(VACANCY_ID));
            assertEquals("Failed to delete cover image for vacancy " + VACANCY_ID, exception.getMessage());
            verify(vacancyRepositoryAdapter, never()).save(any(Vacancy.class));
        }

        @Test
        void testDeleteVacancyCover_RepositorySaveThrowsException_ThrowsS3OperationException() {
            String coverKey = "covers/vacancy-1/some-uuid.png";
            Vacancy mockVacancy = createMockVacancy(USER_ID, coverKey);

            when(vacancyRepositoryAdapter.getVacancyOrThrow(VACANCY_ID)).thenReturn(mockVacancy);
            when(userContext.getUserId()).thenReturn(USER_ID);
            doNothing().when(s3ServiceInterface).removeObject(BUCKET_NAME, coverKey);
            doThrow(new RuntimeException("DB save failed")).when(vacancyRepositoryAdapter).save(mockVacancy);


            S3OperationException exception = assertThrows(S3OperationException.class,
                    () -> vacancyCoverService.deleteVacancyCover(VACANCY_ID));
            verify(s3ServiceInterface).removeObject(BUCKET_NAME, coverKey);
            verify(vacancyRepositoryAdapter).save(mockVacancy);
            assertTrue(exception.getMessage().startsWith("Failed to delete cover image for vacancy"));
            assertEquals("DB save failed",exception.getCause().getMessage());
        }
    }
}
