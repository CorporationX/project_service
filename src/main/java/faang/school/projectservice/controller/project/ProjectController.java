package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilter;
import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.campaign.CampaignService;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final CampaignService campaignService;

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@RequestBody @Valid ProjectDto projectDto) {
        return ResponseEntity.ok(projectService.create(projectDto));
    }

    @PatchMapping("/update-status/{id}")
    public ResponseEntity<ProjectDto> updateStatus(@PathVariable @NonNull Long id, @RequestBody @NonNull ProjectStatus projectStatus) {
        return ResponseEntity.ok(projectService.updateStatus(projectStatus, id));
    }

    @PatchMapping("/update-description/{id}")
    public ResponseEntity<ProjectDto> updateDescription(@PathVariable long id, @RequestBody @NonNull String description) {
        return ResponseEntity.ok(projectService.updateDescription(description, id));
    }

    @PostMapping("/filtered")
    public List<ProjectDto> findWithFilters(@RequestBody @NonNull @Valid ProjectFilterDto filters) {
        return projectService.findWithFilters(filters);
    }

    @GetMapping
    public List<ProjectDto> findAll() {
        return projectService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> findById(@PathVariable @NonNull @Positive Long id) {
        return projectService.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("{projectId}/filterChildren")
    public ResponseEntity<List<CreateSubProjectDto>> getProjectsByFilters(@RequestBody @Valid ProjectFilterDto filterDto, @PathVariable @Positive @NotNull Long projectId) {
        return ResponseEntity.ok(projectService.getProjectsByFilters(projectId, filterDto));
    }

    @GetMapping("{id}/campaigns")
    public List<CampaignDto> getByProject(@PathVariable Long id, @ModelAttribute @Valid CampaignFilter filter) {
        return campaignService.getCampaignsByProject(id, filter);
    }

}
