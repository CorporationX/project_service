package faang.school.projectservice.service;

import faang.school.projectservice.exeption.EntityNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.VacancyRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VacancyService {

    private final VacancyRepository vacancyRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public VacancyService(VacancyRepository vacancyRepository, ProjectRepository projectRepository) {
        this.vacancyRepository = vacancyRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional
    public Vacancy createVacancy(VacancyCreateRequest request, Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        // Проверка роли пользователя
        if (!isValidRoleForVacancy(userId, project)) {
            throw new UnauthorizedActionException("You do not have permission to create vacancies");
        }

        Vacancy vacancy = new Vacancy();
        vacancy.setPosition(request.position());
        vacancy.setAvailableSlots(request.availableSlots());
        vacancy.setProject(project);

        return vacancyRepository.save(vacancy);
    }

    @Transactional
    public Vacancy updateVacancy(Long vacancyId, VacancyUpdateRequest request, Long userId) {
        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new EntityNotFoundException("Vacancy not found"));

        Project project = vacancy.getProject();

        // Проверка роли пользователя
        if (!isValidRoleForVacancy(userId, project)) {
            throw new UnauthorizedActionException("You do not have permission to update this vacancy");
        }

        vacancy.setPosition(request.position());
        vacancy.setAvailableSlots(request.availableSlots());

        return vacancyRepository.save(vacancy);
    }

    @Transactional
    public void deleteVacancy(Long vacancyId) {
        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new EntityNotFoundException("Vacancy not found"));

        vacancyRepository.delete(vacancy);
    }

    public List<Vacancy> getVacancies(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        return project.getVacancies();
    }

    private boolean isValidRoleForVacancy(Long userId, Project project) {
        // Здесь будет логика проверки роли пользователя (OWNER или MANAGER)
        return project.getOwnerId().equals(userId);
    }
}
