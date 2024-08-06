package faang.school.projectservice.service.utilservice;

import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamUtilService {
    private final TeamRepository teamRepository;

    public List<Team> findTeamsById(List<Long> ids) {
        return teamRepository.findAllById(ids);
    }
}
