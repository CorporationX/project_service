package faang.school.projectservice.controller;

import faang.school.projectservice.adapter.CampaignRepositoryAdapter;
import faang.school.projectservice.adapter.ProjectRepositoryAdapter;
import faang.school.projectservice.dto.CampaignDto;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = "Campaign", description = "Api for campaign management")
@RequestMapping("/api/v1/Campaigns")
public class CampaignController {
    private final CampaignMapper mapper;
    private final CampaignRepositoryAdapter campaignRepositoryAdapter;
    private final ProjectRepositoryAdapter projectRepositoryAdapter;

    @Operation(summary = "Create new campaign")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Campaign created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request data"),
            @ApiResponse(responseCode = "404", description = "project with received id not found")
            })
    @PostMapping
    public CampaignDto createCampaign(@Valid @RequestBody CampaignDto campaignDto) {
        Campaign campaign = mapper.toEntity(campaignDto);
        campaign.setStatus(CampaignStatus.ACTIVE);
        campaign.setProject(projectRepositoryAdapter.getById(campaignDto.getProjectId()));
        Campaign campaignWithId = campaignRepositoryAdapter.save(campaign);
        return mapper.toDto(campaignWithId);
    }
}
