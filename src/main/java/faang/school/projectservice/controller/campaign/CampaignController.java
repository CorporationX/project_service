package faang.school.projectservice.controller.campaign;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.dto.campaign.CampaignPublishingDto;
import faang.school.projectservice.dto.campaign.CampaignUpdateDto;
import faang.school.projectservice.service.campaign.CampaignService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;

    @PostMapping
    public CampaignDto publishingCampaign(@RequestBody @Valid CampaignPublishingDto campaignPublishingDto) {
        return campaignService.publishingCampaign(campaignPublishingDto);
    }

    @PutMapping("/{campaignId}")
    public CampaignDto updateCampaign(@PathVariable @NotNull Long campaignId,
                                      @RequestBody CampaignUpdateDto campaignUpdateDto) {
        return campaignService.updateCampaign(campaignId, campaignUpdateDto);
    }

    @DeleteMapping("/{campaignId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCampaignById(@PathVariable @NotNull Long campaignId) {
        campaignService.deleteCampaignById(campaignId);
    }

    @GetMapping("/{campaignId}")
    public CampaignDto getCampaignById(@PathVariable @NotNull Long campaignId) {
        return campaignService.getCampaignById(campaignId);
    }

    @GetMapping("/project/{projectId}")
    public List<CampaignDto> getAllCampaignsByProjectId(@PathVariable @NotNull Long projectId,
                                                        @RequestBody CampaignFilterDto campaignFilterDto) {
        return campaignService.getAllCampaignsByProjectId(projectId, campaignFilterDto);
    }
}
