package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.CreateVacancyRequest;
import faang.school.projectservice.dto.vacancy.CreateVacancyResponse;
import faang.school.projectservice.dto.vacancy.GetVacancyResponse;
import faang.school.projectservice.dto.vacancy.UpdateVacancyRequest;
import faang.school.projectservice.dto.vacancy.UpdateVacancyResponse;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.VacancyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final ProjectRepository projectRepository;
    private final VacancyMapper vacancyMapper;
    private final VacancyValidator vacancyValidator;

    public CreateVacancyResponse create(CreateVacancyRequest createRequest, long userId) {
        Vacancy vacancy = vacancyMapper.fromCreateRequest(createRequest);

        Project project = projectRepository.findById(createRequest.getProjectId()).orElseThrow(
                () -> new DataValidationException("Project with ID " + createRequest.getProjectId() + " not found"));

        vacancy.setProject(project);
        vacancy.setCreatedBy(userId);

        vacancyValidator.validateCreatingVacancy(vacancy);

        vacancy.setStatus(VacancyStatus.OPEN);
        Vacancy createdVacancy = vacancyRepository.save(vacancy);

        return vacancyMapper.toCreateResponse(createdVacancy);
    }

    public UpdateVacancyResponse update(UpdateVacancyRequest updateRequest, long userId) {
        Vacancy vacancy = getVacancyFromRepository(updateRequest.getId());

        vacancyMapper.update(updateRequest, vacancy);

        vacancy.setUpdatedBy(userId);

        vacancyValidator.validateUpdatingVacancy(vacancy);

        Vacancy updatedVacancy = vacancyRepository.save(vacancy);

        return vacancyMapper.toUpdateResponse(updatedVacancy);
    }

    public void delete(long id) {
        Vacancy vacancy = getVacancyFromRepository(id);
        vacancyRepository.deleteById(id);
    }

    public GetVacancyResponse getById(long id) {
        Vacancy vacancy = getVacancyFromRepository(id);
        return vacancyMapper.toGetResponse(vacancy);
    }

    public List<GetVacancyResponse> getByFilters(VacancyFilterDto filters) {
        String name = filters.getNamePattern();
        TeamRole positionPattern = filters.getPositionPattern();

        var vacancies = vacancyRepository.findAllByFilters(name, positionPattern);

        return vacancies.stream()
                .map(vacancyMapper::toGetResponse)
                .toList();
    }

    private Vacancy getVacancyFromRepository(long id) {
        return vacancyRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Vacancy with ID " + id + " not found"));
    }
}
