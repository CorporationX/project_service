package faang.school.projectservice.controller;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.service.CampaignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "Campaign", description = "Api for campaign management")
@RequestMapping("/api/v1/Campaigns")
@Valid
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

    @Operation(summary = "Change campaign by id", description = "The fields id, amountRaised, createdAt," +
            " createdBy, updatedAt, updatedBy" +
            " are set automatically on creation and cannot be changed manually")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Campaign updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request data"),
            @ApiResponse(responseCode = "404", description = "Campaign with received id not found")
    })
    @PutMapping("/{campaignId}")
    public CampaignDto updateCampaign(@Valid @RequestBody CampaignDto campaignDto,
                                      @PathVariable @Valid @Positive long campaignId) {
         return service.updateCampaign(campaignDto, campaignId);
    }

    @Operation(summary = "Delete campaign by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Campaign deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request data"),
            @ApiResponse(responseCode = "404", description = "Campaign with received id not found")
    })
    @DeleteMapping("/{id}")
    public void deleteCampaign(@PathVariable @Valid @Positive long id) {
        service.deleteCampaign(id);
    }

    @Operation(summary = "Get campaign by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Campaign founded successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request data"),
            @ApiResponse(responseCode = "404", description = "Campaign with received id not found")
    })
    @GetMapping("/{id}")
    public CampaignDto getCampaign(@PathVariable @Valid @Positive long id) {
        return service.getCampaign(id);
    }

    @Operation(summary = "Get campaigns with filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Campaigns founded successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request data")
    })
    @GetMapping
    public List<CampaignDto> getCampaignsByProject(CampaignFilterDto filterDto) {
        return service.getCampaigns(filterDto);
    }
}
