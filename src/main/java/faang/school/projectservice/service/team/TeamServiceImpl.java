package faang.school.projectservice.service.team;

import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.events.TeamEvent;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.team.TeamMapper;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.publisher.TeamEventPublisher;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamServiceImpl implements TeamService {

    private final TeamMemberJpaRepository teamMemberRepository;
    private final TeamMapper teamMapper;
    private final TeamRepository teamRepository;
    private final ProjectService projectService;
    private final TeamEventPublisher teamEventPublisher;

    @Transactional
    public TeamDto createTeam(Long authorId, TeamDto teamDto) {
        log.info("Starting team creation. Author ID: {}, DTO: {}", authorId, teamDto);
        Team team = teamMapper.toTeam(teamDto);

        team.setProject(projectService.getProjectEntityById(teamDto.getProjectId()));

        Team savedTeam = teamRepository.save(team);

        TeamEvent teamEvent = new TeamEvent(savedTeam.getId(), authorId,
                teamDto.getProjectId());
        log.info("Publishing team event: {}", teamEvent);
        teamEventPublisher.publish(teamEvent);
        log.info("TeamEvent Published successfully");

        return teamMapper.toTeamDto(savedTeam);
    }

    @Override
    public void deleteMemberByUserId(Long userId) {
        teamMemberRepository.deleteByUserId(userId);
    }

    public Optional<TeamMember> findMemberByUserIdAndProjectId(Long userId, Long projectId) {
        return teamMemberRepository.findByUserIdAndProjectId(userId, projectId);
    }
}
