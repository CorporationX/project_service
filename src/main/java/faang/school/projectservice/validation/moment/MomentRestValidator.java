package faang.school.projectservice.validation.moment;

import faang.school.projectservice.dto.moment.MomentRestDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MomentRestValidator {
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;

    public void momentHasProjectValidation(MomentRestDto momentDto) {
        momentDto.getProjects()
                .stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Moment has no project"));
    }

    //Todo Перенести в ProjectValidator
    public void projectNotCancelledValidator(List<Long> projectIds) {
        projectRepository.findAllByIds(projectIds)
                .stream()
                .filter(project -> project.getStatus().equals(ProjectStatus.CANCELLED))
                .forEach(project -> {
                    throw new IllegalArgumentException("Project is cancelled");
                });
    }

    public void projectsUpdateValidator(Moment oldMoment, MomentRestDto newMomentDto) {
        Set<Long> newProjectIds = new HashSet<>(newMomentDto.getProjects());
        newProjectIds.retainAll(oldMoment.getProjects()
                .stream()
                .map(Project::getId)
                .collect(Collectors.toSet()));

        if (newProjectIds.size() > 0) {
            Set<Long> momentUserIds = new HashSet<>(newMomentDto.getUserIds());
            newProjectIds.forEach(projectId ->
                    momentUserIds.addAll(projectRepository.getProjectById(projectId)
                            .getTeams()
                            .stream()
                            .flatMap(team -> team.getTeamMembers().stream())
                            .map(TeamMember::getId)
                            .distinct()
                            .toList()));
            newMomentDto.setUserIds(momentUserIds.stream().toList());
        }
    }

    public void membersUpdateValidator(Moment oldMoment, MomentRestDto newMomentDto) {
        Set<Long> newUserIds = new HashSet<>(newMomentDto.getUserIds());
        Set<Long> oldUserIds = new HashSet<>(oldMoment.getUserIds());
        newUserIds.retainAll(oldUserIds);

        if (newUserIds.size() > 0) {
            Set<Long> projectIds = new HashSet<>(newMomentDto.getProjects());
            newUserIds.forEach(userId -> {
                Long userProjectId = teamMemberRepository.findById(userId)
                        .getTeam()
                        .getProject()
                        .getId();
                projectIds.add(userProjectId);
            });
            newMomentDto.setProjects(projectIds.stream().toList());
        }
    }
}
