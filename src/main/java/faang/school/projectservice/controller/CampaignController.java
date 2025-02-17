package faang.school.projectservice.controller;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CreateCampaignDto;
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
    public void updateCampaign() {

    }

    @DeleteMapping("/{campaignId}")
    public void deleteCampaign() {

    }

    @GetMapping("/{campaignId}")
    public void getCampaignById() {

    }

    @GetMapping
    public void getFilteredCampaignsByProject() {

    }
}
