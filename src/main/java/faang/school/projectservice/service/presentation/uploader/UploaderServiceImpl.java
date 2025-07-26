package faang.school.projectservice.service.presentation.uploader;

import faang.school.projectservice.exeption.FileUploadException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploaderServiceImpl implements UploaderService {

    private final S3Service s3Service;

    @Override
    public void uploadPdf(Project project, File pdfFile, String fileKey) {
        validateFile(pdfFile);
        deleteOldPresentationIfExists(project);
        try (InputStream inputStream = new FileInputStream(pdfFile)) {
            s3Service.upload(inputStream, fileKey, pdfFile.length());
        } catch (IOException e) {
            log.error("IOException while uploading PDF for project {} with key {}", project.getId(), fileKey, e);
            throw new FileUploadException("Failed to upload presentation for project " + project.getId());
        } finally {
            deleteTempFile(pdfFile);
        }
    }

    private void validateFile(File pdfFile) {
        if (pdfFile == null || !pdfFile.exists()) {
            log.error("Validation failed: PDF file is null or does not exist");
            throw new FileUploadException("PDF file is null or does not exist");
        }
    }

    private void deleteOldPresentationIfExists(Project project) {
        String existingKey = project.getPresentationFileKey();
        if (existingKey != null && !existingKey.isBlank()) {
            s3Service.delete(existingKey);
        }
    }

    private void deleteTempFile(File file) {
        if (file.exists() && !file.delete()) {
            file.deleteOnExit();
        }
    }
}
