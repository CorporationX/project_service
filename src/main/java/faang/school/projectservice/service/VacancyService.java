package faang.school.projectservice.service;

import faang.school.projectservice.addon.ImageResizer;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final MinioService minioService;
    private final AccessControlService accessControlService;

    public void uploadCover(Long vacancyId, MultipartFile file) throws Exception {
        accessControlService.checkAccess(vacancyId); // 🔥 проверка доступа

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("File size exceeds 5MB limit");
        }

        byte[] originalBytes = file.getBytes();
        byte[] resizedBytes = ImageResizer.resizeImage(originalBytes);
        String fileName = file.getOriginalFilename();

        String coverImageKey = minioService.storeFile(fileName, resizedBytes); // 🔥 передаём байты
        Vacancy vacancy = vacancyRepository.findById(vacancyId).orElseThrow(() -> new RuntimeException("Vacancy not found"));
        vacancy.setCoverImageKey(coverImageKey);
        vacancyRepository.save(vacancy);
    }

    public void deleteCover(Long vacancyId) throws Exception {
        accessControlService.checkAccess(vacancyId);

        Vacancy vacancy = vacancyRepository.findById(vacancyId).orElseThrow(() -> new RuntimeException("Vacancy not found"));
        if (vacancy.getCoverImageKey() != null) {
            minioService.deleteFile(vacancy.getCoverImageKey());
            vacancy.setCoverImageKey(null);
            vacancyRepository.save(vacancy);
        }
    }

    private void validateFile(MultipartFile file) throws IOException {
        byte[] imageToBytes = file.getBytes();
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("File size exceeds 5MB limit");
        }
        try {
            ImageResizer.resizeImage(imageToBytes);
        } catch (IOException e) {
            throw new RuntimeException("Invalid image size");
        }
    }
}
