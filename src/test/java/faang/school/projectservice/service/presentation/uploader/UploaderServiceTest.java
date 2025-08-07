package faang.school.projectservice.service.presentation.uploader;

import faang.school.projectservice.config.s3.UploaderProperties;
import faang.school.projectservice.exeption.FileUploadException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.unit.DataSize;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class UploaderServiceTest {

    @Mock
    private S3Service s3Service;

    private UploaderServiceImpl uploaderService;

    private static final long PROJECT_ID = 1L;
    private static final String FILE_KEY = "test-key";
    private static final String OLD_KEY = "old-key";
    private static final String NON_EXISTENT_FILE = "nonexistent.pdf";
    private static final String CONTENT_TYPE_PDF = "application/pdf";

    @BeforeEach
    void setUp() {
        UploaderProperties uploaderProperties = new UploaderProperties(DataSize.ofMegabytes(5));
        uploaderService = new UploaderServiceImpl(s3Service, uploaderProperties);
    }

    @Test
    @DisplayName("Should upload PDF successfully and delete old file if exists")
    void shouldUploadPdfSuccessfully() throws IOException {
        File file = createTempFileWithContent("PDF content");
        long fileLength = file.length();
        Project project = buildProject(OLD_KEY);

        uploaderService.uploadPdf(project, file, FILE_KEY, CONTENT_TYPE_PDF);

        verify(s3Service).delete(OLD_KEY);
        verify(s3Service).upload(any(InputStream.class), eq(FILE_KEY), eq(fileLength), eq(CONTENT_TYPE_PDF));

        if (file.exists()) {
            assertTrue(file.delete(), "Temporary file was not deleted");
        }
    }

    @Test
    @DisplayName("Should throw if file is null")
    void shouldThrowIfFileIsNull() {
        Project project = buildProject(null);

        FileUploadException exception = assertThrows(FileUploadException.class,
                () -> uploaderService.uploadPdf(project, null, FILE_KEY, CONTENT_TYPE_PDF));

        assertEquals("File is null or does not exist", exception.getMessage());
        verifyNoInteractions(s3Service);
    }

    @Test
    @DisplayName("Should throw if file does not exist")
    void shouldThrowIfFileDoesNotExist() {
        File nonexistent = new File(NON_EXISTENT_FILE);
        Project project = buildProject(null);

        FileUploadException exception = assertThrows(FileUploadException.class,
                () -> uploaderService.uploadPdf(project, nonexistent, FILE_KEY, CONTENT_TYPE_PDF));

        assertEquals("File is null or does not exist", exception.getMessage());
        verifyNoInteractions(s3Service);
    }

    @Test
    @DisplayName("Should throw if file exceeds maxFileSize")
    void shouldThrowIfFileIsTooLarge() throws IOException {
        File file = createTempFileWithSize(10);
        long fileLength = file.length();
        Project project = buildProject(null);

        UploaderProperties smallLimit = new UploaderProperties(DataSize.ofBytes(5));
        uploaderService = new UploaderServiceImpl(s3Service, smallLimit);

        FileUploadException exception = assertThrows(FileUploadException.class,
                () -> uploaderService.uploadPdf(project, file, FILE_KEY, CONTENT_TYPE_PDF));

        assertTrue(exception.getMessage().startsWith("File size exceeds max limit"));
        verifyNoInteractions(s3Service);
    }

    private File createTempFileWithContent(String content) throws IOException {
        File file = File.createTempFile("upload-test", ".pdf");
        try (FileOutputStream out = new FileOutputStream(file)) {
            out.write(content.getBytes());
        }
        return file;
    }

    private File createTempFileWithSize(int sizeInBytes) throws IOException {
        File file = File.createTempFile("upload-test", ".pdf");
        try (FileOutputStream out = new FileOutputStream(file)) {
            byte[] content = new byte[sizeInBytes];
            out.write(content);
        }
        return file;
    }

    private Project buildProject(String fileKey) {
        return Project.builder()
                .id(PROJECT_ID)
                .presentationFileKey(fileKey)
                .build();
    }
}
