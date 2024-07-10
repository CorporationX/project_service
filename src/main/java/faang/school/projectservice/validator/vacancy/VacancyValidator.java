package faang.school.projectservice.validator.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.VacancyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class VacancyValidator {

    private final ProjectRepository projectRepository;
    private final VacancyRepository vacancyRepository;
    @Autowired
    public VacancyValidator(ProjectRepository projectRepository, VacancyRepository vacancyRepository) {
        this.projectRepository = projectRepository;
        this.vacancyRepository = vacancyRepository;
    }

    public void createVacancyValidator(VacancyDto vacancyDto) {
        Project project = projectRepository.getProjectById(vacancyDto.getProjectId());
    }
    public void updateVacancyValidator(Long id, VacancyDto vacancyDto) {
        Optional<Vacancy> optionalVacancy = vacancyRepository.findById(id);
        if(optionalVacancy.isEmpty()) {
            throw new IllegalArgumentException("Vacancy not found.");
        }


    }

}
