package faang.school.projectservice.controller;

import faang.school.projectservice.model.dto.CampaignDto;
import faang.school.projectservice.model.dto.CampaignFilterDto;
import faang.school.projectservice.service.CampaignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/campaign")
@Tag(name = "Campaign Management", description = "API for managing campaigns")
public class CampaignController {
    private final CampaignService campaignService;

    @PostMapping
    @Operation(summary = "Create a campaign", description = "Create a new campaign")
    CampaignDto create(
            @Parameter(description = "ID of the user", required = true)
            @RequestHeader("x-user-id") String userId,
            @Valid @RequestBody CampaignDto campaignDto) {
        return campaignService.create(campaignDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update campaign", description = "Update an existing campaign")
    CampaignDto update(
            @Parameter(description = "ID of the user", required = true)
            @RequestHeader("x-user-id") String userId,
            @PathVariable @NotNull long id,
            @Valid @RequestBody CampaignDto campaignDto) {
        return campaignService.update(id, campaignDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete of campaign", description = "Soft delete of campaign")
    public void delete(
            @Parameter(description = "ID of the user", required = true)
            @RequestHeader("x-user-id") String userId,
            @Parameter(description = "ID of the campaign", required = true)
            @PathVariable Long id) {
        campaignService.delete(id);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an campaign by ID", description = "Retrieve an campaign by its ID")
    public CampaignDto getCampaignById(
            @Parameter(description = "ID of the user", required = true)
            @RequestHeader("x-user-id") String userId,
            @Parameter(description = "ID of the campaign", required = true)
            @PathVariable Long id) {
        return campaignService.getById(id);
    }

    @PostMapping("/filter")
    @Operation(summary = "Get campaigns by filter", description = "Retrieve campaigns by filters: creation date, " +
            "status, creator. Campaigns ordered by creation date from new to old")
    public List<CampaignDto> getCampaignsByFilter(
            @Parameter(description = "ID of the user", required = true)
            @RequestHeader("x-user-id") String userId,
            @Parameter(description = "Filter for campaigns", required = true)
            @RequestBody CampaignFilterDto filterDto) {
        return campaignService.getByFilter(filterDto);
    }
}
