package faang.school.projectservice.controller.campaign;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.service.campaign.CampaignService;
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

@RestController
@RequestMapping("/api/v1/campaign")
@RequiredArgsConstructor
public class CampaignController {
    private final CampaignService campaignService;

    @PostMapping("/campaigns")
    public List<CampaignDto> findAllCampaigns(@RequestBody CampaignFilterDto campaignFilterDto) {
        return campaignService.findAll(campaignFilterDto);
    }

    @PostMapping
    public void createCampaign(@RequestBody @Valid CampaignDto campaignDto) {
        campaignService.createCampaign(campaignDto);
    }

    @GetMapping("/{id}")
    public void findCampaignById(@PathVariable Long id) {
        campaignService.findById(id);
    }

    @PutMapping("/{id}")
    public void updateCampaign(@PathVariable Long id, @RequestBody @Valid CampaignDto campaignDto) {
        campaignService.updateCampaign(campaignDto, id);
    }

    @DeleteMapping("/{id}")
    public void deleteCampaign(@PathVariable Long id) {
        campaignService.deleteCampaign(id);
    }
}
