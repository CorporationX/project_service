package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.service.CampaignService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/campaigns")
public class CampaignController {

    private final UserContext userContext;
    private final CampaignService campaignService;

    @PostMapping("/publish")
    public CampaignDto publishCampaign(@RequestBody @Valid CampaignDto campaignDto) {
        return campaignService.publish(campaignDto, userContext.getUserId());
    }

    @PutMapping("/")
    public CampaignDto updateCampaign(@RequestBody @Valid CampaignDto campaignDto) {
        return campaignService.update(campaignDto, userContext.getUserId());
    }

    @DeleteMapping("/{campaignId}")
    public void deleteCampaign(@PathVariable("campaignId") long campaignId) {
        campaignService.delete(campaignId);
    }

    @GetMapping("/{campaignId}/get-campaign")
    public CampaignDto getCampaignById(@PathVariable("campaignId") long campaignId) {
        return campaignService.getCampaign(campaignId);
    }
}
