package faang.school.projectservice.service.team;

import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.exception.ResourceNotFoundException;
import faang.school.projectservice.mapper.TeamMapper;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;

    @Transactional
    public TeamDto getById(Long id) {
//        Team team = Optional.of(teamRepository.getReferenceById(id))
//                .orElseThrow(() -> new ResourceNotFoundException(String.format("Team with %s id doesn't exist", id)));

        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Team with %s id doesn't exist", id)));
        return teamMapper.toDto(team);
    }

    public boolean existsById(Long id) {
        return teamRepository.existsById(id);
    }
}
