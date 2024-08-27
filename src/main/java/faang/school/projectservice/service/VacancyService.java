package faang.school.projectservice.service;


import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.VacancyServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VacancyService {

    private final VacancyMapper vacancyMapper;
    private final VacancyRepository vacancyRepository;
    private final VacancyServiceValidator vacancyServiceValidator;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional
    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        vacancyServiceValidator.existByProject(vacancyDto.getProjectId());
        TeamMember ownerMember = teamMemberRepository.findById(vacancyDto.getOwnerId());
        vacancyServiceValidator.ensureTeamRoleOwner(ownerMember);

        Vacancy vacancy = vacancyMapper.toVacancy(vacancyDto);
        vacancy.setCreatedAt(LocalDateTime.now());
        vacancy.setUpdatedAt(LocalDateTime.now());
        vacancyRepository.save(vacancy);

        return vacancyMapper.toVacancyDto(vacancy);
    }

    @Transactional
    public VacancyDto updateVacancy(VacancyDto vacancyDto, long vacancyId) {
        Vacancy vacancy = findVacancyIfExist(vacancyId);
        vacancyMapper.updateVacancy(vacancyDto, vacancy);

        vacancyRepository.save(vacancy);
        return vacancyMapper.toVacancyDto(vacancy);
    }

    @Transactional
    public void closeVacancy(long vacancyId) {
        Vacancy vacancy = findVacancyIfExist(vacancyId);
        vacancyServiceValidator.checkCountCandidatesToClose(vacancy);

        vacancy.getCandidates()
                .stream()
                .peek(candidate -> candidate.setCandidateStatus(CandidateStatus.ACCEPTED))
                .map(candidate -> teamMemberRepository.findById(candidate.getId()))
                .forEach(teamMember -> teamMember.getRoles().add(vacancy.getTeamRole()));

        vacancy.setStatus(VacancyStatus.CLOSED);
        vacancyRepository.save(vacancy);
    }

    @Transactional
    public void deleteVacancy(long vacancyId) {
        Vacancy vacancy = findVacancyIfExist(vacancyId);

        List<Long> candidateDeleteId = vacancy.getCandidates()
                .stream()
                .filter(candidate -> candidate.getCandidateStatus() != CandidateStatus.ACCEPTED)
                .map(Candidate::getId)
                .toList();

        teamMemberRepository.deleteTeamMember(candidateDeleteId);

        vacancyRepository.delete(vacancy);
    }

    @Transactional
    public List<VacancyDto> getVacancyPositionAndName(VacancyFilterDto filters) {
        List<Vacancy> vacancies = vacancyRepository.findByFilter(filters.getName(), filters.getStatus());

        return vacancies.stream()
                .map(vacancyMapper::toVacancyDto)
                .toList();
    }

    @Transactional
    public VacancyDto getVacancyById(long vacancyId) {
        return vacancyMapper.toVacancyDto(findVacancyIfExist(vacancyId));
    }

    private Vacancy findVacancyIfExist(long vacancyId) {
        Optional<Vacancy> vacancy = vacancyRepository.findById(vacancyId);
        vacancyServiceValidator.existByVacancy(vacancy);
        return vacancy.get();
    }
}