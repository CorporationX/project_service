package faang.school.projectservice.service.impl.team;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.dto.team.CreateTeamDto;
import faang.school.projectservice.model.dto.team.TeamDto;
import faang.school.projectservice.model.entity.Team;
import faang.school.projectservice.model.event.ManagerAchievementEvent;
import faang.school.projectservice.model.mapper.team.TeamMapper;
import faang.school.projectservice.publisher.ManagerEventPublisher;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamServiceImpl implements TeamService {
    private final ManagerEventPublisher managerEventPublisher;
    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;
    private final ProjectRepository projectRepository;
    private final UserContext userContext;

    @Override
    @Transactional
    public TeamDto create(CreateTeamDto createTeamDto) {
        log.info("request data: {}", createTeamDto);
        projectExistsById(createTeamDto.projectId());
        TeamDto teamDto = TeamDto
                .builder()
                .teamMemberIds(createTeamDto.teamMembersIds())
                .projectId(createTeamDto.projectId())
                .build();
        log.info("team dto data: {}", teamDto);
        Team newTeam = teamRepository.save(teamMapper.toEntity(teamDto));
        log.info("new team: {}", newTeam);
        ManagerAchievementEvent managerAchievementEvent = ManagerAchievementEvent
                .builder()
                .teamId(newTeam.getId())
                .authorId(userContext.getUserId())
                .projectId(createTeamDto.projectId())
                .build();
        managerEventPublisher.publish(managerAchievementEvent);
        return teamMapper.toDto(newTeam);
    }

    private void projectExistsById(Long projectId) throws DataValidationException {
        boolean result = projectRepository.existsById(projectId);
        if (!result) {
            throw new DataValidationException("No project exists with id " + projectId);
        }
    }
}
