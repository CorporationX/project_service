package faang.school.projectservice.controller.campaign;

import faang.school.projectservice.model.dto.campaign.CampaignDto;
import faang.school.projectservice.model.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.model.dto.campaign.CampaignUpdateDto;
import faang.school.projectservice.service.CampaignService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/campaigns")
public class CampaignController {

    private final CampaignService campaignService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CampaignDto publishCampaign(@Valid @RequestBody CampaignDto campaignDto){
        return campaignService.publishCampaign(campaignDto);
    }

    @PutMapping
    public CampaignDto updateCampaign(@Valid @RequestBody CampaignUpdateDto campaignUpdateDto){
        return campaignService.updateCampaign(campaignUpdateDto);
    }

    @PutMapping("/{id}")
    public void deleteCampaign(@PathVariable @Positive long id){
        campaignService.deleteCampaign(id);
    }

    @GetMapping("/{id}")
    public CampaignDto getCampaign(@PathVariable @Positive long id){
        return campaignService.getCampaign(id);
    }

    @GetMapping
    public List<CampaignDto> getAllCampaignsOfProject(@RequestBody CampaignFilterDto filters){
        return campaignService.getAllCampaignsByFilter(filters);
    }
}
