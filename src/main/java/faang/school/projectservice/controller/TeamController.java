package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.dto.team.TeamMemberFilterDto;
import faang.school.projectservice.service.TeamAvatarService;
import faang.school.projectservice.service.TeamMemberService;
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
    private final TeamMemberService teamMemberService;
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
        TeamMemberDto teamMember = teamMemberService.addMember(teamMemberDto, requesterId);
        return ResponseEntity.status(HttpStatus.CREATED).body(teamMember);
    }

    @PostMapping("{teamId}/member/all")
    public ResponseEntity<List<TeamMemberDto>> getAllMember(@Valid @NotNull @PathVariable Long teamId, @RequestBody TeamMemberFilterDto filterDto) {
        return ResponseEntity.ok(teamMemberService.getAllMembers(teamId,filterDto));
    }

    @GetMapping("/member/{userId}")
    public ResponseEntity<TeamMemberDto> getTeamMember(@Valid @NotNull @PathVariable Long userId) {
        return ResponseEntity.ok(teamMemberService.getMember(userId));
    }

    @PutMapping("/member/{userId}")
    public ResponseEntity<TeamMemberDto> updateTeamMember(@Valid @NotNull @PathVariable Long userId, @RequestBody TeamMemberDto teamMemberDto) {
        Long requesterId = userContext.getUserId();
        TeamMemberDto updatedMember = teamMemberService.updateMember(userId, teamMemberDto, requesterId);
        return ResponseEntity.ok(updatedMember);
    }

    @DeleteMapping("{teamId}/member/{userId}")
    public ResponseEntity<Void> removeTeamMember(@Valid @NotNull @PathVariable Long teamId, @Valid @NotNull @PathVariable Long userId) {
        Long requesterId = userContext.getUserId();
        teamMemberService.removeMember(teamId, userId, requesterId);
        return ResponseEntity.noContent().build();
    }
}
