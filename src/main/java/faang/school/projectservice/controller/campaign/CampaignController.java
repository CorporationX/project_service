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
@RequestMapping("/campaign")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;

    @PostMapping("/{projectId}/{creatorId}")
    public ResponseEntity<CampaignDto> create(
            @RequestBody @Valid CampaignDto campaignDto,
            @PathVariable("projectId") long projectId,
            @PathVariable("creatorId") long creatorId
    ) {
        campaignDto.setProjectId(projectId);
        campaignDto.setCreatedBy(creatorId);
        CampaignDto createdCampaign = campaignService.create(campaignDto, projectId, creatorId);

        URI location = URI.create("/campaign/" + createdCampaign.getId());

        return ResponseEntity.created((location)).body(createdCampaign);
    }

    @PutMapping("/{projectId}/{campaignId}/{updaterId}")
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

    @DeleteMapping("/{projectId}/{campaignId}/{deleterId}")
    public ResponseEntity<Map<String, String>> softDelete(
            @PathVariable("projectId") long projectId,
            @PathVariable("campaignId") long campaignId,
            @PathVariable("deleterId") long deleterId
    ) {
        campaignService.softDelete(projectId, campaignId, deleterId);

        Map<String, String> response = Map.of(
                "message", String.format("Campaign with %d id was soft-deleted successful", campaignId)
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{projectId}/{campaignId}")
    public ResponseEntity<CampaignDto> getCampaign(
            @PathVariable("projectId") long projectId,
            @PathVariable("campaignId") long campaignId
    ) {
        return ResponseEntity.ok(campaignService.getCampaign(projectId, campaignId));
    }

    @PostMapping ("/{projectId}")
    public ResponseEntity<List<CampaignDto>> getCampaignByFilter(
            @RequestBody CampaignDto campaignDto,
            @PathVariable("projectId") long projectId
    ) {
        return ResponseEntity.ok(campaignService.getCampaignByFilter(projectId, campaignDto));
    }
}
