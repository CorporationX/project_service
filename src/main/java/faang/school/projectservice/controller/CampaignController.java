package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.company.CampaignDto;
import faang.school.projectservice.service.CampaignService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/campaigns")
public class CampaignController {
    private final CampaignService campaignService;
    private final UserContext userContext;

    @PostMapping("/publish")
    public CampaignDto publishCampaign(@RequestBody @Valid CampaignDto dto) {
        return campaignService.publishCampaign(dto, userContext.getUserId());
    }

    @PutMapping("/")
    public CampaignDto updateCampaign(@RequestBody @Valid CampaignDto dto) {
        return campaignService.updateCampaign(dto, userContext.getUserId());
    }
}
