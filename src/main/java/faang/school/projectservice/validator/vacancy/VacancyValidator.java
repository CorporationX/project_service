package faang.school.projectservice.validator.vacancy;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class VacancyValidator {
    private final VacancyRepository vacancyRepository;
    private final ProjectRepository projectRepository;


    @Transactional(readOnly = true)
    public void validateExistingOfProject(long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new DataValidationException("The project doesn't exist in the System");
        }
    }

    @Transactional(readOnly = true)
    public void validateVacancyCreator(long authorId, long projectId){
        long ownerId = projectRepository.getProjectById(projectId).getOwnerId();
        if (authorId != ownerId) {
            throw new DataValidationException("Only project owner can create new vacancy");
        }
    }
}
