package faang.school.projectservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.projectservice.dto.TeamDto;
import faang.school.projectservice.dto.TeamEvent;
import faang.school.projectservice.publisher.TeamPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamPublisher publisher;

    public TeamDto create(TeamDto teamDto) throws JsonProcessingException {
        publisher.publish(TeamEvent.builder()
                .projectId(teamDto.getProjectId())
                .userId(teamDto.getAuthorId())
                .teamId(teamDto.getId())
                .build());
        return teamDto;
    }
}