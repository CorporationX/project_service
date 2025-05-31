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
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class CampaignController {
    private final CampaignService campaignService;

    @PostMapping("/campaigns")
    public List<CampaignDto> findAll(@RequestBody CampaignFilterDto campaignFilterDto) {
       return campaignService.findAll(campaignFilterDto);
    }

    @PostMapping("/campaign")
    public void create(@RequestBody @Valid CampaignDto campaignDto) {
        campaignService.createCampaign(campaignDto);
    }

    @GetMapping("/campaign/{id}")
    public void findById(@PathVariable Long id) {
        campaignService.findById(id);
    }

    @PutMapping("/campaign/{id}")
    public void update(@RequestBody @Valid CampaignDto campaignDto, @PathVariable Long id) {
        campaignService.updateCampaign(campaignDto, id);
    }

    @DeleteMapping ("/campaign/{id}")
    public void delete(@PathVariable Long id) {
        campaignService.deleteCampaign(id);
    }
}
