package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
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
    public CampaignDto publishCampaign(@RequestBody CampaignDto campaignDto) {
        return campaignService.publish(campaignDto, userContext.getUserId());
    }
}
