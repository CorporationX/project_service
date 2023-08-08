package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDtoGetReq;
import faang.school.projectservice.dto.vacancy.VacancyDtoUpdateReq;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.exception.vacancy.VacancyValidateException;
import faang.school.projectservice.filters.vacancy.VacancyFilter;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static faang.school.projectservice.commonMessages.vacancy.ErrorMessagesForVacancy.*;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private static final int MIN_COUNT_MEMBERS = 5;
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final List<VacancyFilter> vacancyFilters;

    @Transactional
    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        Project curProject = projectRepository.getProjectById(vacancyDto.getProjectId());
        checkCreatorVacancy(vacancyDto.getCreatedBy());

        Vacancy newVacancy = vacancyMapper.toEntity(vacancyDto);
        newVacancy.setProject(curProject);
        return vacancyMapper.toDto(vacancyRepository.save(newVacancy));
    }

    @Transactional
    public VacancyDto updateVacancy(Long vacancyId, VacancyDtoUpdateReq vacancyDto) {
        Vacancy vacancyForUpdate = getVacancyById(vacancyId);

        checkRoleUserForPossibilityUpdateVacancy(vacancyDto.getUpdatedBy());

        // проверка возможности закрытия вакансии
        if (vacancyDto.getStatus().equals(VacancyStatus.CLOSED)) {
            checkPossibilityCloseVacancy(vacancyForUpdate);
        }

        // готовы к обновлению
        vacancyMapper.updateEntityFromDto(vacancyDto, vacancyForUpdate);
        vacancyForUpdate.setUpdatedAt(LocalDateTime.now());

        return vacancyMapper.toDto(vacancyRepository.save(vacancyForUpdate));
    }

    @Transactional
    public void deleteVacancy(Long vacancyId) {
        Vacancy vacancy = getVacancyById(vacancyId);
        Long projectId = vacancy.getProject().getId();
        List<Long> usersID = vacancy.getCandidates().stream().map(Candidate::getUserId).toList();
        usersID.forEach(candidateId -> deleteCandidateFromTeamMember(candidateId, projectId));
        vacancyRepository.delete(vacancy);
    }

    @Transactional
    public VacancyDtoGetReq getVacancy(Long vacancyId) {
        Vacancy vacancy = getVacancyById(vacancyId);
        return vacancyMapper.toDtoGetReq(vacancy);
    }

    public List<VacancyDto> getVacanciesByFilter(VacancyFilterDto filterDto) {
        Stream<Vacancy> vacancies = vacancyRepository.findAll().stream();

        List<VacancyFilter> listApplicableFilters =
                vacancyFilters.stream().filter(curFilter -> curFilter.isApplicable(filterDto)).toList();

        for (VacancyFilter curFilter : listApplicableFilters) {
            vacancies = curFilter.apply(vacancies, filterDto);
        }

        return vacancies.map(vacancyMapper::toDto).toList();
    }

    private void deleteCandidateFromTeamMember(Long userId, Long projectId) {
        TeamMember teamMember = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);
        if (teamMember.getRoles().stream().anyMatch(role -> role.equals(TeamRole.INTERN))) {
            teamMemberRepository.deleteEntity(teamMember);
        }
    }

    private Vacancy getVacancyById(Long vacancyId) {
        return vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new VacancyValidateException(
                        MessageFormat.format(VACANCY_NOT_EXIST_FORMAT, vacancyId)));
    }

    private void checkPossibilityCloseVacancy(Vacancy vacancy) {
        String errorMessage = MessageFormat.format(VACANCY_CANT_BE_CLOSED_FORMAT,
                vacancy.getId(), MIN_COUNT_MEMBERS);

        if (vacancy.getCandidates().size() < MIN_COUNT_MEMBERS) {
            throw new VacancyValidateException(errorMessage);
        }
    }

    private void checkCreatorVacancy(Long creatorId) {
        TeamMember owner = teamMemberRepository.findById(creatorId);
        if (!owner.getRoles().contains(TeamRole.OWNER)) {
            String errorMessage = MessageFormat.format(ERROR_OWNER_ROLE_FORMAT, creatorId);
            throw new VacancyValidateException(errorMessage);
        }
    }

    private void checkRoleUserForPossibilityUpdateVacancy(Long updatedBy) {
        TeamMember teamMember = teamMemberRepository.findById(updatedBy);
        List<TeamRole> roles = teamMember.getRoles();
        if (!roles.contains(TeamRole.OWNER) && !roles.contains(TeamRole.MANAGER)) {
            String errorMessage = MessageFormat.format(VACANCY_CANT_BE_CHANGED_FORMAT, roles);
            throw new VacancyValidateException(errorMessage);
        }
    }
}
