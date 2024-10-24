package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.CampaignDto;
import faang.school.projectservice.dto.CampaignUpdateDto;
import faang.school.projectservice.dto.filter.CampaignFilterDto;
import faang.school.projectservice.service.CampaignService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping("api/v1/campaign")
@RequiredArgsConstructor
public class CampaignController {

    private final UserContext userContext;
    private final CampaignService campaignService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CampaignDto publishCampaign(@RequestBody @Validated CampaignDto campaignDto) {
        long userId = userContext.getUserId();
        campaignDto.setCreatorId(userId);
        return campaignService.publishCampaign(campaignDto);
    }

    @PutMapping
    public void updateCampaign(@RequestBody @Validated CampaignUpdateDto campaignDto) {
        long userId = userContext.getUserId();
        if (campaignDto.getUpdatedBy() == null) {
            campaignDto.setUpdatedBy(userId);
        }
        campaignService.updateCampaign(campaignDto);
    }

    @DeleteMapping("/{id}")
    public void deleteCampaign(@PathVariable @Positive Long id) {
        campaignService.deleteCampaign(id);
    }

    @GetMapping("/{id}")
    public CampaignDto getCampaign(@PathVariable @Positive Long id) {
        return campaignService.getCampaign(id);
    }

    @GetMapping
    public List<CampaignDto> getCampaignsSortedByCreatedAtAndByFilter(@RequestBody CampaignFilterDto campaignFilterDto) {
        return campaignService.getCampaignsSortedByCreatedAtAndByFilter(campaignFilterDto);
    }
}
