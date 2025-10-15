package faang.school.projectservice.validation.vacancy;

import faang.school.projectservice.dto.vacancy.UpdateVacancyDto;
import faang.school.projectservice.exception.VacancyValidationException;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class VacancyValidatorImpl implements VacancyValidator {
    final TeamMemberRepository teamMemberRepository;

    @Override
    public void validateCreate(long userId, long projectId) {
        if (!isAllowedRole(userId, projectId)) {
            throw new VacancyValidationException("User has incorrect role for this action (create vacancy)");
        }
    }

    @Override
    public void validateUpdate(long userId, Vacancy existingVacancy, UpdateVacancyDto updateVacancyDto) {
        if (!isAllowedRole(userId, updateVacancyDto.projectId())) {
            throw new VacancyValidationException("User has incorrect role for this action (update vacancy)");
        }
        VacancyStatus newVacancyStatus = VacancyStatus.valueOf(updateVacancyDto.vacancyStatus());
        if (newVacancyStatus != VacancyStatus.CLOSED) {
            return;
        }
        if (existingVacancy.getCandidates().isEmpty()) {
            throw new VacancyValidationException("Can't close vacancy without any candidates");
        }
        if (existingVacancy.getCandidates().size() < existingVacancy.getCount()) {
            throw new VacancyValidationException("Not found enough candidates for the vacancy");
        }
        if (!isEnoughCandidatesAssigned(existingVacancy)) {
            throw new VacancyValidationException("Not found enough assigned candidates for the vacancy");
        }
    }

    private boolean isAllowedRole(long userId, long projectId) {
        TeamMember teamMember = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);
        if (teamMember == null) {
            return false;
        }
        List<TeamRole> allowedRoles = List.of(TeamRole.OWNER, TeamRole.MANAGER);
        return teamMember.getRoles().stream()
                .anyMatch(allowedRoles::contains);
    }

    private boolean isEnoughCandidatesAssigned(Vacancy vacancy) {
        long projectId = vacancy.getProject().getId();
        List<Long> gotJobCandidates = vacancy.getCandidates().stream()
                .map(Candidate::getUserId)
                .filter(userId -> {
                    TeamRole teamRoleForCheck = TeamRole.valueOf(vacancy.getPosition().toString());
                    TeamMember teamMember = teamMemberRepository.findByUserIdAndProjectId(
                            userId,
                            projectId);
                    return teamMember.getRoles().contains(teamRoleForCheck);
                })
                .toList();
        return gotJobCandidates.size() >= vacancy.getCount();
    }

}

