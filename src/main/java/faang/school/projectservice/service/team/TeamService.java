package faang.school.projectservice.service.team;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamService {
    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;

    @Transactional
    public Team createTeam(Long projectId) {
        Project project = projectRepository.getByIdOrThrow(projectId);

        Team team = new Team();
        team.setProject(project);
        Team created = teamRepository.save(team);
        log.info("Created team(teamID = {}) for project(projectID = {})", created.getId(), projectId);
        if (Objects.isNull(project.getTeams()) || project.getTeams().isEmpty()) {
            project.setTeams(List.of(team));
        } else {
            project.getTeams().add(team);
        }
        projectRepository.save(project);
        return created;
    }
}
