package faang.school.projectservice.service.presentation.uploader;

import faang.school.projectservice.config.s3.UploaderProperties;
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

    private final UploaderProperties uploaderProperties;

    @Override
    public void uploadPdf(Project project, File pdfFile, String fileKey, String contentType) {
        validateFile(pdfFile);
        deleteOldPresentationIfExists(project);
        try (InputStream inputStream = new FileInputStream(pdfFile)) {
            s3Service.upload(inputStream, fileKey, pdfFile.length(), contentType);
        } catch (IOException e) {
            log.error("IOException while uploading PDF for project {} with key {}", project.getId(), fileKey, e);
            throw new FileUploadException("Failed to upload presentation for project " + project.getId());
        }
    }

    private void validateFile(File file) {
        if (file == null || !file.exists()) {
            log.error("Validation failed: file is null or does not exist");
            throw new FileUploadException("File is null or does not exist");
        }

        long maxSize = uploaderProperties.maxFileSize().toBytes();
        if (file.length() > maxSize) {
            log.error("Validation failed: file size {} exceeds max {}", file.length(), maxSize);
            throw new FileUploadException("File size exceeds max limit: " +
                    uploaderProperties.maxFileSize().toMegabytes() + " MB");
        }
    }

    private void deleteOldPresentationIfExists(Project project) {
        String existingKey = project.getPresentationFileKey();
        if (existingKey != null && !existingKey.isBlank()) {
            s3Service.delete(existingKey);
        }
    }
}
