package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.exception.vacancy.DataValidationException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.filter.vacancy.VacancyFilter;
import faang.school.projectservice.validator.vacancy.VacancyServiceValidator;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Log
@Service
@Data
@RequiredArgsConstructor
public class VacancyService {
    private VacancyMapper vacancyMapper;
    private VacancyRepository vacancyRepository;
    private ProjectRepository projectRepository;
    private CandidateRepository candidateRepository;
    private TeamMemberJpaRepository teamMemberJpaRepository;
    private VacancyServiceValidator vacancyServiceValidator;
    private List<VacancyFilter> vacancyFilters;
    private UserContext userContext;

    public List<VacancyDto> getVacancyIdsByFilters(VacancyFilterDto filterDto) {
        Stream<Vacancy> vacanciesStream = vacancyRepository.findAll().stream();
        return vacancyFilters.stream()
                .filter(vacancyFilter -> vacancyFilter.isApplicable(filterDto))
                .flatMap(vacancyFilterActual -> vacancyFilterActual.apply(vacanciesStream, filterDto))
                .map(vacancyMapper::vacancyToVacancyDto)
                .distinct()
                .toList();
    }

    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        Long userId = userContext.getUserId();
        Vacancy vacancy = vacancyMapper.vacancyDtoToVacancy(vacancyDto);
        List<TeamMember> teamMembers = teamMemberJpaRepository.findByUserId(userId);
        List<Project> listOfProjectOwners = projectRepository.findAll();
        if (!vacancyServiceValidator.verificationOfCurator(userId, teamMembers, listOfProjectOwners)) {
                throw new DataValidationException("Такой куратор не найден");
        }
        vacancy.setProject(projectRepository.getProjectById(vacancyDto.getProjectId()));
        vacancy.setStatus(VacancyStatus.OPEN);
        vacancy.setCreatedBy(userId);
        vacancy.setCandidates(getCandidatesByIds(vacancyDto.getCandidateIds()));
        Vacancy actualVacancy = vacancyRepository.save(vacancy);
        VacancyDto dto = vacancyMapper.vacancyToVacancyDto(actualVacancy);
        dto.setCandidateIds(getCandidateIds(actualVacancy));
        return dto;
    }

    public VacancyDto updateVacancy(Long id, VacancyDto vacancyDto) {
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Такой задачи не существует"));;
        vacancy.setCandidates(getCandidatesByIds(vacancyDto.getCandidateIds()));
        vacancy.setUpdatedBy(userContext.getUserId());
         vacancyRepository.save(vacancy);
        VacancyDto resultDto = vacancyMapper.vacancyToVacancyDto(vacancy);
        resultDto.setCandidateIds(getCandidateIds(vacancy));
        return resultDto;
    }

    public VacancyDto closeVacancy(VacancyDto vacancyDto) {
        Vacancy vacancy = vacancyRepository.findById(vacancyDto.getId())
                .orElseThrow(() -> new DataValidationException("Такой задачи не существует"));;
        if (!vacancyServiceValidator.validationCompletedVacancy(vacancy)) {
            vacancy.setStatus(VacancyStatus.POSTPONED);
            vacancy = vacancyRepository.save(vacancy);
            log.info("Неудачная попытка закрыть вакансию");
            return vacancyMapper.vacancyToVacancyDto(vacancy);
        }
        deleteVacancy(vacancy);
        vacancy.setStatus(VacancyStatus.CLOSED);
        log.info("Вакансия закрыта");
        return vacancyMapper.vacancyToVacancyDto(vacancy);
    }

    private void deleteVacancy(Vacancy vacancy) {
        List<Long> candidateIds = vacancy.getCandidates().stream()
                .filter(candidate -> candidate.getVacancy().getId().equals(vacancy.getId()))
                .filter(candidate -> !candidate.getCandidateStatus().equals(CandidateStatus.ACCEPTED))
                .map(Candidate::getId)
                .toList();
        candidateRepository.deleteAllById(candidateIds);
        vacancyRepository.deleteById(vacancy.getId());
    }

    private List<Long> getCandidateIds(Vacancy vacancy) {
        return vacancy.getCandidates().stream()
                .map(Candidate::getId).toList();
    }

    private List<Candidate> getCandidatesByIds(List<Long> candidateIds) {
        return candidateRepository.findAllById(candidateIds);
    }
}
