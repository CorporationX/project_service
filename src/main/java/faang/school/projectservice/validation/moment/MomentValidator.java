package faang.school.projectservice.validation.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MomentValidator {
    private final ProjectRepository projectRepository;
    private final MomentMapper momentMapper;
    private final TeamMemberRepository teamMemberRepository;

    public void momentProjectValidation(MomentDto momentDto) {
        momentDto.getProjectIds()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Moment has no project"));
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

    public void projectsUpdateValidator(Moment oldMoment, MomentDto newMomentDto) {
        List<Long> oldProject = oldMoment.getProjects()
                .stream()
                .map(Project::getId)
                .toList();

        List<Long> newProject = new ArrayList<>(newMomentDto.getProjectIds());
        newProject.removeAll(oldProject);

        List<Long> momentUserIds = new ArrayList<>(newMomentDto.getUserIds());
        newProject.forEach(projectId -> {
            List<Long> userIds = projectRepository.getProjectById(projectId)
                    .getTeams()
                    .stream()
                    .flatMap(team -> team.getTeamMembers().stream())
                    .map(TeamMember::getId)
                    .distinct()
                    .toList();
            momentUserIds.addAll(userIds);
        });
        newMomentDto.setUserIds(new ArrayList<>(momentUserIds));

        momentMapper.toEntity(newMomentDto);
    }

    public void membersUpdateValidator(Moment oldMoment, MomentDto newMomentDto) {
        List<Long> oldMember = oldMoment.getUserIds();

        List<Long> newMember = new ArrayList<>(newMomentDto.getUserIds());
        newMember.removeAll(oldMember);

        List<Long> projectIds = newMomentDto.getProjectIds();
        newMember.forEach(userId -> {
            Long team = teamMemberRepository.findById(userId)
                    .getTeam()
                    .getProject()
                    .getId();
            projectIds.add(team);
        });
        newMomentDto.setProjectIds(projectIds);

        momentMapper.toEntity(newMomentDto);
    }
}
