package faang.school.projectservice.validator.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.vacancy.VacancyValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VacancyValidator {
    private final ProjectRepository projectRepository;

    public void createVacancyControllerValidation(VacancyDto vacancyDto, long creator) {
        if (vacancyDto == null) {
            throw new VacancyValidationException("your vacancy is null!");
        }

        if (vacancyDto.getProjectId() == null || vacancyDto.getProjectId() < 1) {
            throw new VacancyValidationException("Illegal project!");
        }

        if (creator < 1) {
            throw new VacancyValidationException("IWrong creator!");
        }
    }

    public void createVacancyValidation(VacancyDto vacancyDto, long creator) {
        Project project = projectRepository.getProjectById(vacancyDto.getProjectId());
        if (!project.getOwnerId().equals(creator)) {
            throw new VacancyValidationException("Access Denied!");
        }
    }
}
