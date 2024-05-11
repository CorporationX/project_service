package faang.school.projectservice.validator;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class VacancyValidator {

    private final ProjectJpaRepository projectJpaRepository;
    private final UserServiceClient userServiceClient;
    private final TeamMemberJpaRepository teamMemberJpaRepository;

    public void validateVacancyDto(VacancyDto vacancyDto) {
        if (vacancyDto.getName().isBlank()) {
            throw new DataValidationException("Vacancy name can't be empty");
        }
        if (vacancyDto.getDescription().isBlank()) {
            throw new DataValidationException("Vacancy description can't be empty");
        }
        if (vacancyDto.getStatus() == null) {
            throw new DataValidationException("Vacancy status can't be null");
        }
        if (vacancyDto.getProjectId() == null || vacancyDto.getProjectId() <= 0) {
            throw new DataValidationException("Project Id in vacancy must be set and can't be 0");
        }
    }

    public void validateCreateVacancyStatus(VacancyDto vacancyDto) {
        log.info("Validate create vacancy status");
        if (!vacancyDto.getStatus().equals(VacancyStatus.OPEN)) {
            throw new DataValidationException("New vacancy must be with status OPEN");
        }
    }

    public void validateVacancyId(Long id) {
        if (id == null || id <= 0) {
            throw new DataValidationException("Vacancy id can't be null and must be more then 0");
        }
    }

    public void validateUpdateVacancyStatus(VacancyStatus status, Vacancy vacancy) {
        log.info("Check that vacancy has enough candidates to close");
        if (status.equals(VacancyStatus.CLOSED) && vacancy.getCandidates().size() < vacancy.getCount()) {
            throw new DataValidationException("Vacancy can't be CLOSE, if not enough candidates");
        }
    }

    public void validateProject(VacancyDto vacancyDto, TeamMember curator) {
        log.info("Validate project");
        Project project = projectJpaRepository.findById(vacancyDto.getProjectId()).orElseThrow(() ->
                new EntityNotFoundException(String.format("Project doesn't exist by id: %s", vacancyDto.getProjectId()))
        );
        if (!project.getTeams().contains(curator.getTeam())) {
            throw new DataValidationException("The curator is not from the project team");
        }
    }

    public void validateCuratorRole(TeamMember curator) {
        log.info("Validate curator role");
        List<TeamRole> roles = curator.getRoles();
        String errMessage = "The supervisor does not have the appropriate role for hiring employees";
        if (!roles.contains(TeamRole.OWNER) && !roles.contains(TeamRole.MANAGER)) {
            log.error(errMessage);
            throw new DataValidationException(errMessage);
        }
    }

    public void validateSkills(VacancyDto vacancyDto) {
        log.info("Check skill exist");
        vacancyDto.getRequiredSkillIds()
                .forEach(userServiceClient::getSkillById);
    }

    public boolean checkTeamMemberNotExists(Long userId, Long projectId) {
        return teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId) == null;
    }
}
