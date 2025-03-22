package faang.school.projectservice.validator.vacancy;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.vacancy.VacancyCreateDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidatorVacancy {
    TeamRole teamRole;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final VacancyRepository vacancyRepository;

    public boolean validatorProjectAvailability(Long projectId) {
        Optional<Project> projectFor = projectRepository.findById(projectId);
        if (projectFor.isPresent()) {
            Project project = projectFor.get();
            if (project.getId() == 2) {
                return true;
            }
        }
        return false;
    }

    public boolean validatorTeamRole(String teamRoleDto) {
        try {
            return teamRole.getAll().stream().anyMatch(teamRole -> teamRole.toString().equals(teamRoleDto));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("не указана позиция, на которую открыта вакансия");
        }
    }

    public boolean checkRoleCurator(VacancyCreateDto vacancyCreateDto) {
        Long projectId = vacancyCreateDto.getProjectId();
        Optional<Project> projectFind = projectRepository.findById(projectId);

        return projectFind.map(project -> {
                    Long ownerId = project.getOwnerId();
                    if (ownerId == null) {
                        throw new ValidationException("Project owner is not set");
                    }
                    TeamMember owner = teamMemberRepository.findByUserIdAndProjectId(ownerId, projectId);
                    if (owner == null) {
                        return false;
                    }
                    return owner.getRoles().stream()
                            .anyMatch(role -> role == TeamRole.OWNER || role == TeamRole.MANAGER);
                }
        ).orElse(false);
    }

    public boolean checkRoleUpdatingUser(Long id) {
        TeamMember updatingUser = teamMemberRepository.findByUserId(id).stream()
                .findFirst().orElseThrow(() -> new DataValidationException("Мы не нашли Вас в списки пользователей, которые имеют право на обновление вакансии"));
        try {
            return updatingUser.getRoles().stream()
                    .anyMatch(role -> role == TeamRole.OWNER || role == TeamRole.MANAGER);
        } catch (Exception e) {
            throw new DataValidationException("У Вас нет прав для обновления вакансии. Необходимо иметь роль OWNER или MANAGER");
        }
    }

    public boolean checkingNumberCandidates(VacancyUpdateDto dto) {
        if (dto.getStatus().equals(VacancyStatus.CLOSED.toString())) {
            Vacancy vacancy = vacancyRepository.getById(dto.getId());
            if (vacancy.getCount() == dto.getCount()) {
                return vacancy.getCandidates().stream().allMatch(role -> {
                    TeamMember teamMember = teamMemberRepository
                            .findByUserIdAndProjectId(role.getUserId(), dto.getProjectId());
                    return dto.getPosition().equals(teamMember.toString());
                });
            } else {
                throw new DataValidationException("Нельзя закрыть вакансию, пока не набраны необходимые сотрудники");
            }
        } else {
            return true;
        }
    }
}
