package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.CampaignCreateDto;
import faang.school.projectservice.dto.CampaignDto;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.filter.CampaignFilterDto;
import faang.school.projectservice.service.CampaignService;
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

@RestController
@RequestMapping("/campaign")
@RequiredArgsConstructor
public class CampaignController {
    private final UserContext userContext;
    private final CampaignService campaignService;

    @GetMapping("/{id}")
    public CampaignDto get(@PathVariable Long id) {
        return campaignService.getCampaign(id);
    }

    @GetMapping
    public List<CampaignDto> getAll(CampaignFilterDto filter) {
        return campaignService.getAllCampaigns(filter);
    }

    @PostMapping
    public CampaignDto create(@RequestBody CampaignCreateDto campaign) {
        return campaignService.createCampaign(campaign, userContext.getUserId());
    }

    @PutMapping("/{id}")
    public CampaignDto update(@PathVariable("id") Long campaignId, @RequestBody CampaignCreateDto campaign) {
        return campaignService.updateCampaign(campaignId, campaign, userContext.getUserId());
    }

    @PostMapping("/{id}/donate")
    public CampaignDto donateToCampaign(@PathVariable("id") Long campaignId, @RequestBody PaymentRequest request) {
        return campaignService.donateToCampaign(campaignId, request, userContext.getUserId());
    }

    @PostMapping("/{id}/cancel")
    public CampaignDto cancel(@PathVariable("id") Long campaignId) {
        return campaignService.cancelCampaign(campaignId, userContext.getUserId());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long campaignId) {
        campaignService.deleteCampaign(campaignId, userContext.getUserId());
    }
}
