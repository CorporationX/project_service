package faang.school.projectservice.service.team;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.projectservice.dto.team.CreateTeamDto;
import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.dto.team.TeamEvent;
import faang.school.projectservice.publisher.TeamPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamPublisher publisher;

    public CreateTeamDto create(TeamDto teamDto) throws JsonProcessingException {
        publisher.publish(TeamEvent.builder()
                .projectId(teamDto.getProjectId())
                .authorId(teamDto.getAuthorId())
                .teamId(teamDto.getId())
                .build());
        return CreateTeamDto.builder()
                .id(teamDto.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
