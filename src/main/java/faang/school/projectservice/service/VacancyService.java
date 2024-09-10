package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.VacancyFilter;
import faang.school.projectservice.filter.VacancyFilterDto;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.VacancyValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final VacancyValidator vacancyValidator;
    private final List<VacancyFilter> filters;
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectRepository projectRepository;
    private final CandidateRepository candidateRepository;


    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        vacancyValidator.validateVacancyByName(vacancyDto);
        vacancyValidator.validateVacancyByDescription(vacancyDto);
        vacancyValidator.validateVacancyByCount(vacancyDto);

        if (checkInvalidSupervisorRole(vacancyDto.getCreated_by())) {
            throw new DataValidationException("Only owner or manager can create vacancies");
        }
        if (!projectRepository.existsById(vacancyDto.getProjectId())) {
            throw new DataValidationException("Project does not exist");
        }

        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
        return vacancyMapper.toDto(vacancyRepository.save(vacancy));
    }

    private boolean checkInvalidSupervisorRole(Long supervisorId) {
        TeamMember supervisor = teamMemberRepository.findById(supervisorId);
        return !(supervisor.getRoles().contains(TeamRole.OWNER) || supervisor.getRoles().contains(TeamRole.MANAGER));
    }

    public VacancyDto updateVacancy(Long vacancyId, VacancyDto vacancyDto) {
        vacancyValidator.validateVacancyByName(vacancyDto);
        vacancyValidator.validateVacancyByDescription(vacancyDto);
        vacancyValidator.validateVacancyByCount(vacancyDto);

        if (checkInvalidSupervisorRole(vacancyDto.getUpdated_by())) {
            throw new DataValidationException("Only owner or manager can update vacancies");
        }

        Vacancy vacancyToUpdate = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new DataValidationException("Vacancy does not exist"));

        if (isToBeClosed(vacancyToUpdate, vacancyDto)) {
            vacancyToUpdate.setStatus(vacancyDto.getStatus());
        }

        updatingFields(vacancyToUpdate, vacancyDto);

        vacancyToUpdate.setUpdatedAt(LocalDateTime.now());
        return vacancyMapper.toDto(vacancyRepository.save(vacancyToUpdate));
    }

    private boolean isToBeClosed(Vacancy vacancyToUpdate, VacancyDto vacancyDto) {
        return !vacancyToUpdate.getStatus().equals(VacancyStatus.CLOSED)
                && vacancyDto.getStatus().equals(VacancyStatus.CLOSED)
                && getAcceptedCount(vacancyToUpdate) > vacancyToUpdate.getCount();
    }

    private long getAcceptedCount(Vacancy vacancy) {
        return vacancy.getCandidates().stream()
                .filter(candidate -> candidate.getCandidateStatus().equals(CandidateStatus.ACCEPTED))
                .count();
    }

    private void updatingFields(Vacancy vacancyToUpdate, VacancyDto vacancyDto) {
        vacancyToUpdate.setName(vacancyDto.getName());
        vacancyToUpdate.setDescription(vacancyDto.getDescription());
        if (vacancyDto.getStatus() != null) {
            vacancyToUpdate.setStatus(vacancyDto.getStatus());
        }
        if (vacancyDto.getSalary() != null) {
            vacancyToUpdate.setSalary(vacancyDto.getSalary());
        }
        if (vacancyDto.getWorkSchedule() != null) {
            vacancyToUpdate.setWorkSchedule(vacancyDto.getWorkSchedule());
        }
    }

    public void deleteVacancy(Long vacancyId) {
        if (vacancyRepository.existsById(vacancyId)) {
            Vacancy vacancyToDelete = vacancyRepository.getReferenceById(vacancyId);
            vacancyToDelete.getCandidates().stream()
                    .filter(candidate -> candidate.getCandidateStatus().equals(CandidateStatus.ACCEPTED))
                    .forEach(candidate -> candidateRepository.deleteById(candidate.getId()));
            vacancyRepository.deleteById(vacancyId);
        }
    }

    public List<VacancyDto> getVacancies(VacancyFilterDto vacancyFilterDto) {
        Stream<Vacancy> vacancies = vacancyRepository.findAll().stream();
        return filters.stream()
                .filter(filter -> filter.isApplicable(vacancyFilterDto))
                .flatMap(filter -> filter.apply(vacancyFilterDto, vacancies))
                .map(vacancyMapper::toDto)
                .toList();
    }

    public VacancyDto getVacancyById(Long vacancyId) {
        return vacancyMapper.toDto(vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new EntityNotFoundException("Vacancy does not exist")));
    }
}
