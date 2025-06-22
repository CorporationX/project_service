package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.excepcion.ImageProcessingException;
import faang.school.projectservice.excepcion.S3OperationException;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.adapter.vacancy.VacancyRepositoryAdapter;
import faang.school.projectservice.service.adapter.TeamMemberServiceAdapter;
import faang.school.projectservice.service.s3.S3ServiceInterface;
import faang.school.projectservice.utility.TwelveMonkeysImageUtility;
import faang.school.projectservice.model.internal.ProcessedImage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VacancyCoverServiceImpl implements VacancyCoverService {

    private final UserContext userContext;
    private final S3ServiceInterface s3ServiceInterface;
    private final TwelveMonkeysImageUtility imageUtility;
    private final VacancyRepositoryAdapter vacancyRepositoryAdapter;
    private final TeamMemberServiceAdapter teamMemberServiceAdapter;

    @Value("${services.s3.bucketName}")
    private String coversBucket;

    @Value("${services.upload.vacancy-cover.maxFileSize}")
    private long maxImageSizeBytes;

    @Override
    @Transactional
    public void uploadVacancyCover(Long vacancyId, MultipartFile multipartFile) {
        Vacancy vacancy = getVacancyOrThrow(vacancyId);
        log.info("Processing direct cover upload for vacancy ID: {}. Original filename: {}",
                vacancyId, multipartFile.getOriginalFilename());
        if (multipartFile.isEmpty()) {
            log.warn("Multipart file is empty for vacancy ID: {}", vacancyId);
            throw new ImageProcessingException("Uploaded file is empty.");
        }
        if (multipartFile.getSize() > maxImageSizeBytes) {
            log.error("File size {} bytes for vacancy ID {} exceeds limit of {} bytes.",
                    multipartFile.getSize(), vacancyId, maxImageSizeBytes);
            throw new ImageProcessingException("File exceeds maximum allowed size of 5MB.");
        }
        String originalContentType = multipartFile.getContentType();
        String tempObjectNameForUtility = String.format("vacancy-%d-temp-%s-%s",
                vacancyId, UUID.randomUUID(), multipartFile.getOriginalFilename());

        try (InputStream originalImageStream = multipartFile.getInputStream()) {
            log.info("Processing image for vacancy ID: {} with TwelveMonkeysImageUtility...", vacancyId);
            ProcessedImage processedImage = imageUtility.processAndResizeImage(
                    originalImageStream,
                    tempObjectNameForUtility,
                    originalContentType
            );
            log.info("Image processed successfully for vacancy ID: {}. New content type: {}, new size: {}",
                    vacancyId, processedImage.contentType(), processedImage.size());

            String finalObjectKey = String.format("covers/vacancy-%d/%s.%s",
                    vacancyId,
                    UUID.randomUUID(),
                    processedImage.outputExtension()
            );
            try (InputStream processedInputStream = processedImage.inputStream()) {
                s3ServiceInterface.uploadObject(
                        coversBucket,
                        finalObjectKey,
                        processedInputStream,
                        processedImage.size(),
                        processedImage.contentType()
                );
                log.info("Successfully uploaded processed cover for vacancy ID {} to S3: s3://{}/{}",
                        vacancyId, coversBucket, finalObjectKey);
            }
            vacancy.setCoverImageKey(finalObjectKey);
            vacancyRepositoryAdapter.save(vacancy);
            log.info("Successfully processed and saved cover for vacancy ID: {}. Final S3 key: {}",
                    vacancyId, finalObjectKey);
        } catch (IOException e) {
            log.error("IOException during processing cover for vacancy ID {}: {}", vacancyId, e.getMessage(), e);
            throw new ImageProcessingException("Failed to read or process image file for vacancy " + vacancyId, e);
        } catch (ImageProcessingException | S3OperationException e) {
            log.error("{} during cover upload for vacancy ID {}: {}",
                    e.getClass().getSimpleName(), vacancyId, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error processing cover for vacancy ID {}: {}", vacancyId, e.getMessage(), e);
            throw new RuntimeException(String.format("Unexpected error processing cover image for vacancy %d",
                    vacancyId), e);
        }
    }

    @Override
    @Transactional
    public void deleteVacancyCover(Long vacancyId) {
        log.info("Attempting to delete cover image for vacancy ID: {}", vacancyId);
        Vacancy vacancy = getVacancyOrThrow(vacancyId);
        String coverImageKey = vacancy.getCoverImageKey();
        if (coverImageKey == null || coverImageKey.isBlank()) {
            log.warn("No cover image found for vacancy ID: {}", vacancyId);
            return;
        }
        try {
            s3ServiceInterface.removeObject(coversBucket, coverImageKey);
            log.info("Successfully deleted cover image from vacancy ID {} from S3: s3://{}/{}",
                    vacancyId, coversBucket, coverImageKey);
            vacancy.setCoverImageKey(null);
            vacancyRepositoryAdapter.save(vacancy);
        } catch (Exception e) {
            log.error("Failed to delete cover image for vacancy ID {}: {}", vacancyId, e.getMessage(), e);
            throw new S3OperationException(String.format("Failed to delete cover image for vacancy %d", vacancyId), e);
        }
    }

    private Vacancy getVacancyOrThrow(Long vacancyId) {
        Vacancy vacancy = vacancyRepositoryAdapter.getVacancyOrThrow(vacancyId);
        log.debug("Vacancy with ID {} loaded successfully for cover upload.", vacancyId);
        if (userContext.getUserId() != vacancy.getCreatedBy()) {
            log.warn("User {} is not the creator of the vacancy ID {}. " +
                            "Asserting OWNER/MANAGER rights for project ID {}.",
                    userContext.getUserId(), vacancyId, vacancy.getProject().getId());
            teamMemberServiceAdapter.assertOwnerOrManager(vacancy.getProject().getId(), userContext.getUserId());
            log.info("User {} has OWNER/MANAGER rights for project of vacancy ID: {}",
                    userContext.getUserId(), vacancyId);
        }
        return vacancy;
    }
}
