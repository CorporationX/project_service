package faang.school.projectservice.controller;

import faang.school.projectservice.dto.CampaignDto;
import faang.school.projectservice.service.CampaignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = "Campaign", description = "Api for campaign management")
@RequestMapping("/api/v1/Campaigns")
public class CampaignController {
    private final CampaignService service;

    @Operation(summary = "Create new campaign", description = "The fields id, amountRaised, createdAt," +
            " createdBy, updatedAt, updatedBy" +
            " are set automatically on creation and cannot be changed manually," +
            " so they must be null when queried. When created, the status can only be ACTIVE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Campaign created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request data"),
            @ApiResponse(responseCode = "404", description = "project with received id not found")
    })
    @PostMapping
    public CampaignDto createCampaign(@Valid @RequestBody CampaignDto campaignDto) {
        return service.createCampaign(campaignDto);
    }

    @PutMapping("/{id}")
    public CampaignDto updateCampaign(@Valid @RequestBody CampaignDto campaignDto, @PathVariable Long campaignId) {
         return service.updateCampaign(campaignDto, campaignId);
    }
}
