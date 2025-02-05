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
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.VacancyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final ProjectRepository projectRepository;
    private final CandidateRepository candidateRepository;
    private final List<VacancyFilter> vacancyFilters;

    private final VacancyMapper vacancyMapper;

    private final VacancyValidator vacancyValidator;

    public CreateVacancyResponse create(CreateVacancyRequest createRequest) {
        Vacancy vacancy = vacancyMapper.fromCreateRequest(createRequest);
        vacancy.setProject(projectRepository.getReferenceById(createRequest.getProjectId()));
        vacancyValidator.validateCreatedVacancy(vacancy);

        vacancy.setStatus(VacancyStatus.OPEN);

        Vacancy createdVacancy = vacancyRepository.save(vacancy);

        return vacancyMapper.toCreateResponse(createdVacancy);
    }

    public UpdateVacancyResponse update(UpdateVacancyRequest updateRequest) {
        Vacancy vacancy = vacancyMapper.fromUpdateRequest(updateRequest);
        vacancy.setProject(projectRepository.getReferenceById(updateRequest.getProjectId()));
        vacancy.setCandidates(candidateRepository.findAllById(updateRequest.getCandidateIds()));
        vacancyValidator.validateUpdatedVacancy(vacancy);

        Vacancy updatedVacancy = vacancyRepository.save(vacancy);

        return vacancyMapper.toUpdateResponse(updatedVacancy);
    }

    public void delete(long id) {
        Optional<Vacancy> vacancyOptional = vacancyRepository.findById(id);
        if (vacancyOptional.isPresent()) {
            List<Long> candidateIds = vacancyOptional.get()
                    .getCandidates()
                    .stream()
                    .map(Candidate::getId)
                    .toList();

            candidateRepository.deleteAllById(candidateIds);
            vacancyRepository.deleteById(id);
        } else {
            throw new VacancyValidationException("There is no vacancy for this ID");
        }
    }

    public GetVacancyResponse getVacancyById(long id) {
        Optional<Vacancy> vacancyOptional = vacancyRepository.findById(id);
        if (vacancyOptional.isPresent()) {
            return vacancyMapper.toGetResponse(vacancyOptional.get());
        } else {
            throw new VacancyValidationException("There is no vacancy for this ID");
        }
    }

    public List<GetVacancyResponse> getAllVacancies(VacancyFilterDto filters) {
        Stream<Vacancy> vacancies = vacancyRepository.findAll().stream();
        vacancyFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(vacancies, filters));

        return vacancies.map(vacancyMapper::toGetResponse).toList();
    }
}
