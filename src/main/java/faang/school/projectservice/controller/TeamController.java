package faang.school.projectservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.projectservice.dto.TeamDto;
import faang.school.projectservice.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/team")
public class TeamController {
    private final TeamService teamService;

    @PostMapping
    public TeamDto create(@RequestBody TeamDto teamDto) throws JsonProcessingException {
        return teamService.create(teamDto);
    }
}