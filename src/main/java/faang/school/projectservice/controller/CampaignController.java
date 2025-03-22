package faang.school.projectservice.controller;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.dto.campaign.CampaignUpdateDto;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.service.CampaignService;
import faang.school.projectservice.utils.validationsUtils.CampaignValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/campaign")
public class CampaignController {

    private final CampaignService campaignService;

    @PostMapping
    public CampaignDto createCampaign(@RequestBody CampaignDto campaignDto) {
        CampaignValidator.validationCampaignDto(campaignDto);
        log.info("All validation have been verifeied.\nStarting the campaign creation process");
        CampaignDto returnedCampaignDto = campaignService.create(campaignDto);
        log.info("The campaign has been created");
        return returnedCampaignDto;
    }

    @PutMapping
    public CampaignDto updateCampaign(@RequestBody CampaignUpdateDto campaignUpdateDto) {
        CampaignValidator.validateCampaignUpdateDto(campaignUpdateDto);
        log.info("All validation have been verifeied.\nStarting the campaign updating process");
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

    @GetMapping("/{projectId}")
    public List<CampaignDto> getCampaigns(@RequestBody CampaignFilterDto campaignFilterDto) {
        return campaignService.getCampaignsByProject(campaignFilterDto);
    }
}
