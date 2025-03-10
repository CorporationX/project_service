package faang.school.projectservice.controller;

import faang.school.projectservice.dto.TeamEvent;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {

  private final TeamService teamService;

  @PostMapping
  public ResponseEntity<Team> createTeam(@RequestBody TeamEvent teamEvent) {
    Team createdTeam = teamService.createTeam(teamEvent);
    return ResponseEntity.ok(createdTeam);
  }
}
