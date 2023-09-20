package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilter;
import faang.school.projectservice.dto.campaign.CampaignGetDto;
import faang.school.projectservice.dto.campaign.CampaignUpdatedDto;
import faang.school.projectservice.service.CampaignService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/campaign")
public class CampaignController {

    private final CampaignService campaignService;
    private final UserContext userContext;

    @PostMapping
    public CampaignGetDto createCampaign(@RequestBody @Valid CampaignDto campaign) {
        long userId = userContext.getUserId();
        return campaignService.createCampaign(campaign, userId);
    }

    @PutMapping("/{campaignId}")
    public CampaignGetDto updateCampaign(@RequestBody CampaignUpdatedDto campaignDto, @PathVariable long campaignId) {
        long userId = userContext.getUserId();
        return campaignService.updateCampaign(campaignDto, userId, campaignId);
    }

    @PatchMapping("/{id}")
    public void deleteSoftCampaign(@PathVariable long id) {
        campaignService.deleteSoftCampaign(id);
    }

    @GetMapping("/{id}")
    public CampaignGetDto getCampaignById(@PathVariable long id) {
        return campaignService.getCampaignById(id);
    }

    @GetMapping("/list")
    public List<CampaignGetDto> getCampaignByFilter(@RequestBody CampaignFilter campaignFilter) {
        return campaignService.getCampaignsByFilter(campaignFilter);
    }
}
