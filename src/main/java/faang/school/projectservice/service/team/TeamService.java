package faang.school.projectservice.service.team;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.team.CreateMembersDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.util.TeamMemberUtil;
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
    private final TeamMemberService teamMemberService;
    private final UserContext userContext;

    @Transactional
    public Team createTeam(Long projectId, CreateMembersDto dto) {
        Project project = projectRepository.getProjectByIdOrThrow(projectId);
        TeamMemberUtil.validateProjectOwner(project, userContext.getUserId());
        Team team = new Team();
        team.setProject(project);
        if (Objects.nonNull(dto)) {
            teamRepository.save(team);
            teamMemberService.addToTeam(team.getId(), dto.getRole(), dto.getUserIds());
        } else {
            // at team creation, owner should be added to the team
            // this method should be defined in teamMemberService
        }

        project.getTeams().add(team);
        projectRepository.save(project);
        log.info("Created team(teamID = {}) for project(projectID = {})", team.getId(), projectId);
        return teamRepository.save(team);
    }
}
