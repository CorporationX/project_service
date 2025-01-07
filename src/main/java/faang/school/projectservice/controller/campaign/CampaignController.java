package faang.school.projectservice.controller.campaign;

import faang.school.projectservice.dto.campaign.CampaignCreateDto;
import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignUpdateDto;
import faang.school.projectservice.service.campaign.CampaignService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/campaigns")
@RequiredArgsConstructor
public class CampaignController {
    private final CampaignService campaignService;

    @PostMapping
    public CampaignDto createCampaign(@RequestBody @Valid CampaignCreateDto createDto) {
        return campaignService.createCampaign(createDto);
    }

    @PatchMapping("/{id}")
    public CampaignDto updateCampaign(@PathVariable Long id, @RequestBody @Valid CampaignUpdateDto updateDto) {
        return campaignService.updateCampaign(id, updateDto);
    }

    @DeleteMapping("/{id}")
    public CampaignDto closeCampaign(@PathVariable Long id) {
        return campaignService.cancelCampaign(id);
    }

    @GetMapping("/{id}")
    public CampaignDto getById(@PathVariable Long id) {
        return campaignService.getById(id);
    }

}
