package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.candidate.CandidateService;
import faang.school.projectservice.service.vacancy.filter.VacancyFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final ProjectService projectService;
    private final VacancyValidator vacancyValidator;
    private final CandidateService candidateService;
    private final List<VacancyFilter> filters;

    public Vacancy createVacancy(Vacancy vacancy, Long userId) {
        Project project = projectService.findProjectById(vacancy.getProject().getId());
        vacancy.setCreatedBy(userId);
        vacancy.setProject(project);
        vacancy.setCandidates(List.of());
        vacancy.setCreatedAt(LocalDateTime.now());
        vacancy.setUpdatedAt(null);
        vacancy.setUpdatedBy(null);
        vacancy.setStatus(VacancyStatus.OPEN);

        vacancyValidator.validateTutorRole(vacancy.getCreatedBy(), vacancy.getProject().getId());

        log.info("Vacancy created: " + vacancy);
        return vacancyRepository.save(vacancy);
    }

    public Vacancy closeVacancy(Long vacancyId, Long tutorId) {
        if (vacancyId == null || tutorId == null) {
            throw new DataValidationException("vacancyId or tutorId is null");
        }

        Vacancy vacancy = vacancyRepository.findById(vacancyId).orElseThrow(() ->
                new DataValidationException("vacancy %d not found".formatted(vacancyId)));

        vacancyValidator.validateVacancyStatus(vacancy);
        vacancyValidator.validateTutorRole(vacancy.getCreatedBy(), vacancy.getProject().getId());
        vacancyValidator.validateCandidatesCount(vacancy);

        vacancy.setStatus(VacancyStatus.CLOSED);
        vacancy.setUpdatedAt(LocalDateTime.now());
        vacancy.setUpdatedBy(tutorId);
        log.info("Vacancy close: " + vacancy);
        return vacancyRepository.save(vacancy);
    }

    public Vacancy addCandidates(List<Candidate> candidates, Long vacancyId, Long tutorId) {
        if (candidates == null || candidates.isEmpty() || vacancyId == null || tutorId == null) {
            throw new DataValidationException("candidates, tutorId or vacancyId is null");
        }

        Vacancy vacancy = vacancyRepository.findById(vacancyId).orElseThrow(() ->
                new DataValidationException("vacancy %d not found".formatted(vacancyId)));

        vacancyValidator.validateTutorRole(tutorId, vacancy.getProject().getId());
        vacancyValidator.validateCandidates(vacancy, candidates);

        vacancy.setCandidates(Stream.concat(vacancy.getCandidates().stream(), candidates.stream())
                .toList());
        vacancy.setUpdatedAt(LocalDateTime.now());
        vacancy.setUpdatedBy(tutorId);

        return vacancyRepository.save(vacancy);
    }

    public Vacancy updateVacancy(Long vacancyId, Vacancy newVacancy, Long tutorId) {
        if (vacancyId == null || newVacancy == null || tutorId == null) {
            throw new DataValidationException("vacancyId, newVacancy or tutorId is null");
        }

        Vacancy sourceVacancy = vacancyRepository.findById(vacancyId).orElseThrow(() ->
                new DataValidationException("vacancy %d not found".formatted(vacancyId)));

        vacancyValidator.validateVacancyStatus(sourceVacancy);
        newVacancy.setId(vacancyId);
        newVacancy.setUpdatedAt(LocalDateTime.now());
        newVacancy.setUpdatedBy(tutorId);

        vacancyValidator.validateTutorRole(tutorId, newVacancy.getProject().getId());

        return vacancyRepository.save(newVacancy);
    }

    public void deleteVacancy(Long vacancyId, Long tutorId) {
        if (vacancyId == null || tutorId == null) {
            throw new DataValidationException("vacancyId or tutorId is null");
        }

        Vacancy vacancy = vacancyRepository.findById(vacancyId).orElseThrow(() ->
                new DataValidationException("vacancy %d not found".formatted(vacancyId)));

        vacancyValidator.validateTutorRole(tutorId, vacancy.getProject().getId());
        candidateService.deleteCandidatesByVacancyId(vacancyId);
        vacancyRepository.deleteById(vacancyId);
    }

    public Vacancy getVacancy(Long vacancyId) {
        if (vacancyId == null) {
            throw new DataValidationException("vacancyId is null");
        }

        return vacancyRepository.findById(vacancyId).orElseThrow(() ->
                new DataValidationException("vacancy %s not found".formatted(vacancyId)));
    }

    public List<Vacancy> getVacancies(VacancyFilterDto filterDto) {
        Stream<Vacancy> allVacancies = vacancyRepository.findAll().stream();

        if (filterDto == null) {
            return allVacancies.toList();
        }

        return filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(allVacancies,
                        (stream, filter) -> filter.apply(stream, filterDto),
                        (s1, s2) -> s1)
                .toList();
    }
}
