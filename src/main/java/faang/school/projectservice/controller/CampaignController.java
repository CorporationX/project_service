package faang.school.projectservice.controller;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.dto.campaign.CampaignUpdateDto;
import faang.school.projectservice.service.CampaignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/campaigns")
public class CampaignController {
    private final CampaignService campaignService;

    @PostMapping
    public CampaignDto createCampaign(@RequestBody CampaignDto campaignDto) {
        CampaignDto returnedCampaignDto = campaignService.create(campaignDto);
        log.info("The campaign has been created");
        return returnedCampaignDto;
    }

    @PutMapping
    public CampaignDto updateCampaign(@RequestBody CampaignUpdateDto campaignUpdateDto) {
        CampaignDto returnedCampaignDto = campaignService.update(campaignUpdateDto);
        log.info("The campaign has been updated");
        return returnedCampaignDto;
    }

    @DeleteMapping("/{campaignId}")
    public void deleteCampaign(@PathVariable Long campaignId) {
        campaignService.delete(campaignId);
        log.info("Campaign has been deleted");
    }

    @GetMapping("/{campaignId}")
    public CampaignDto getCampaign(@PathVariable Long campaignId) {
        return campaignService.getCampaign(campaignId);
    }

    @GetMapping
    public List<CampaignDto> getCampaigns(@RequestBody CampaignFilterDto campaignFilterDto) {
        return campaignService.getCampaignsByProject(campaignFilterDto);
    }
}
