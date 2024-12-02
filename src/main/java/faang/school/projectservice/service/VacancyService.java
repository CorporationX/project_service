package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.vacancy.UpdateDeleteVacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final CandidateService candidateService;
    private final TeamMemberService teamMemberService;
    private final ProjectService projectService;
    private final VacancyMapper vacancyMapper;

    public CreateVacancyDto createVacancy(CreateVacancyDto createVacancyDto) {
        long curatorId = createVacancyDto.createdBy();
        ensureCuratorHasAccess(curatorId);

        Vacancy vacancy = vacancyMapper.toEntity(createVacancyDto);
        Project vacancyProject = projectService.findProjectById(createVacancyDto.projectId());
        vacancy.setProject(vacancyProject);
        if (createVacancyDto.candidatesIds() != null) {
            List<Candidate> candidates = candidateService.findCandidates(createVacancyDto.candidatesIds());
            candidateService.updateCandidatesWithVacancy(candidates, vacancy);
            candidates.forEach(vacancy::addCandidate);
        } else {
            vacancy.setCandidates(new ArrayList<>());
        }
        vacancy.setStatus(VacancyStatus.OPEN);
        vacancy.setCreatedAt(LocalDateTime.now());
        vacancyRepository.save(vacancy);

        log.info("Vacancy with ID {} created successfully", vacancy.getId());
        return vacancyMapper.toCreateDto(vacancy);
    }

    public UpdateDeleteVacancyDto updateVacancy(UpdateDeleteVacancyDto vacancyDto) {
        long vacancyId = vacancyDto.id();
        long curatorId = vacancyDto.createdBy();
        ensureCuratorHasAccess(curatorId);

        Vacancy vacancy = findVacancyById(vacancyId);

        if (vacancyDto.name() != null) {
            vacancy.setName(vacancyDto.name());
        }
        if (vacancyDto.description() != null) {
            vacancy.setDescription(vacancyDto.description());
        }
        if (vacancyDto.candidatesIds() != null) {
            List<Candidate> candidates = candidateService.findCandidates(vacancyDto.candidatesIds());
            candidateService.updateCandidatesWithVacancy(candidates, vacancy);
            candidates.forEach(vacancy::addCandidate);
        }
        if (vacancyDto.status() != null) {
            vacancy.setStatus(vacancyDto.status());
        }
        if (vacancyDto.count() != null) {
            vacancy.setCount(vacancyDto.count());
        }

        vacancy.setUpdatedAt(LocalDateTime.now());
        vacancyRepository.save(vacancy);

        log.info("Vacancy with ID {} updated successfully", vacancyId);
        return vacancyMapper.toUpdateDeleteDto(vacancy);
    }

    public void deleteVacancy(UpdateDeleteVacancyDto vacancyDto) {
        long vacancyId = vacancyDto.id();
        Vacancy vacancy = findVacancyById(vacancyId);
        List<Candidate> candidates = vacancy.getCandidates();
        if (candidates != null) {
            for (Candidate candidate : candidates) {
                candidateService.deleteCandidate(candidate.getId());
            }
        }
        vacancyRepository.deleteById(vacancyId);
        log.info("Vacancy with ID {} deleted successfully", vacancyId);
    }

    public List<UpdateDeleteVacancyDto> getFilteredVacancies(String name, String position) {
        List<Vacancy> vacancies = vacancyRepository.findAll();
        List<Vacancy> filteredVacancies = vacancies.stream()
                .filter(vacancy -> vacancy.containsName(name) && vacancy.containsPosition(position))
                .toList();
        return vacancyMapper.toUpdateDeleteDto(filteredVacancies);
    }

    public UpdateDeleteVacancyDto getVacancy(long vacancyId) {
        Vacancy vacancy = findVacancyById(vacancyId);
        return vacancyMapper.toUpdateDeleteDto(vacancy);
    }

    private Vacancy findVacancyById(long vacancyId) {
        return vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new DataValidationException("Vacancy not found"));
    }

    private void ensureCuratorHasAccess(long curatorId) {
        if (teamMemberService.curatorHasNoAccess(curatorId)) {
            log.error("Curator with ID {} does not have access to create a vacancy", curatorId);
            throw new DataValidationException("Curator does not have access to create a vacancy");
        }
    }
}
