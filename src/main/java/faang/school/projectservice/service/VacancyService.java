package faang.school.projectservice.service;

import faang.school.projectservice.config.cover.VacancyCoverConfiguration;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.utils.ImageResizer;
import faang.school.projectservice.validation.CoverValidator;
import org.springframework.core.io.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Сервис для управления обложками вакансий.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final S3Service s3Service;
    private final CoverValidator coverValidator;
    private final ImageResizer<VacancyCoverConfiguration> imageResizer;
    private final VacancyCoverConfiguration coverConfig;

    /**
     * Загружает обложку для проекта.
     *
     * @param vacancyId идентификатор проекта
     * @param image файл изображения для обложки
     */
    @Transactional
    public void uploadCover(long vacancyId,
                            MultipartFile image) {
        coverValidator.validateBasics(image, coverConfig);

        if (coverValidator.isImageOversize(image, coverConfig)) {
            image = imageResizer.resizeImage(image, coverConfig);
        }

        Vacancy vacancy = getVacancy(vacancyId);
        String oldKey = vacancy.getCoverImageKey();
        String folder = String.format("vacancies/%d/cover", vacancyId);

        String key = s3Service.uploadImage(folder, image);
        vacancy.setCoverImageKey(key);

        if (oldKey != null) {
            s3Service.deleteImage(oldKey);
        }
    }

    /**
     * Удаляет обложку проекта.
     *
     * @param vacancyId идентификатор проекта
     * @throws DataValidationException если у проекта нет обложки
     */
    @Transactional
    public void deleteCover(long vacancyId) {
        Vacancy vacancy = getVacancy(vacancyId);
        String key = findVacancyCoverKey(vacancy);
        s3Service.deleteImage(key);
        vacancy.setCoverImageKey(null);
    }

    /**
     * Получает обложку проекта.
     *
     * @param vacancyId идентификатор вакансии
     * @return полученная обложка
     * @throws DataValidationException если у проекта нет обложки
     */
    @Transactional
    public Resource getCover(long vacancyId) {
        Vacancy vacancy = getVacancy(vacancyId);
        String key = findVacancyCoverKey(vacancy);
        return s3Service.getImage(key);
    }

    /**
     * Получает проект по идентификатору.
     *
     * @param vacancyId идентификатор проекта
     * @return найденный проект
     * @throws EntityNotFoundException если проект не найден
     */
    private Vacancy getVacancy(long vacancyId) {
        return vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new EntityNotFoundException("Vacancy not found"));
    }

    /**
     * Находит ключ обложки вакансии.
     *
     * @param vacancy вакансия
     * @return ключ обложки
     * @throws DataValidationException если ключ обложки равен null
     */
    private String findVacancyCoverKey(Vacancy vacancy) {
        String key = vacancy.getCoverImageKey();
        if (key == null) {
            log.error("Cover image id is null");
            throw new DataValidationException("Cover image id is null");
        }
        return key;
    }
}
