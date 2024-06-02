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
        List<Long> newProject = new ArrayList<>(newMomentDto.getProjectIds());
        newProject.removeAll(
                oldMoment.getProjects()
                        .stream()
                        .map(Project::getId)
                        .toList()
        );

        List<Long> momentUserIds = new ArrayList<>(newMomentDto.getUserIds());
        newProject.forEach(projectId ->
                momentUserIds.addAll(projectRepository.getProjectById(projectId)
                        .getTeams()
                        .stream()
                        .flatMap(team -> team.getTeamMembers().stream())
                        .map(TeamMember::getId)
                        .distinct()
                        .toList()
                ));
        newMomentDto.setUserIds(new ArrayList<>(momentUserIds));

        momentMapper.toEntity(newMomentDto);
    }

    public void membersUpdateValidator(Moment oldMoment, MomentDto newMomentDto) {
        List<Long> newMember = new ArrayList<>(newMomentDto.getUserIds());
        newMember.removeAll(oldMoment.getUserIds());

        List<Long> projectIds = newMomentDto.getProjectIds();
        newMember.forEach(userId ->
                projectIds.add(teamMemberRepository.findById(userId)
//                    .getTeam()
//                  .getProject()
                                .getId()
                ));
        newMomentDto.setProjectIds(new ArrayList<>(projectIds));

        momentMapper.toEntity(newMomentDto);
    }
}
