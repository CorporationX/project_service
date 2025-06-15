package faang.school.projectservice.controller.campaign;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.service.campaign.CampaignService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("projects/{projectId}/campaigns")
public class CampaignController {
    private final CampaignService campaignService;

    @PostMapping
    public ResponseEntity<CampaignDto> create(
            @PathVariable Long projectId,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CampaignDto dto
    ) {
        dto.setProjectId(projectId);
        return new ResponseEntity<>(campaignService.createCampaign(dto, userId), HttpStatus.CREATED);
    }

    @PutMapping("/{campaignId}")
    public CampaignDto update(
            @PathVariable Long projectId,
            @PathVariable Long campaignId,
            @RequestHeader("X-User-Id") @NotNull(message = "User ID is required") Long userId,
            @Valid @RequestBody CampaignDto dto
    ) {
        dto.setProjectId(projectId);
        dto.setId(campaignId);
        return campaignService.updateCampaign(campaignId, dto, userId);
    }

    @DeleteMapping("/{campaignId}")
    public CampaignDto delete(
            @PathVariable Long campaignId,
            @RequestHeader("X-User-Id") @NotNull(message = "User ID is required") Long userId) {
        return campaignService.softDelete(campaignId, userId);
    }

    @GetMapping("/{campaignId}")
    public CampaignDto getById(@PathVariable Long campaignId) {
        return campaignService.getCampaignById(campaignId);
    }

    @GetMapping("/filter")
    public Page<CampaignDto> getWithFilters(
            @ModelAttribute CampaignFilterDto campaignFilterDto,
            @PageableDefault(size = 20, sort = "{createdAt}", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return campaignService.getCampaignDtoWithFilters(campaignFilterDto, pageable);
    }
}
