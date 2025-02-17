package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.dto.team.TeamMemberFilterDto;
import faang.school.projectservice.service.TeamAvatarService;
import faang.school.projectservice.service.TeamService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {
    private final TeamAvatarService teamAvatarService;
    private final TeamService teamService;
    private final UserContext userContext;

    @PostMapping("/{id}/upload_avatar")
    public ResponseEntity<?> uploadTeamAvatar(@PathVariable("id") Long id, @RequestParam("file") MultipartFile avatar) {
        teamAvatarService.uploadAvatar(id, avatar);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/remove_avatar")
    public ResponseEntity<?> removeTeamAvatar(@PathVariable("id") Long id, @RequestParam("teamMemberId") Long teamMemberId) {
        teamAvatarService.removeAvatar(id, teamMemberId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/member")
    public ResponseEntity<TeamMemberDto> addTeamMember(@RequestBody TeamMemberDto teamMemberDto) {
        Long requesterId = userContext.getUserId();
        TeamMemberDto teamMember = teamService.addMember(teamMemberDto, requesterId);
        return ResponseEntity.status(HttpStatus.CREATED).body(teamMember);
    }

    @PostMapping("{teamId}/member/all")
    public ResponseEntity<List<TeamMemberDto>> getAllMember(@Valid @NotNull @PathVariable Long teamId, @RequestBody TeamMemberFilterDto filterDto) {
        return ResponseEntity.ok(teamService.getAllMembers(teamId, filterDto));
    }

    @GetMapping("/member/{userId}")
    public ResponseEntity<TeamMemberDto> getTeamMember(@Valid @NotNull @PathVariable Long userId) {
        return ResponseEntity.ok(teamService.getMember(userId));
    }

    @PutMapping("/member/role/{userId}")
    public ResponseEntity<TeamMemberDto> updateTeamMemberRole(@Valid @NotNull @PathVariable Long userId, @RequestBody TeamMemberDto teamMemberDto) {
        Long requesterId = userContext.getUserId();
        TeamMemberDto updatedMember = teamService.updateMemberRole(userId, teamMemberDto, requesterId);
        return ResponseEntity.ok(updatedMember);
    }

    @PutMapping("/member/name/{userId}")
    public ResponseEntity<TeamMemberDto> updateTeamMemberName(@Valid @NotNull @PathVariable Long userId, @RequestBody TeamMemberDto teamMemberDto) {
        TeamMemberDto updatedMember = teamService.updateMemberName(userId, teamMemberDto);
        return ResponseEntity.ok(updatedMember);
    }

    @DeleteMapping("{teamId}/member/{userId}")
    public ResponseEntity<Void> removeTeamMember(@Valid @NotNull @PathVariable Long teamId, @Valid @NotNull @PathVariable Long userId) {
        Long requesterId = userContext.getUserId();
        teamService.removeMember(teamId, userId, requesterId);
        return ResponseEntity.noContent().build();
    }
}
