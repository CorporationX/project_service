package faang.school.projectservice.service;

import faang.school.projectservice.dto.coverImageVacancy.ResourceDto;
import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.multipartFile.CustomMultipartFile;
import faang.school.projectservice.service.s3.S3ServiceCover;
import faang.school.projectservice.service.validator.CoverImageValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class CoverImageService {
    private final VacancyRepository vacancyRepository;
    private final ResourceRepository resourceRepository;
    private final S3ServiceCover s3ServiceCover;
    private final CoverImageValidator coverImageValidator;
    private final ResourceMapper resourceMapper;


    @Transactional
    public ResourceDto uploadCover(Long currentUserId, Long vacancyId, MultipartFile file) {
        Vacancy vacancy = coverImageValidator.validateUploadCover(currentUserId, vacancyId);
        String folder = vacancyId + vacancy.getName();
        Resource resource = s3ServiceCover.uploadFile(compressImage(file), folder);
        resource.setProject(vacancy.getProject());
        resource = resourceRepository.save(resource);
        vacancy.setCoverImageKey(resource.getKey());
        vacancyRepository.save(vacancy);
        return resourceMapper.toDto(resource);
    }

    @Transactional
    public void deleteCover(Long currentUserId, Long resourceId) {
        Resource resource = coverImageValidator.validateDeleteCover(currentUserId, resourceId);
        s3ServiceCover.deleteResource(resource);
        resourceRepository.delete(resource);
        List<Vacancy> vacancies = resource.getProject().getVacancies();
        for (Vacancy vacancy : vacancies) {
            if (vacancy.getCoverImageKey() != null && vacancy.getCoverImageKey().equals(resource.getKey())) {
                vacancy.setCoverImageKey(null);
                vacancyRepository.save(vacancy);
                log.debug("CoverImageKey успешно удален из vacancy ID {}", vacancy.getId());
                return;
            }
        }
    }

    public InputStream getCoverImage(Long resourceId) {
        coverImageValidator.validateResource(resourceId);
        return s3ServiceCover.getCoverImage(resourceRepository.findById(resourceId).get());
    }

    private MultipartFile compressImage(MultipartFile file) {
        final int maximumPixelSize = 512;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            Thumbnails.of(file.getInputStream())
                    .size(maximumPixelSize, maximumPixelSize)
                    .outputFormat("jpg")
                    .toOutputStream(outputStream);
            log.debug("Изображение успешно прошло сжатие");
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new BusinessException("Не удалось сжать изображение");
        }
        return new CustomMultipartFile(outputStream.toByteArray(),
                file.getName(), file.getOriginalFilename(), "image/jpeg");
    }
}
