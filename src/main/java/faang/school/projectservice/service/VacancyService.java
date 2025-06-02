package faang.school.projectservice.service;

import faang.school.projectservice.util.ImageResizer;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final MinioService minioService;
    private final AccessControlService accessControlService;

    public void uploadCover(Long vacancyId, MultipartFile file) throws Exception {
        long fileLimitMb = 5L;
        long fileLimitBytes = fileLimitMb * 1024 * 1024;

        accessControlService.checkAccess(vacancyId);
        if (file.getSize() > fileLimitBytes) {
            throw new FileSizeLimitExceededException("File must be smaller than 5MB",
                    file.getSize(), fileLimitBytes);
        }

        byte[] originalBytes = file.getBytes();
        byte[] resizedBytes = ImageResizer.resizeImage(originalBytes);
        String fileName = file.getOriginalFilename();

        String coverImageKey = minioService.storeFile(fileName, resizedBytes);
        Vacancy vacancy = vacancyRepository.findById(vacancyId).orElseThrow(() -> new EntityNotFoundException("Vacancy not found"));
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
}
