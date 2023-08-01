package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDtoReqUpdate;
import faang.school.projectservice.exception.vacancy.VacancyValidateException;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;

import static faang.school.projectservice.commonMessages.vacancy.ErrorMessagesForVacancy.*;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private static final int MIN_COUNT_MEMBERS = 5;
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional
    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        Project curProject = projectRepository.getProjectById(vacancyDto.getProjectId());
        checkCreatorVacancy(vacancyDto.getCreatedBy());

        Vacancy newVacancy = vacancyMapper.toEntity(vacancyDto);
        newVacancy.setProject(curProject);
        return vacancyMapper.toDto(vacancyRepository.save(newVacancy));
    }

    @Transactional
    public VacancyDto updateVacancy(Long vacancyId, VacancyDtoReqUpdate vacancyDto) {
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

    private void checkProjectIsExist(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            String errorMessage = MessageFormat.format(PROJECT_NOT_EXIST_FORMAT, projectId);
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
