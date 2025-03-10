package faang.school.projectservice.service;

import faang.school.projectservice.dto.TeamEvent;
import faang.school.projectservice.model.Team;

public interface TeamService {
  Team createTeam(TeamEvent teamEvent);
}