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

    public void createVacancyControllerValidation(VacancyDto vacancyDto) {
        if (vacancyDto == null) {
            throw new VacancyValidationException("Your vacancy is null!");
        }

        if (vacancyDto.getProjectId() == null) {
            throw new VacancyValidationException("Illegal project!");
        }
    }

    public void createVacancyValidation(VacancyDto vacancyDto, long creator) {
        if (!projectRepository.existsById(vacancyDto.getProjectId())) {
            throw new VacancyValidationException("Non-existent project");
        }

        Project project = projectRepository.getProjectById(vacancyDto.getProjectId());
        if (!project.getOwnerId().equals(creator)) {
            throw new VacancyValidationException("Access Denied!");
        }
    }
}
