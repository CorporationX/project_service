package faang.school.projectservice.controller.team;

import faang.school.projectservice.dto.client.event.TeamEventDto;
import faang.school.projectservice.publisher.TeamEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/team")
@RequiredArgsConstructor
public class TeamController {

    private final TeamEventPublisher teamEventPublisher;

    @PutMapping
    public void createTeam() {
        TeamEventDto teamEventDto = TeamEventDto.builder()
                .userId(1L)
                .projectId(2L)
                .teamId(1L)
                .build();
        teamEventPublisher.publish(teamEventDto);
    }
}