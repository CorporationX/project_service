package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.CreateVacancyRequest;
import faang.school.projectservice.dto.vacancy.CreateVacancyResponse;
import faang.school.projectservice.dto.vacancy.GetVacancyResponse;
import faang.school.projectservice.dto.vacancy.UpdateVacancyRequest;
import faang.school.projectservice.dto.vacancy.UpdateVacancyResponse;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.exception.VacancyValidationException;
import faang.school.projectservice.filter.vacancy.VacancyFilter;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.VacancyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final ProjectRepository projectRepository;
    private final List<VacancyFilter> vacancyFilters;
    private final VacancyMapper vacancyMapper;
    private final VacancyValidator vacancyValidator;

    public CreateVacancyResponse create(CreateVacancyRequest createRequest) {
        Vacancy vacancy = vacancyMapper.fromCreateRequest(createRequest);

        vacancy.setProject(projectRepository.findById(createRequest.getProjectId())
                .orElseThrow(() -> new VacancyValidationException("Project with ID " + createRequest.getProjectId() +
                        " not found")));

        vacancyValidator.validateCreatingVacancy(vacancy);

        vacancy.setStatus(VacancyStatus.OPEN);
        Vacancy createdVacancy = vacancyRepository.save(vacancy);

        return vacancyMapper.toCreateResponse(createdVacancy);
    }

    public UpdateVacancyResponse update(UpdateVacancyRequest updateRequest) {
        Vacancy vacancy = vacancyRepository.findById(updateRequest.getId())
                .orElseThrow(() -> new VacancyValidationException("Vacancy with ID " + updateRequest.getId() +
                        " not found"));

        vacancyMapper.update(updateRequest, vacancy);

        vacancyValidator.validateUpdatingVacancy(vacancy);

        Vacancy updatedVacancy = vacancyRepository.save(vacancy);

        return vacancyMapper.toUpdateResponse(updatedVacancy);
    }

    public void delete(long id) {
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new VacancyValidationException("Vacancy with ID " + id + " not found"));
        vacancyRepository.deleteById(id);
    }

    public GetVacancyResponse getById(long id) {
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new VacancyValidationException("Vacancy with ID " + id + " not found"));
        return vacancyMapper.toGetResponse(vacancy);
    }

    public List<GetVacancyResponse> get(VacancyFilterDto filters) {
        Stream<Vacancy> vacancies = vacancyRepository.findAll().stream();

        for (VacancyFilter vacancyFilter : vacancyFilters) {
            if (vacancyFilter.isApplicable(filters)) {
                vacancies = vacancyFilter.apply(vacancies, filters);
            }
        }

        return vacancies.map(vacancyMapper::toGetResponse).toList();
    }
}
