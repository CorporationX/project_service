package faang.school.projectservice.controller.campaign;

import faang.school.projectservice.dto.client.Campaign.CampaignDto;
import faang.school.projectservice.service.campaign.CampaignService;
import faang.school.projectservice.validator.campaign.CampaignValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/campaign/projects/{projectId}")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;

    @PostMapping("/{creatorId}")
    public ResponseEntity<CampaignDto> create(
            @RequestBody @Valid CampaignDto campaignDto,
            @PathVariable("projectId") long projectId,
            @PathVariable("creatorId") long creatorId
    ) {
        campaignDto.setProjectId(projectId);
        campaignDto.setCreatedBy(creatorId);
        CampaignDto createdCampaign = campaignService.create(campaignDto, projectId, creatorId);

        URI location = URI.create(String.format("api/v1/campaign/projects/%d/campaigns/%d",
                createdCampaign.getProjectId(),
                createdCampaign.getId()));

        return ResponseEntity.created((location)).body(createdCampaign);
    }

    @PutMapping("/campaigns/{campaignId}/{updaterId}")
    public ResponseEntity<CampaignDto> update(
            @RequestBody CampaignDto campaignDto,
            @PathVariable("projectId") long projectId,
            @PathVariable("campaignId") long campaignId,
            @PathVariable("updaterId") long updaterId
    ) {
        CampaignValidator.checkForbiddenValue(campaignDto, updaterId);
        CampaignValidator.checkPermittedValue(campaignDto, updaterId);
        return ResponseEntity.ok(campaignService.update(campaignDto, projectId, campaignId, updaterId));
    }

    @DeleteMapping("/campaigns/{campaignId}/{deleterId}")
    public ResponseEntity<Map<String, String>> softDelete(
            @PathVariable("projectId") long projectId,
            @PathVariable("campaignId") long campaignId,
            @PathVariable("deleterId") long deleterId
    ) {
        campaignService.softDelete(projectId, campaignId, deleterId);

        Map<String, String> response = Map.of(
                "message", String.format("Campaign with id %d was archived successfully", campaignId)
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/campaigns/{campaignId}")
    public ResponseEntity<CampaignDto> getCampaign(
            @PathVariable("projectId") long projectId,
            @PathVariable("campaignId") long campaignId
    ) {
        return ResponseEntity.ok(campaignService.getCampaign(projectId, campaignId));
    }

    @PostMapping()
    public ResponseEntity<List<CampaignDto>> getCampaignByFilter(
            @RequestBody CampaignDto campaignDto,
            @PathVariable("projectId") long projectId
    ) {
        return ResponseEntity.ok(campaignService.getCampaignByFilter(projectId, campaignDto));
    }
}
