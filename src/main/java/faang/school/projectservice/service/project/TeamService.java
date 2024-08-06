package faang.school.projectservice.service.project;

import faang.school.projectservice.model.Team;
import faang.school.projectservice.service.utilservice.TeamUtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamUtilService teamUtilService;

    public List<Team> findTeamsByIds(List<Long> ids) {
        return teamUtilService.findTeamsById(ids);
    }
}
