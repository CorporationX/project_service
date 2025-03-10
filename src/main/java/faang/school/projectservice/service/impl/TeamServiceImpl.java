package faang.school.projectservice.service.impl;

import faang.school.projectservice.dto.TeamEvent;
import faang.school.projectservice.kafka.TeamEventPublisher;
import faang.school.projectservice.mapper.TeamMapper;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {
  private final TeamEventPublisher teamEventPublisher;
  private final TeamRepository teamRepository;
  private final TeamMapper teamMapper;

  @Override
  @Transactional
  public Team createTeam(TeamEvent teamEvent) {
    Team team = teamMapper.toEntity(teamEvent);
    Team savedTeam = teamRepository.save(team);
    teamEventPublisher.publishTeamEvent(teamEvent);

    return savedTeam;
  }
}