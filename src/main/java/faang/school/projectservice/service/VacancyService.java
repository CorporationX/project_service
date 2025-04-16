package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.FileData;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@AllArgsConstructor
public class VacancyService {
    private final VacancyMapper vacancyMapper;
    private final S3Service s3Service;
    private final VacancyRepository vacancyRepository;
    private final UserContext userContext;

    public VacancyDto getVacancy(Long id) {
        Vacancy vacancy = findVacancy(id);
        return vacancyMapper.toDto(vacancy);
    }

    @Transactional
    public String saveCoverImage(Long id, MultipartFile file) {
        Vacancy vacancy = findVacancy(id);
        FileData fileData = s3Service.uploadFile(file, getFolder(vacancy));
        vacancy.setCoverImageKey(fileData.getKey());
        Vacancy vacancyWithImageKey = vacancyRepository.save(vacancy);
        return vacancyWithImageKey.getCoverImageKey();
    }

    @Transactional
    public void deleteCoverImageFromVacancy(Long vacancyId) {
        Vacancy vacancy = findVacancy(vacancyId);
        checkCreatorAndOwner(vacancy);
        s3Service.deleteFile(vacancy.getCoverImageKey());
        vacancy.setCoverImageKey(null);
        vacancyRepository.save(vacancy);
    }

    private Vacancy findVacancy(Long id) {
        return vacancyRepository.findById(id)
                .orElseThrow(
                        () ->
                        {
                            log.error("Vacancy with id {} not found", id);
                            return new EntityNotFoundException(
                                    String.format("Vacancy with id %s not found", id)
                            );
                        }
                );
    }

    private void checkCreatorAndOwner(Vacancy vacancy) {
        Long userId = userContext.getUserId();
        if (!(vacancy.getCreatedBy().equals(userId) || userId.equals(vacancy.getProject().getOwnerId()))) {
            log.error("Удаление обложки вакансии {} доступно только автору вакансии или владельцу проекта",
                    vacancy.getId()
            );
            throw new DataValidationException(
                    String.format("Удаление обложки вакансии %d доступно только автору вакансии или владельцу проекта",
                            vacancy.getId()
                    )
            );
        }
    }

    private String getFolder(Vacancy vacancy) {
        return "%d%s/vacancy/".formatted(vacancy.getProject().getId(), vacancy.getProject().getName());
    }
}
