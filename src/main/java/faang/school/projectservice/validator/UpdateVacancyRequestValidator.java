package faang.school.projectservice.validator;

import faang.school.projectservice.dto.vacancy.UpdateVacancyRequestDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UpdateVacancyRequestValidator {
    private final VacancyRepository vacancyRepository;
    private final TeamMemberService teamMemberService;

    public Vacancy validateAndGetVacancy(UpdateVacancyRequestDto requestDto) {
        return vacancyRepository.findById(requestDto.vacancyId())
                .orElseThrow(() -> new DataValidationException(
                        "Vacancy with id '%d' is not found".formatted(requestDto.vacancyId())));
    }

    public void validateCandidatesCount(
            UpdateVacancyRequestDto requestDto,
            Vacancy vacancy,
            List<Candidate> attachedToProjectCandidates) {
        var currentStatus = vacancy.getStatus();
        if (requestDto.status() != null && !requestDto.status().equals(currentStatus)) {
            switch (requestDto.status()) {
                case OPEN -> throw new DataValidationException("It is not possible to open vacancy by this endpoint");
                case CLOSED -> {
                    var requiredCandidatesCount = requestDto.requiredCandidatesCount();
                    if (requiredCandidatesCount == null) {
                        requiredCandidatesCount = vacancy.getCount();
                    }
                    if (requiredCandidatesCount == null) {
                        throw new DataValidationException("Set required candidates count before close vacancy");
                    }

                    if (attachedToProjectCandidates.size() != requiredCandidatesCount) {
                        throw new DataValidationException(
                                ("Current attached to project '%s' users count (%d) "
                                        + "must be the same as required members count %d for vacancy %d").formatted(
                                        vacancy.getProject().getName(),
                                        attachedToProjectCandidates.size(),
                                        requiredCandidatesCount,
                                        vacancy.getId()));
                    }
                }
                case POSTPONED -> {
                    if (currentStatus == VacancyStatus.CLOSED) {
                        throw new DataValidationException("Closed vacancy cannot be postponed");
                    }
                }
            }
        }
    }

    public void validateUpdaterRole(UpdateVacancyRequestDto requestDto) {
        var updater = teamMemberService.getTeamMemberById(requestDto.teamMemberUpdaterId())
                .orElseThrow(() -> new DataValidationException(
                        "Team member with id %d is not found".formatted(requestDto.teamMemberUpdaterId())));

        var isRightRole = updater.getRoles()
                .stream()
                .anyMatch(role -> role == TeamRole.OWNER || role == TeamRole.MANAGER);
        if (!isRightRole) {
            throw new DataValidationException(
                    "Current updater roles are %s. Only OWNER and MANAGER is possible".formatted(
                            String.join(
                                    ",",
                                    updater.getRoles().stream().map(Enum::toString).toList())));
        }
    }
}
