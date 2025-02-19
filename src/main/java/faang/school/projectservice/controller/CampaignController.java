package faang.school.projectservice.controller;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.dto.campaign.CreateCampaignDto;
import faang.school.projectservice.dto.campaign.UpdateCampaignDto;
import faang.school.projectservice.service.CampaignService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/campaigns")
public class CampaignController {
    private final CampaignService campaignService;

    @PostMapping
    public CampaignDto createCampaign(@RequestHeader("x-user-id") Long userId,
                                      @Valid @RequestBody CreateCampaignDto createCampaignDto) {
        return campaignService.createCampaign(userId, createCampaignDto);
    }

    @PutMapping("/{campaignId}")
    public CampaignDto updateCampaign(@RequestHeader("x-user-id") Long userId,
                                      @PathVariable Long campaignId,
                                      @Valid @RequestBody UpdateCampaignDto updateCampaignDto) {
        return campaignService.updateCampaign(userId, campaignId, updateCampaignDto);
    }

    @DeleteMapping("/{campaignId}")
    public void deleteCampaign(@RequestHeader("x-user-id") Long userId,
                               @PathVariable Long campaignId) {
        campaignService.deleteCampaign(userId, campaignId);
    }

    @GetMapping("/{campaignId}")
    public CampaignDto getCampaignById(@PathVariable Long campaignId) {
        return campaignService.getCampaignDtoById(campaignId);
    }

    @GetMapping
    public List<CampaignDto> getFilteredCampaignsByProject(@ModelAttribute CampaignFilterDto campaignFilterDto) {
        return campaignService.getAllCampaignsByProject(campaignFilterDto);
    }
}
