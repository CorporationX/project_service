package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFiltersDto;
import faang.school.projectservice.dto.campaign.CampaignUpdateDto;
import faang.school.projectservice.service.CampaignService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/campaign")
public class CampaignController {

    private final CampaignService campaignService;
    private final UserContext userContext;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CampaignDto createCampaign(@RequestBody @Valid CampaignDto campaignDto) {
        return campaignService.createCampaign(campaignDto, userContext.getUserId());
    }

    @PutMapping("/{campaignId}")
    @ResponseStatus(HttpStatus.OK)
    public CampaignDto updateCampaign(@RequestBody @Valid CampaignUpdateDto campaignUpdateDto, @PathVariable long campaignId) {
        return campaignService.updateCampaign(campaignUpdateDto, userContext.getUserId(), campaignId);
    }

    @DeleteMapping("/{campaignId}")
    @ResponseStatus(HttpStatus.OK)
    public void softDeleteCampaign(@PathVariable long campaignId) {
        campaignService.softDeleteCampaign(campaignId, userContext.getUserId());
    }

    @GetMapping("/{campaignId}")
    @ResponseStatus(HttpStatus.OK)
    public CampaignDto getCampaignById(@PathVariable long campaignId) {
        return campaignService.getCampaignDtoById(campaignId);
    }

    @GetMapping("/byFilter")
    @ResponseStatus(HttpStatus.OK)
    public List<CampaignDto> getAllCampaignsByFilter(@RequestBody CampaignFiltersDto campaignFiltersDto) {
        return campaignService.getAllCampaignsByFilter(campaignFiltersDto);
    }
}
