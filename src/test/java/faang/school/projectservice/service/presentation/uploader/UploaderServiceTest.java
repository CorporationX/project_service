package faang.school.projectservice.service.presentation.uploader;

import faang.school.projectservice.exeption.FileUploadException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.s3.S3Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class UploaderServiceTest {

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private UploaderServiceImpl uploaderService;

    private static final long PROJECT_ID = 1L;
    private static final String FILE_KEY = "test-key";
    private static final String OLD_KEY = "old-key";
    private static final String NON_EXISTENT_FILE = "nonexistent.pdf";

    @Test
    @DisplayName("Should upload PDF successfully and delete old file if exists")
    void shouldUploadPdfSuccessfully() throws IOException {
        File file = createTempFile(true);
        long fileLength = file.length();
        Project project = buildProject(OLD_KEY);

        uploaderService.uploadPdf(project, file, FILE_KEY);

        verify(s3Service).delete(OLD_KEY);
        verify(s3Service).upload(any(InputStream.class), eq(FILE_KEY), eq(fileLength));
        assertFalse(file.exists());
    }

    @Test
    @DisplayName("Should throw if file is null")
    void shouldThrowIfFileIsNull() {
        Project project = buildProject(null);

        FileUploadException exception = assertThrows(FileUploadException.class,
                () -> uploaderService.uploadPdf(project, null, FILE_KEY));

        assertEquals("PDF file is null or does not exist", exception.getMessage());
        verifyNoInteractions(s3Service);
    }

    @Test
    @DisplayName("Should throw if file does not exist")
    void shouldThrowIfFileDoesNotExist() {
        File nonexistent = new File(NON_EXISTENT_FILE);
        Project project = buildProject(null);

        FileUploadException exception = assertThrows(FileUploadException.class,
                () -> uploaderService.uploadPdf(project, nonexistent, FILE_KEY));

        assertEquals("PDF file is null or does not exist", exception.getMessage());
        verifyNoInteractions(s3Service);
    }

    @Test
    @DisplayName("Should throw FileUploadException if IOException occurs during upload")
    void shouldThrowOnIOException() throws IOException {
        File file = createTempFile(true);
        long fileLength = file.length();
        Project project = buildProject(null);

        doAnswer(invocation -> {
            throw new IOException("Simulated failure");
        }).when(s3Service).upload(any(InputStream.class), eq(FILE_KEY), anyLong());

        FileUploadException exception = assertThrows(FileUploadException.class,
                () -> uploaderService.uploadPdf(project, file, FILE_KEY));

        assertTrue(exception.getMessage().contains("Failed to upload presentation"));
        verify(s3Service).upload(any(InputStream.class), eq(FILE_KEY), eq(fileLength));
    }

    private File createTempFile(boolean withContent) throws IOException {
        File file = File.createTempFile("upload-test", ".pdf");
        if (withContent) {
            try (FileOutputStream out = new FileOutputStream(file)) {
                out.write("PDF content".getBytes());
            }
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
