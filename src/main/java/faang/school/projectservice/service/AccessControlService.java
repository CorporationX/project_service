package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessControlService {

    private final VacancyRepository vacancyRepository;
    private final UserContext userContext;

    public void checkAccess(long vacancyId) {
        long currentUserId = userContext.getUserId();

        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new EntityNotFoundException("Vacancy not found"));

        boolean isAuthor = vacancy.getCreatedBy() == currentUserId;
        boolean isProjectOwner = vacancy.getProject() != null &&
                vacancy.getProject().getOwnerId() != null &&
                vacancy.getProject().getOwnerId().equals(currentUserId);

        if (!isAuthor && !isProjectOwner) {
            throw new AccessDeniedException("Access denied");
        }
    }
}
