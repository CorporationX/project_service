package faang.school.projectservice.service;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.s3.S3ServiceImpl;
import faang.school.projectservice.validation.FileValidator;
import lombok.AllArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AccessDeniedException;
import java.util.UUID;

@AllArgsConstructor
public class VacancyService {

    private S3ServiceImpl s3Service;
    private VacancyRepository vacancyRepository;

    public void updateVacancyCoverImage(Long vacancyId, MultipartFile file, Long userId) throws AccessDeniedException {
        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new NotFoundException("Vacancy not found"));

        if (!isUserAuthorizedToModifyVacancy(vacancy, userId)) {
            throw new AccessDeniedException("You do not have permission to modify this vacancy.");
        }
        FileValidator.validateFile(file);

        try {
            InputStream resizedImage = S3ServiceImpl.resizeImage(file, 512, "jpeg");
            long contentLength = resizedImage.available();

            String keyName = "vacancy-covers/" + UUID.randomUUID() + ".jpeg";

            s3Service.uploadToS3(keyName, resizedImage, contentLength, file);

            vacancy.setCoverImageKey(keyName);
            vacancyRepository.save(vacancy);
        } catch (IOException e) {
            throw new RuntimeException("Error processing the image", e);
        }
    }

    public void deleteVacancyCoverImage(Long vacancyId, Long userId) throws AccessDeniedException {
        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new NotFoundException("Vacancy not found"));

        if (!isUserAuthorizedToModifyVacancy(vacancy, userId)) {
            throw new AccessDeniedException("You do not have permission to delete this image.");
        }

        String keyName = vacancy.getCoverImageKey();
        if (keyName != null) {
            s3Service.deleteFromS3(keyName);
            vacancy.setCoverImageKey(null);
            vacancyRepository.save(vacancy);
        }
    }

    private boolean isUserAuthorizedToModifyVacancy(Vacancy vacancy, Long userId) {
        return vacancy.getCreatedBy().equals(userId) || vacancy.getProject().getOwnerId().equals(userId);
    }
}
