package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ResponseProjectDto;
import faang.school.projectservice.dto.teamMember.ResponseTeamMemberDto;
import faang.school.projectservice.model.team.TeamRole;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.TeamMemberService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
@RestController
public class ProjectV1Controller {
    private final TeamMemberService teamService;
    private final ProjectService projectService;

    @GetMapping("/{projectId}/members")
    public List<ResponseTeamMemberDto> getFilteredTeamMembers(@RequestParam(required = false) String name,
                                                              @RequestParam(required = false) TeamRole role,
                                                              @PathVariable @Positive @NotNull Long projectId) {
        return teamService.getFilteredTeamMembers(name, role, projectId);
    }

    @GetMapping("/{projectId}")
    public ResponseProjectDto getProject(@PathVariable @Positive Long projectId) {
        return projectService.getProject(projectId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{projectId}/cover")
    public ResponseProjectDto addCover(@PathVariable @Positive long projectId,
                         @RequestPart MultipartFile file) {
        return projectService.addCover(projectId, file);
    }
}
