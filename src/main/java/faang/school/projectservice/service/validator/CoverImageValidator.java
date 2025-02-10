package faang.school.projectservice.service.validator;

import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoverImageValidator {
    private final VacancyRepository vacancyRepository;
    private final ResourceRepository resourceRepository;

    public Vacancy validateUploadCover(Long currentUserId, Long vacancyId) {
        if (!vacancyRepository.existsById(vacancyId)) {
            log.error("Вакансия с ID {} не найдена в базе данных", vacancyId);
            throw new EntityNotFoundException("Вакансии с id " + vacancyId + "не существует");
        }
        Vacancy vacancy = vacancyRepository.findById(vacancyId).get();

        if (!currentUserId.equals(vacancy.getProject().getOwnerId())) {
            log.error("Пользователь с ID {} не имеет прав загружать обложку для вакансии с ID {}",
                    currentUserId, vacancyId);
            throw new BusinessException("У вас нет прав загрузить обложку в данную вакансию");
        }
        return vacancy;
    }

    public Resource validateDeleteCover(Long currentUserId, Long resourceId) {
        validateResource(resourceId);
        Resource resource = resourceRepository.findById(resourceId).get();

        if (!currentUserId.equals(resource.getProject().getOwnerId())) {
            log.error("Пользователь с ID {} не имеет прав удалять обложку ID {}", currentUserId, resourceId);
            throw new BusinessException("У вас нет прав удалять обложку из данной вакансии");
        }
        return resource;
    }

    public void validateResource(Long resourceId) {
        if (!resourceRepository.existsById(resourceId)) {
            log.error("Обложки с ID {} не найдено в базе данных", resourceId);
            throw new EntityNotFoundException("Обложка не найдена");
        }
    }
}
