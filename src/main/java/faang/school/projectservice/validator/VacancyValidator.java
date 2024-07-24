package faang.school.projectservice.validator;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class VacancyValidator {
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;

    public Project validate(VacancyDto vacancyDto) {
        log.info("Validation");
        Project project = projectRepository.getProjectById(vacancyDto.getProjectId());
        checkProjectStatus(project);
        checkRolesOfVacancyUpdater(vacancyDto, project);
        return project;
    }

    public void validateId(Long id) {
        log.info("Validate id");
        if (id == null || id <= 0) {
            throw new DataValidationException("Vacancy id can't be empty");
        }
    }

    public void validateVacancyStatus(VacancyDto vacancyDto) {
        log.info("Validate vacancy status");
        if (!vacancyDto.getStatus().equals(VacancyStatus.OPEN)) {
            throw new DataValidationException("Vacancy must be with status OPEN");
        }
    }

    public void validateCandidatesCount(List<Candidate> candidates, VacancyDto vacancyDto) {
        log.info("Validate candidates count");
        if (candidates.size() < vacancyDto.getCount()) {
            throw new IllegalStateException("Cannot close vacancy, not enough candidates found");
        }
    }

    private void checkRolesOfVacancyUpdater(VacancyDto vacancyDto, Project project) {
        log.info("Check project exist");
        TeamMember curator = getCurator(vacancyDto);
        checkCuratorRole(curator);
        if (!project.getTeams().contains(curator.getTeam())) {
            throw new DataValidationException("The curator is not from the project team");
        }
    }

    private void checkCuratorRole(TeamMember curator) {
        log.info("Check curator role");
        List<TeamRole> roles = curator.getRoles();
        String errMessage = "The supervisor does not have the appropriate role for hiring employees";
        if (!roles.contains(TeamRole.OWNER) && !roles.contains(TeamRole.MANAGER)) {
            throw new DataValidationException(errMessage);
        }
    }

    private TeamMember getCurator(VacancyDto vacancyDto) {
        return teamMemberRepository.findById(vacancyDto.getCreatedBy());
    }

    private void checkProjectStatus(Project project) {
        log.info("Check project status");
        if (project == null || project.getStatus().equals(ProjectStatus.CANCELLED) ||
                project.getStatus().equals(ProjectStatus.COMPLETED)) {
            throw new DataValidationException("This project are cancelled!");
        }
    }
}
