package faang.school.projectservice.service.team;

import faang.school.projectservice.dto.publisher.achievement.ConglomerateAchievementDto;
import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.mapper.team.TeamMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.publisher.ConglomerateAchievementPublisher;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamService {
    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final TeamMapper teamMapper;
    private final ConglomerateAchievementPublisher conglomerateAchievementPublisher;

    public TeamDto createTeam(TeamDto teamDto, Long userId) {
        Project project = projectRepository.findById(teamDto.getProjectId()).orElseThrow(
                () -> new IllegalArgumentException("Project not found")
        );

        Team team = teamMapper.toEntity(teamDto);
        team.setProject(project);

        Team savedTeam = teamRepository.save(team);

        ConglomerateAchievementDto achievementDto = ConglomerateAchievementDto.builder()
                .userId(userId)
                .teamId(savedTeam.getId())
                .projectId(project.getId())
                .build();
        conglomerateAchievementPublisher.publish(achievementDto);
        log.info("Achievement was published");
        return teamMapper.toDto(savedTeam);
    }
}
