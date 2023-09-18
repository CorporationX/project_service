package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
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

import java.util.List;

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

    @GetMapping("/{projectId}/get-all-campaign")
    public List<CampaignDto> getCampaignsByProjectId(@PathVariable("projectId") long projectId) {
        return campaignService.getAllCampaigns(projectId);
    }

    @GetMapping("/get-by-filters")
    public List<CampaignDto> getFilter(@RequestBody CampaignFilterDto campaignFilterDto) {
        return campaignService.getByFilters(campaignFilterDto);
    }
}
