package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.dto.client.VacancyFilterDto;
import faang.school.projectservice.filter.VacancyFilterService;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.TeamMemberValidator;
import faang.school.projectservice.validator.VacancyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final VacancyValidator vacancyValidator;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamMemberValidator teamMemberValidator;
    private final CandidateRepository candidateRepository;
    private final VacancyFilterService vacancyFilterService;

    public VacancyDto createVacancy(VacancyDto vacancy) {
        vacancyValidator.validateVacancyName(vacancy);
        vacancyValidator.validateCountOfVacancy(vacancy);
        vacancyValidator.checkExistsById(vacancy.getId());
        TeamMember teamMember = teamMemberRepository.findById(vacancy.getCreatedBy());
        teamMemberValidator.validateTeamMember(teamMember);
        Vacancy convertedVacancy = vacancyMapper.toEntity(vacancy);
        return vacancyMapper.toDto(vacancyRepository.save(convertedVacancy));
    }

    public VacancyDto closeVacancy(VacancyDto vacancy) {
        Vacancy vacancyForUpdate = vacancyRepository.getReferenceById(vacancy.getId());
        vacancyValidator.validateCountOfCandidate(vacancyForUpdate);
        vacancyForUpdate.getCandidates()
                .stream()
                .findFirst()
                .ifPresent(candidate -> candidate.setCandidateStatus(CandidateStatus.ACCEPTED));
        vacancyForUpdate.setStatus(VacancyStatus.CLOSED);
        return vacancyMapper.toDto(vacancyRepository.save(vacancyForUpdate));
    }

    public VacancyDto hireCandidate(VacancyDto vacancy, long userId, TeamRole teamRole) {
        vacancyValidator.checkExistsVacancy(vacancy);
        Vacancy vacancyForUpdate = vacancyRepository.getReferenceById(vacancy.getId());
        TeamMember candidateForHire = teamMemberRepository.findById(userId);
        candidateForHire.setRoles(List.of(teamRole));
        Candidate candidate = candidateRepository.getReferenceById(userId);
        vacancyForUpdate.setCandidates(List.of(candidate));
        teamMemberRepository.save(candidateForHire);
        return vacancyMapper.toDto(vacancyRepository.save(vacancyForUpdate));
    }

    public void deleteVacancy(VacancyDto vacancy) {
        vacancyValidator.checkExistsVacancy(vacancy);
        Vacancy vacancyForDelete = vacancyRepository.getReferenceById(vacancy.getId());
        List<Long> candidatesIds = vacancyForDelete.getCandidates().stream().map(Candidate::getId).toList();
        teamMemberRepository.deleteAllById(candidatesIds);
        vacancyRepository.deleteById(vacancy.getId());
    }

    public List<VacancyDto> getVacancyByNameAndPosition(VacancyFilterDto vacancyFilter) {
        List<Vacancy> vacancies = vacancyRepository.findAll();

        return vacancyFilterService.applyFilter(vacancies.stream(), vacancyFilter)
                .map(vacancyMapper::toDto)
                .toList();
    }

    public VacancyDto getVacancy(long id) {
        vacancyValidator.checkExistsById(id);
        return vacancyMapper.toDto(vacancyRepository.getReferenceById(id));
    }
}