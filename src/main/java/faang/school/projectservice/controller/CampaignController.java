package faang.school.projectservice.controller;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CreateCampaignDto;
import faang.school.projectservice.dto.campaign.UpdateCampaignDto;
import faang.school.projectservice.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/campaigns")
public class CampaignController {
    private final CampaignService campaignService;

    @PostMapping
    public CampaignDto createCampaign(@RequestHeader("x-user-id") Long userId,
                                      @RequestBody CreateCampaignDto createCampaignDto) {
        return campaignService.createCampaign(userId, createCampaignDto);
    }

    @PutMapping("/{campaignId}")
    public CampaignDto updateCampaign(@RequestHeader("x-user-id") Long userId,
                                      @RequestParam Long campaignId,
                                      UpdateCampaignDto updateCampaignDto) {
        return campaignService.updateCampaign(userId, campaignId, updateCampaignDto);
    }

    @DeleteMapping("/{campaignId}")
    public void deleteCampaign(@RequestParam Long campaignId) {
        campaignService.deleteCampaign(campaignId);
    }

    @GetMapping("/{campaignId}")
    public CampaignDto getCampaignById(@RequestParam Long campaignId) {
        return campaignService.getCampaignDtoById(campaignId);
    }

    @GetMapping
    public void getFilteredCampaignsByProject() {

    }
}
