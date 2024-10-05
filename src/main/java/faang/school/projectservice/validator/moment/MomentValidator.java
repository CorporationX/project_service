package faang.school.projectservice.validator.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MomentValidator {
    private final ProjectRepository projectRepository;
    private final MomentRepository momentRepository;

    public void validateMomentDto(MomentDto momentDto) {
        if (momentDto.name() == null || momentDto.name().isEmpty() || momentDto.name().isBlank()) {
            log.error("The name field is empty {}", momentDto);
            throw new DataValidationException("The name cannot be empty");
        }
    }

    public List<Project> validateProjectsByIdAndStatus(MomentDto momentDto) {
        List<Project> projects = projectRepository.findAllByIds(momentDto.projectIds());

        if (projects.isEmpty()) {
            log.error("the project list is empty for these IDs " + momentDto.projectIds());
            throw new DataValidationException("The list of projects was not found");
        }

        for (Project project : projects) {
            if (project.getStatus().equals(ProjectStatus.CANCELLED)) {
                log.error("Project with ID " + project.getId() + " has the status canceled");
                throw new DataValidationException("close the project with ID " + project.getId());
            }
        }

        return projects;
    }

    public Moment validateExistingMoment(long momentId) {
        return momentRepository.findById(momentId).orElseThrow(() ->
                new DataValidationException("Moment with ID " + momentId + " not found"));
    }

    public List<Project> validateProjectsByUserIdAndStatus(long userId) {
        List<Project> projects = projectRepository.findAll()
                .stream()
                .filter(project -> !project.getStatus().equals(ProjectStatus.CANCELLED))
                .filter(project -> project.getTeams().stream()
                        .anyMatch(team -> team.getTeamMembers().stream()
                                .anyMatch(teamMember -> teamMember.getUserId() == userId)))
                .toList();

        if (projects.isEmpty()) {
            log.error("User with ID " + userId + " there is no project");
            throw new DataValidationException("The project was not found by user with ID " + userId);
        }
        return projects;
    }
}
