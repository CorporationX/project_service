package faang.school.projectservice.controller;

import faang.school.projectservice.dto.campaign.CampaignCreateDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.dto.campaign.CampaignUpdateDto;
import faang.school.projectservice.dto.campaign.ResponseCampaignDto;
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
@RequestMapping("/api/v1/campaigns")
public class CampaignController {
    private final CampaignService campaignService;

    @PostMapping
    public ResponseCampaignDto createCampaign(@RequestBody CampaignCreateDto campaignCreateDto) {
        ResponseCampaignDto createdCampaignDto = campaignService.create(campaignCreateDto);
        log.info("The campaign has been created");
        return createdCampaignDto;
    }

    @PutMapping
    public ResponseCampaignDto updateCampaign(@RequestBody CampaignUpdateDto campaignUpdateDto) {
        ResponseCampaignDto updatedCampaignDto = campaignService.update(campaignUpdateDto);
        log.info("The campaign has been updated");
        return updatedCampaignDto;
    }

    @DeleteMapping("/{campaignId}")
    public void deleteCampaign(@PathVariable Long campaignId) {
        campaignService.delete(campaignId);
        log.info("Campaign has been deleted");
    }

    @GetMapping("/{campaignId}")
    public ResponseCampaignDto getCampaign(@PathVariable Long campaignId) {
        return campaignService.getCampaign(campaignId);
    }

    @GetMapping
    public List<ResponseCampaignDto> getCampaigns(@RequestBody CampaignFilterDto campaignFilterDto) {
        return campaignService.getCampaignsByProject(campaignFilterDto);
    }
}
