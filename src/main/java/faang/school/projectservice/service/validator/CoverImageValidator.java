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

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoverImageValidator {
    private final VacancyRepository vacancyRepository;
    private final ResourceRepository resourceRepository;

    public Vacancy validateUploadCover(Long currentUserId, Long vacancyId) {
       Optional <Vacancy> vacancy = vacancyRepository.findById(vacancyId);
        if (vacancy.isEmpty()) {
            throw new EntityNotFoundException("Вакансии с id " + vacancyId + " не существует");
        }
        if (!currentUserId.equals(vacancy.get().getProject().getOwnerId())) {
            throw new BusinessException("У вас нет прав загрузить обложку в данную вакансию");
        }
        return vacancy.get();
    }

    public Resource validateDeleteCover(Long currentUserId, Long resourceId) {
        Optional<Resource> resource = resourceRepository.findById(resourceId);
        if (resource.isEmpty()) {
            throw new EntityNotFoundException("Обложка c Id" + resourceId + " не найдена");
        }
        if (!currentUserId.equals(resource.get().getProject().getOwnerId())) {
            throw new BusinessException("У вас нет прав удалять обложку с id " + resourceId + " из данной вакансии");
        }
        return resource.get();
    }
}
